package com.example.myfirstapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class NewPostActivity extends Activity {

  private final static String CREATE_TASK_ENDPOINT_URL = "http://192.168.0.74:3000/api/v1/posts.json";
  private SharedPreferences mPreferences;
  private String mPostTitle;
  private String mPostDescription;
  private String mPostCategory;
  
  private Spinner category;
  

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_post);
    
    addItemsOnSpinner();

    mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
  }

  public void saveTask(View button) {
    EditText postTitlelField = (EditText) findViewById(R.id.postTitle);
    mPostTitle = postTitlelField.getText().toString();
    EditText postDescriptionField = (EditText) findViewById(R.id.postDescription);
    mPostDescription = postDescriptionField.getText().toString();
    //http://www.mkyong.com/android/android-spinner-drop-down-list-example/ ���ǳ� (select item)
    Spinner postCategoryField = (Spinner) findViewById(R.id.postCategory);
    mPostCategory = String.valueOf(postCategoryField.getSelectedItem());

    if (mPostTitle.length() == 0) {
      // input fields are empty
      Toast.makeText(this, "Please write something as a title for this task",
          Toast.LENGTH_LONG).show();
      return;
    } else {
      // everything is ok!
      CreateTaskTask createTask = new CreateTaskTask(NewPostActivity.this);
      createTask.setMessageLoading("Creating new task...");
      createTask.execute(CREATE_TASK_ENDPOINT_URL);
    }
  }

  private class CreateTaskTask extends UrlJsonAsyncTask {
    public CreateTaskTask(Context context) {
      super(context);
    }

    @Override
    protected JSONObject doInBackground(String... urls) {
      DefaultHttpClient client = new DefaultHttpClient();
      HttpPost post = new HttpPost(urls[0]);
      JSONObject holder = new JSONObject();
      JSONObject taskObj = new JSONObject();
      String response = null;
      JSONObject json = new JSONObject();

      try {
        try {
          json.put("success", false);
          json.put("info", "Something went wrong. Retry!");
          taskObj.put("title", mPostTitle);
          taskObj.put("category", mPostCategory);
          taskObj.put("description", mPostDescription);
          holder.put("post", taskObj);
          StringEntity se = new StringEntity(holder.toString(), "utf-8");
          post.setEntity(se);
          post.setHeader("Accept", "application/json");
          post.setHeader("Content-Type", "application/json");
          post.setHeader("Authorization", "Token token=" + mPreferences.getString("AuthToken", ""));

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
          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
          startActivity(intent);
          finish();
          }
          Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
      } finally {
        super.onPostExecute(json);
      }
    }
  }
  
  public void addItemsOnSpinner(){
	  category = (Spinner) findViewById(R.id.postCategory);
	  List<String> list = new ArrayList<String>();
	  list.add("판매");
	  list.add("구매");
	  ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
	  categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	  category.setAdapter(categoryAdapter);
  }
  
  public void addListenerOnSpinnerItemSelection(){
	  category = (Spinner) findViewById(R.id.postCategory);
	  category.setOnItemSelectedListener(new CustomOnItemSelectedListener());
  }
}