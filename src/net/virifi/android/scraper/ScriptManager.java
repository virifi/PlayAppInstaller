package net.virifi.android.scraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.webkit.WebView;

public class ScriptManager {
	Context mContext;
	WebView mWebView;
	List<Script> mCommonScripts;
	List<Script> mUserScripts;

	
	ScriptManager(Context context, WebView webView) {
		mContext = context;
		mWebView = webView;
		mCommonScripts = new ArrayList<Script>();
		mUserScripts = new ArrayList<Script>();
	}
	
	public void addCommonScript(String name) {
		mCommonScripts.add(new Script(name));
	}
	
	public void setUserScripts(String... names) {
		mUserScripts.clear();
		for (String name : names) {
			mUserScripts.add(new Script(name));
		}
	}
	
	public void loadNext() throws IOException {
		loadScripts(mCommonScripts);
		loadScripts(mUserScripts);
		clearLoaded();
	}

	private void loadScripts(List<Script> scripts) throws IOException {
		for (Script script : scripts) {
			if (script.isLoaded())
				continue;
			
			String scriptStr = script.loadScript();
			script.setLoaded(true);
			mWebView.loadUrl(scriptStr);
		}
	}
	
	public void clearLoaded() {
		for (Script script : mCommonScripts) {
			script.setLoaded(false);
		}
		for (Script script : mUserScripts) {
			script.setLoaded(false);
		}
	}
	
	
	private class Script {
		String mScriptName;
		boolean mLoaded = false;
		
		Script(String name) {
			mScriptName = name;
		}
		
		public boolean isLoaded() {
			return mLoaded;
		}
		
		public void setLoaded(boolean loaded) {
			mLoaded = loaded;
		}
		

		public String loadScript() throws IOException {
			StringBuilder sb = new StringBuilder("javascript:");
			InputStream is = null;
			BufferedReader br = null;
			try {
				is = mContext.getResources().getAssets().open(mScriptName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException(mScriptName + " : failed to load");
			}
			br = new BufferedReader(new InputStreamReader(is));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				throw new IOException(mScriptName + " : failed to load");
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return sb.toString();
		}
	}
}
