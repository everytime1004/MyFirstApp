package com.example.myfirstapp;

import java.util.logging.Logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class CloseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_close);
		// http://blog.naver.com/lion_kwon?Redirect=Log&logNo=40152177060
		if(getIntent().getBooleanExtra("close", false)){
			finish();
		}else if(getIntent().getBooleanExtra("close_signin", false)){
			Intent intent_close_real = new Intent(this, MainActivity.class);
			intent_close_real.putExtra("close_signin", true);
			intent_close_real.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent_close_real);
		}
		else{
			Intent intent_back = new Intent(this, MainActivity.class);
			startActivity(intent_back);
			// http://blog.naver.com/jaejae1988?Redirect=Log&logNo=60180955913
			// overridePendingTransition() 이용해 보기
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_close, menu);
		return true;
	}

}
