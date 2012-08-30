package net.virifi.android.autostartapp;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView textView = (TextView) findViewById(R.id.textview);
        
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int wifiState = wifiManager.getWifiState();
        
        switch (wifiState) {
        case WifiManager.WIFI_STATE_DISABLING:
        	textView.setText("wifiを無効にしています");
        	break;
        case WifiManager.WIFI_STATE_DISABLED:
        	textView.setText("wifiは無効です");
        	break;
        case WifiManager.WIFI_STATE_ENABLED:
        	textView.setText("wifiは有効です");
        	break;
        case WifiManager.WIFI_STATE_ENABLING:
        	textView.setText("wifiを有効にしています");
        	break;
        case WifiManager.WIFI_STATE_UNKNOWN:
        	textView.setText("現在のwifiの状態は不明です");
        	break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
