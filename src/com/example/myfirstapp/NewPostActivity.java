package com.example.myfirstapp;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class NewPostActivity extends Activity {

	ImageView targetImage;

	private final static String CREATE_TASK_ENDPOINT_URL = "http://"
			+ ServerIp.IP + "/api/v1/posts.json";
	private SharedPreferences mPreferences;
	private String mPostTitle;
	private String mPostDescription;
	private String mPostCategory;

	private Spinner category;

	int mImageWidth = 0;
	int mImageHeight = 0;
	int newImageWidth = 400;
	int newImageHeight = 400;
	float scaleWidth = 0;
	float scaleHeight = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_post);

		addItemsOnSpinner();

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Uri targetUri = data.getData();
			Bitmap bitmap;
			try {
				bitmap = BitmapFactory.decodeStream(getContentResolver()
						.openInputStream(targetUri));
				targetImage.setImageResource(0);

				mImageWidth = bitmap.getWidth();
				mImageHeight = bitmap.getHeight();

				scaleWidth = ((float) newImageWidth) / mImageWidth;
				scaleHeight = ((float) newImageHeight) / mImageHeight;

				Matrix matrix = new Matrix();
				matrix.postScale(scaleWidth, scaleHeight);

				Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
						mImageWidth, mImageHeight, matrix, true);

				targetImage.setImageBitmap(resizedBitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean imageListener(View image) {
		switch (image.getId()) {
		case R.id.targetImage1:
			targetImage = (ImageView) findViewById(R.id.targetImage1);
			getImageFromGallery();
			break;
		case R.id.targetImage2:
			targetImage = (ImageView) findViewById(R.id.targetImage2);
			getImageFromGallery();
			break;
		case R.id.targetImage3:
			targetImage = (ImageView) findViewById(R.id.targetImage3);
			getImageFromGallery();
			break;
		case R.id.targetImage4:
			targetImage = (ImageView) findViewById(R.id.targetImage4);
			getImageFromGallery();
			break;
		case R.id.targetImage5:
			targetImage = (ImageView) findViewById(R.id.targetImage5);
			getImageFromGallery();
			break;
		}

		return true;
	}

	public void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 0);
	}

	public void saveTask(View button) {
		EditText postTitlelField = (EditText) findViewById(R.id.postTitle);
		mPostTitle = postTitlelField.getText().toString();
		EditText postDescriptionField = (EditText) findViewById(R.id.postDescription);
		mPostDescription = postDescriptionField.getText().toString();
		// http://www.mkyong.com/android/android-spinner-drop-down-list-example/
		// ���ǳ� (select item)
		Spinner postCategoryField = (Spinner) findViewById(R.id.postCategory);
		mPostCategory = String.valueOf(postCategoryField.getSelectedItem());

		if (mPostTitle.length() == 0) {
			// input fields are empty
			Toast.makeText(this,
					"Please write something as a title for this task",
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
			
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
			targetImage = (ImageView) findViewById(R.id.targetImage1);
			BitmapDrawable drawable = (BitmapDrawable) targetImage.getDrawable();
			Bitmap imageBitmap = drawable.getBitmap();
			imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageStream);
            byte[] data = imageStream.toByteArray();
            String imageData = new String(Base64.encode(data, 1));

			try {
				try {
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");
					taskObj.put("title", mPostTitle);
					taskObj.put("category", mPostCategory);
					taskObj.put("description", mPostDescription);
					taskObj.put("image",  imageData);
					holder.put("post", taskObj);
					StringEntity se = new StringEntity(holder.toString(),
							"utf-8");
					post.setEntity(se);
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
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(intent);
					finish();
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

	public void addItemsOnSpinner() {
		category = (Spinner) findViewById(R.id.postCategory);
		List<String> list = new ArrayList<String>();
		list.add("팝니다");
		list.add("삽니다");
		list.add("판매 완료");
		list.add("문의");
		list.add("견적 의뢰");
		ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		categoryAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category.setAdapter(categoryAdapter);
	}

	public void addListenerOnSpinnerItemSelection() {
		category = (Spinner) findViewById(R.id.postCategory);
		category.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}
}