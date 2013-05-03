package com.example.myfirstapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;


public class SettingActivity extends Activity {
	
	private SharedPreferences mPreferences;
	private static final String GCM_URL = "http://"+ServerIp.IP+"/api/v1/gcms.json";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}
	
	public void saveSettings(View button) {
		CheckBox notyCb = (CheckBox) findViewById(R.id.notyCb);
		if(notyCb.isChecked()){
			SharedPreferences.Editor editor = mPreferences.edit();
	        // save the returned auth_token into
	        // the SharedPreferences
	        editor.putBoolean("noty", true);
	        editor.commit();
		}else{
			SharedPreferences.Editor editor = mPreferences.edit();
	        // save the returned auth_token into
	        // the SharedPreferences
	        editor.putBoolean("noty", false);
	        editor.commit();
		}
		
		loadGCMSendIdToServer(GCM_URL);
		Toast.makeText(this, "저장 완료",  2000).show();
		
	}
	
	private void loadGCMSendIdToServer(String url) {
		GCMSendIdToServer sendIdToServer = new GCMSendIdToServer(SettingActivity.this);
		sendIdToServer.setMessageLoading("Loading ...");
		sendIdToServer.execute(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		return true;
	}

}
