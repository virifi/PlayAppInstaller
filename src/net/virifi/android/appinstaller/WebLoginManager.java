package net.virifi.android.appinstaller;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class WebLoginManager implements AccountManagerCallback<Bundle> {
	private final Activity mActivity;
	private final WebView mWebView;
	private final AccountManager mAccountManager;
	
	private String mCurrentAccountType;
	
	private String mLastAuthToken;
		
	WebLoginManager(Activity activity, WebView webView) {
		mActivity = activity;
		mWebView = webView;
		mAccountManager = AccountManager.get(activity);
	}
	
	public void handleLogin(Account account, String accountType, String tokenType) {
		mCurrentAccountType = accountType;
		String authTokenType = "weblogin:" + tokenType;
		mAccountManager.getAuthToken(account, authTokenType, null, mActivity, this, null);
	}
	
	public void invalidateAuthToken() {
		mAccountManager.invalidateAuthToken(mCurrentAccountType, mLastAuthToken);
	}

	@Override
	public void run(AccountManagerFuture<Bundle> value) {
		try {
			String result = value.getResult().getString(AccountManager.KEY_AUTHTOKEN);
			if (result == null) {
				loginFailed();
			} else {
				mLastAuthToken = result;
				mWebView.loadUrl(result);
			}
		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loginFailed() {
		Toast.makeText(mActivity, "Login failed", Toast.LENGTH_SHORT).show();
	}
}
