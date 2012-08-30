package net.virifi.android.appinstaller;

import net.virifi.android.scraper.ScrapingFragment;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class AppInstallFragment extends ScrapingFragment {
	private WebLoginManager mWebLoginManager;
	private boolean mRetry = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return mWebView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mWebLoginManager = new WebLoginManager(activity, mWebView);
	}
	
	public void loginWithMainAccount() {
		AccountManager mgr = AccountManager.get(getActivity());
		Account[] accounts = mgr.getAccountsByType("com.google");
		if (accounts == null || accounts.length == 0) {
			Toast.makeText(getActivity(), "google accounts not found", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Account account = accounts[0];
		Toast.makeText(getActivity(), "Trying to login with " + account.name, Toast.LENGTH_SHORT).show();
		mWebLoginManager.handleLogin(account, "com.google", "service=googleplay&continue=https%3A%2F%2Fplay.google.com%2Fstore");
	}
	
	
	public void setVisible(boolean visible) {
		if (visible)
			mWebView.setVisibility(View.VISIBLE);
		else
			mWebView.setVisibility(View.INVISIBLE);
	}
	
	// run on UI Thread
	@Override
	public void onScrapingFinished(final String message) {
		mRetry = false;
		super.onScrapingFinished(message);
	}
	
	// run on UI Thread
	@Override
	public void onScrapingError(final String message) {
		if (message != null && message.equals("login_error") && !mRetry) {
			mRetry = true;
			mWebLoginManager.invalidateAuthToken();
			loginWithMainAccount();
			return;
		}
		mRetry = false;
		super.onScrapingError(message);
	}
}
