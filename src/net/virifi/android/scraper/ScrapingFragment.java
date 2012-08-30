package net.virifi.android.scraper;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ScrapingFragment extends Fragment {
	private static final String TAG = "ScrapingFragment";
	private Bundle mResultBundle;
	private Bundle mTempBundle;
	private boolean mCancel = false;
		
	protected WebView mWebView;
	protected ScriptManager mScriptManager;
	
	OnStateChangedListener mListener;

	public interface OnStateChangedListener {
		public void onPageStepChanged(int maxSteps, int currentStep);

		public void onScrapingFinished(String message, Bundle results);

		public void onScrapingError(String message);
		
		public void onProgressMessage(String message);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof OnStateChangedListener == false) {
			throw new ClassCastException(
					"activity does not implements OnStateChangedListener");
		}
		mListener = (OnStateChangedListener) activity;
		
		mResultBundle = new Bundle();
		mTempBundle = new Bundle();

		mWebView = new WebView(activity);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.setHorizontalScrollBarEnabled(false);
		//mWebView.getSettings().setLoadsImagesAutomatically(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new WebViewInterface(), "__webViewInterface");
		
		mScriptManager = new ScriptManager(getActivity(), mWebView);
		mScriptManager.addCommonScript("jquery-1.7.2.min.js");
		mScriptManager.addCommonScript("async.min.js");
		mScriptManager.addCommonScript("js_scraper.js");
		
		String[] scripts = getArguments().getStringArray("scripts");
		if (scripts != null) {
			mScriptManager.setUserScripts(scripts);
		} else {
			String script = getArguments().getString("script");
			if (script != null) {
				mScriptManager.setUserScripts(script);
			}
		}
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				try {
					mScriptManager.loadNext();
				} catch (IOException e) {
					e.printStackTrace();
					mListener.onScrapingError(e.getMessage());
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
	}
	
	public void loadUrl(String url) {
		mWebView.loadUrl(url);
	}
	
	// run on UI Thread
	public void onScrapingError(final String message) {
		mListener.onScrapingError(message);
	}
	
	// run on UI Thread
	public void onScrapingFinished(final String message) {
		mListener.onScrapingFinished(message, mResultBundle);
	}

	private class WebViewInterface {
		public String getBundleString(String key) {
			return getArguments().getString(key);
		}
		
		public void setResultString(String key, String value) {
			mResultBundle.putString(key, value);
		}
		
		public void putTempString(String key, String value) {
			mTempBundle.putString(key, value);
		}
		public String getTempString(String key) {
			return mTempBundle.getString(key);
		}

		public void prepare() {
			mScriptManager.clearLoaded();
		}
		
		public void onProgressMessage(final String message) {
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					mListener.onProgressMessage(message);
				}
			});
		}
		
		public void onPageStepChanged(final int maxStep, final int currentStep) {
			getActivity().runOnUiThread(new Runnable() {	
				@Override
				public void run() {
					mListener.onPageStepChanged(maxStep, currentStep);
				}
			});
		}
		
		public void onScrapingError(final String message) {
			getActivity().runOnUiThread(new Runnable() {			
				@Override
				public void run() {
					ScrapingFragment.this.onScrapingError(message);
				}
			});
		}
		
		public void onScrapingFinished(final String message) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ScrapingFragment.this.onScrapingFinished(message);
				}
			});
		}

		public void log(final String message) {
			Log.d(TAG, message);
		}
		
		public void clickElement(final int window_width, final int window_height, final int left, final int top, final int width, final int height) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int webViewWidth = mWebView.getWidth();
					int webViewHeight = mWebView.getHeight();
					float elementMiddleX = left + (width / 2f);
					float elementMiddleY = top + (height / 2f);
					int clickX = (int) (webViewWidth * elementMiddleX / window_width);
					int clickY = (int) (webViewHeight * elementMiddleY / window_height);
					click(clickX, clickY);
				}
			});
		}
	}
	
	public void click(final int x, final int y)	 {
		Log.d("ScrapingFragment", "clickX = " + String.valueOf(x) + " clickY = " + String.valueOf(y));
		MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
		mWebView.onTouchEvent(evt);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
				mWebView.onTouchEvent(evt);
			}
		}, 300);
	}
}
