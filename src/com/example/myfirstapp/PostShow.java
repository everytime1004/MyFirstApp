package com.example.myfirstapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class PostShow extends Activity {

	private static int mPostId = 0;

	private static String SHOW_TASK_ENDPOINT_URL;

	private SharedPreferences mPreferences;

	int mImageWidth = 0;
	int mImageHeight = 0;
	int newImageWidth = 400;
	int newImageHeight = 400;
	float scaleWidth = 0;
	float scaleHeight = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_show);

		TextView task_show_title = (TextView) findViewById(R.id.task_show_title);
		TextView task_show_description = (TextView) findViewById(R.id.task_show_description);

		Intent taskIntent = getIntent();

		task_show_title.setText(taskIntent.getStringExtra("title"));
		task_show_description.setText(taskIntent.getStringExtra("description"));
		mPostId = taskIntent.getIntExtra("post_id", 0);

		SHOW_TASK_ENDPOINT_URL = "http://" + ServerIp.IP + "/api/v1/posts/"
				+ mPostId + ".json";

		ShowTaskTask showTask = new ShowTaskTask(PostShow.this);
		showTask.setMessageLoading("Loading task...");
		showTask.execute(SHOW_TASK_ENDPOINT_URL);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	private class ShowTaskTask extends UrlJsonAsyncTask {
		public ShowTaskTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet(urls[0]);
			String response = null;
			JSONObject json = new JSONObject();

			try {
				try {
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");

					post.setHeader("Accept", "application/json");
					post.setHeader("Content-Type", "application/json");
					post.setHeader("Authorization", "Token token="
							+ mPreferences.getString("AuthToken", ""));

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
					JSONArray jsonTasks = json.getJSONObject("data")
							.getJSONArray("image");
					int length = jsonTasks.length();
					for (int i = 0; i < length; i++) {
						String image = jsonTasks.getString(i);
						
						String imageData = new String(Base64.decode(image, Base64.DEFAULT));
						
						 byte[] imageBytes = Convert.FromBase64String(base64String);

						Bitmap imageBitmap =  BitmapFactory.decodeStream(bis);

						ImageView showImage1 = (ImageView) findViewById(R.id.showImage1);

						showImage1.setImageResource(0);

						mImageWidth = imageBitmap.getWidth();
						mImageHeight = imageBitmap.getHeight();

						scaleWidth = ((float) newImageWidth) / mImageWidth;
						scaleHeight = ((float) newImageHeight) / mImageHeight;

						Matrix matrix = new Matrix();
						matrix.postScale(scaleWidth, scaleHeight);

						Bitmap resizedBitmap = Bitmap.createBitmap(imageBitmap,
								0, 0, mImageWidth, mImageHeight, matrix, true);

						showImage1.setImageBitmap(resizedBitmap);
						showImage1.setVisibility(View.VISIBLE);
					}
				}
				Toast.makeText(context, json.getString("info"),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_show, menu);
		return true;
	}

}
