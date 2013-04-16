package com.example.myfirstapp;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class SignInActivity extends Activity {

	private final static String LOGIN_API_ENDPOINT_URL = "http://192.168.0.74:3000/api/v1/sessions.json";
	private SharedPreferences mPreferences;
	private String mUserName;
	private String mUserPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setDisplayHomeAsUpEnabled(true);
			ActionBar actionBar_signin = getActionBar();
			// actionbar setting
	   		actionBar_signin.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
	   				| ActionBar.NAVIGATION_MODE_STANDARD | ActionBar.DISPLAY_HOME_AS_UP
	   				| ActionBar.DISPLAY_SHOW_HOME);
		}
		
		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater menuInflater = getMenuInflater(); 
		menuInflater.inflate(R.menu.activity_sign_in, menu);
		return true;
	}


	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_Register:
	    	Intent registerIntent = new Intent(this, RegisterActivity.class);
			startActivity(registerIntent);
			break;
	    case R.id.action_close:
	    	final Context context = this;
	    	Builder d = new AlertDialog.Builder(this);
			d.setMessage("정말 종료하시겠습니까?");
			d.setPositiveButton("예", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// process 전체 종료
					Intent intent_close = new Intent(context, CloseActivity.class);
					intent_close.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent_close.putExtra("close_signin", true);
					context.startActivity(intent_close);
					((Activity) context).finish();
					dialog.dismiss();
				}
			});
			d.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			d.show();
			break;
	    }

	    return true;
	  }
	
	
	public void login(View button) {
	    EditText userEmailField = (EditText) findViewById(R.id.userEmail);
	    mUserName = userEmailField.getText().toString();
	    EditText userPasswordField = (EditText) findViewById(R.id.userPassword);
	    mUserPassword = userPasswordField.getText().toString();

	    if (mUserName.length() == 0 || mUserPassword.length() == 0) {
	        // input fields are empty
	        Toast.makeText(this, "Please complete all the fields",
	            Toast.LENGTH_LONG).show();
	        return;
	    } else {
	        LoginTask loginTask = new LoginTask(SignInActivity.this);
	        loginTask.setMessageLoading("Logging in...");
	        loginTask.setAuthToken(mPreferences.getString("AuthToken", ""));
	        loginTask.execute(LOGIN_API_ENDPOINT_URL);
	    }
	}
	
	private class LoginTask extends UrlJsonAsyncTask {
	    public LoginTask(Context context) {
	        super(context);
	    }

	    @Override
	    protected JSONObject doInBackground(String... urls) {
// #doInBackground 의 리턴값은 onPostExecute함수의 인자로 보내진다. doInBackgroud함수에서 어느때든 publishProgress()를 호출하여 UI Thread에서 onProgressUpdate() 함수가 실행되게 할 수 있다. 
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(urls[0]);
	        JSONObject holder = new JSONObject();
	        JSONObject userObj = new JSONObject();
	        String response = null;
	        JSONObject json = new JSONObject();

	        try {
	            try {
	                // setup the returned values in case
	                // something goes wrong
	                json.put("success", false);
	                json.put("info", "Something went wrong. Retry!");
	                // add the user email and password to
	                // the params
	                userObj.put("name", mUserName);
	                userObj.put("password", mUserPassword);
	                holder.put("user", userObj);
	                // http://rootnode.tistory.com/entry/StringEntity 한글 꺠질 때
	                StringEntity se = new StringEntity(holder.toString());
	                post.setEntity(se);

	                // setup the request headers
	                post.setHeader("Accept", "application/json");
	                post.setHeader("Content-Type", "application/json");

	                ResponseHandler<String> responseHandler = new BasicResponseHandler();
	                response = client.execute(post, responseHandler);
	                json = new JSONObject(response);

	            } catch (HttpResponseException e) {
	                e.printStackTrace();
	                Log.e("ClientProtocol", "" + e);
	                json.put("info", "UserName and/or password are invalid. Retry!");
	            } catch (IOException e) {
	                e.printStackTrace();
	                Log.e("IO", "" + e);
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	            Log.e("JSON", "" + e);
	        }

	        return json;
	    }

	    @Override
	    protected void onPostExecute(JSONObject json) {
	        try {
	            if (json.getBoolean("success")) {
	                // everything is ok
	                SharedPreferences.Editor editor = mPreferences.edit();
	                // save the returned auth_token into
	                // the SharedPreferences
	                editor.putString("AuthToken", json.getJSONObject("data").getString("auth_token"));
	                editor.commit();

	                // launch the HomeActivity and close this one
	                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
	                startActivity(intent);
	                finish();
	            }
	            Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
	        } catch (Exception e) {
	            // something went wrong: show a Toast
	            // with the exception message
	            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}

}
