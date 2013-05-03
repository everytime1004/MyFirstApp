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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.savagelook.android.UrlJsonAsyncTask;

public class GCMSendIdToServer extends UrlJsonAsyncTask {
	private SharedPreferences mPreferences;
	
    public GCMSendIdToServer(Context context) {
      super(context);
      
      mPreferences = context.getSharedPreferences("CurrentUser", context.MODE_PRIVATE);
      
      GCMRegistrar.checkDevice(context);
      GCMRegistrar.checkManifest(context);
      final String regId = GCMRegistrar.getRegistrationId(context);
      if (regId.equals("")) {
    	  GCMRegistrar.register(context, "180594026587");
      } else {
    	  Log.v("[GCM]", "Already registered" + regId);
      }
      SharedPreferences.Editor editor = mPreferences.edit();
      // save the returned auth_token into
      // the SharedPreferences
	  editor.putString("regid", regId);
	  editor.commit();
    }

    @Override
    protected JSONObject doInBackground(String... urls) {
      DefaultHttpClient client = new DefaultHttpClient();
      HttpPost post = new HttpPost(urls[0]);
      JSONObject holder = new JSONObject();
      JSONObject taskObj = new JSONObject();
      String response = null;
      JSONObject json = new JSONObject();
      final String regId = GCMRegistrar.getRegistrationId(context);

      try {
        try {
          json.put("success", false);
          json.put("info", "Something went wrong. Retry!");
          taskObj.put("reg_id", mPreferences.getString("regid", "X"));
          taskObj.put("noty", mPreferences.getBoolean("noty", true));
          taskObj.put("userName", mPreferences.getString("UserName", "X"));
          
          holder.put("gcm", taskObj);
          StringEntity se = new StringEntity(holder.toString(), "utf-8");
          post.setEntity(se);
          post.setHeader("Accept", "application/json");
          post.setHeader("Content-Type", "application/json");

          ResponseHandler<String> responseHandler = new BasicResponseHandler();
          response = client.execute(post, responseHandler);
          json = new JSONObject(response);

        } catch (HttpResponseException e) {
          e.printStackTrace();
          Log.e("ClientProtocol", "" + e);
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
          }
          Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
      } finally {
        super.onPostExecute(json);
      }
    }
  }