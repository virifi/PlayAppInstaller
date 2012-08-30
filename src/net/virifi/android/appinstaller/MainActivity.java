package net.virifi.android.appinstaller;

import net.virifi.android.scraper.ScrapingFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ScrapingFragment.OnStateChangedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		FragmentManager manager = getSupportFragmentManager();
		final AppInstallFragment fragment = new AppInstallFragment();
		Bundle bundle = new Bundle();
		bundle.putString("script", "install_play_store_apps.js");
		bundle.putString("app_package_name", "net.virifi.android.autostartapp");
		fragment.setArguments(bundle);
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.container, fragment, "APP_INSTALL_FRAGMENT");
		transaction.commit();
        
    	final Button installButton = (Button) findViewById(R.id.install_button);
		installButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//fragment.setVisible(false);
				fragment.loginWithMainAccount();
			}
		});
		
		final Button launchButton = (Button) findViewById(R.id.launch_button);
		launchButton.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				launchApp();
			}
		});
    }
    
    private void launchApp() {
		Intent intent = new Intent();
		intent.setClassName("net.virifi.android.autostartapp", "net.virifi.android.autostartapp.MainActivity");
		//startService(intent);
		startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public void onPageStepChanged(int maxSteps, int currentStep) {
		
	}

	@Override
	public void onScrapingFinished(String message, Bundle results) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				launchApp();
			}
			
		}, 20000);
		Toast.makeText(this, "Wait for 20 seconds", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onScrapingError(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProgressMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}    
}
