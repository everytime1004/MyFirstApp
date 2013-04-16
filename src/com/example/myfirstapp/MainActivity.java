package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.savagelook.android.UrlJsonAsyncTask;

public class MainActivity extends Activity{
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.Message";

	private static final String TASKS_URL = "http://192.168.0.74:3000/api/v1/posts.json";
	private static final String GCM_URL = "http://192.168.0.74:3000/api/v1/gcms.json";
	
	private SharedPreferences mPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(getIntent().getBooleanExtra("close_signin", false)){
			finish();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setDisplayHomeAsUpEnabled(true);
			ActionBar actionBar = getActionBar();
			// actionbar setting
	   		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
	   				| ActionBar.NAVIGATION_MODE_STANDARD | ActionBar.DISPLAY_HOME_AS_UP
	   				| ActionBar.DISPLAY_SHOW_HOME);
		}
		
		loadGCMSendIdToServer(GCM_URL);
		
		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}
	
	@Override
	public void onResume() {
	    super.onResume();

	    if ( mPreferences.contains("AuthToken")) {
	        loadTasksFromAPI(TASKS_URL);
	    } else {
	    	Intent intent = new Intent(MainActivity.this, SignInActivity.class);
	    	// http://blog.naver.com/PostView.nhn?blogId=hisukdory&logNo=50088038280
	        startActivityForResult(intent, 0);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater menuInflater = getMenuInflater(); 
		menuInflater.inflate(R.menu.activity_main, menu);
		if ( mPreferences.contains("AuthToken")) {
			menu.findItem(R.id.action_logout).setVisible(true);
	   		menu.findItem(R.id.action_login).setVisible(false);
	    }
		return true;
	}

//	public void sendMessage(View view){
//		Intent intent = new Intent(this, MakeScheduleActivity.class);
//		EditText editText = (EditText) findViewById(R.id.edit_message);
//		String message = editText.getText().toString();
//		intent.putExtra(EXTRA_MESSAGE,  message);
//		startActivity(intent);
//	}
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_login:
	    	Intent loginIntent = new Intent(this, SignInActivity.class);
			startActivity(loginIntent);
			break;
			
	    case R.id.action_logout:
	    	SharedPreferences.Editor editor = mPreferences.edit();
	        // save the returned auth_token into
	        // the SharedPreferences
	        editor.clear();
	        editor.commit();
	        Intent logoutIntent = new Intent(this, SignInActivity.class);
	        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(logoutIntent);
			break;
	      
	    case R.id.action_MakeSchedule:
	    	Intent schdeduleIntent = new Intent(this, MakeScheduleActivity.class);
			startActivity(schdeduleIntent);
	    	break;
	    	
	    case R.id.menu_new_task:
	        Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
	        startActivityForResult(intent, 0);
	        return true;
	    	
	    case R.id.action_close:
	    	final Context context = this;
	    	Builder d = new AlertDialog.Builder(this);
			d.setMessage("정말 종료하시겠습니까?");
			d.setPositiveButton("예", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// process 전체 종료
					Intent intent_close = new Intent(context, CloseActivity.class);
					intent_close.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent_close.putExtra("close", true);
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
	
	private void loadGCMSendIdToServer(String url) {
		GCMSendIdToServer sendIdToServer = new GCMSendIdToServer(MainActivity.this);
		sendIdToServer.setMessageLoading("Loading ...");
		sendIdToServer.execute(url);
	}

	private void loadTasksFromAPI(String url) {
	    GetTasksTask getTasksTask = new GetTasksTask(MainActivity.this);
	    getTasksTask.setMessageLoading("Loading tasks...");
	    getTasksTask.setAuthToken(mPreferences.getString("AuthToken", ""));
	    getTasksTask.execute(url);
	}

	private class GetTasksTask extends UrlJsonAsyncTask {
		  public GetTasksTask(Context context) {
		    super(context);
		  }

		  @Override
		    protected void onPostExecute(JSONObject json) {
		      try {
		        JSONArray jsonTasks = json.getJSONObject("data").getJSONArray("posts");
		        JSONObject jsonTask = new JSONObject();
		        int length = jsonTasks.length();
		        final ArrayList<Post> tasksArray = new ArrayList<Post>(length);

		        for (int i = 0; i < length; i++) {
		          jsonTask = jsonTasks.getJSONObject(i);
		          tasksArray.add(new Post(jsonTask.getLong("id"), jsonTask.getString("title"), jsonTask.getString("category"), jsonTask.getString("description")));
		        }

		        ListView tasksListView = (ListView) findViewById (R.id.tasks_list_view);
		        if (tasksListView != null) {
		          tasksListView.setAdapter(new PostAdapter(MainActivity.this,
		              android.R.layout.simple_list_item_1, tasksArray));
		        }
		      } catch (Exception e) {
		      Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		    } finally {
		      super.onPostExecute(json);
		    }
		  }
		}
	
	@Override
	public void onBackPressed() {
		// 뒤로 버튼 눌렀을 때 home으로 이동
	    Intent startMain = new Intent(Intent.ACTION_MAIN);
	    startMain.addCategory(Intent.CATEGORY_HOME);
	    // http://blog.naver.com/cruel8498?Redirect=Log&logNo=130161744612
	    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(startMain);
	    finish();
	}
	
	private class PostAdapter extends ArrayAdapter<Post>{

		  private ArrayList<Post> items;
		  private int layoutResourceId;

		  public PostAdapter(Context context, int layoutResourceId, ArrayList<Post> items) {
		    super(context, layoutResourceId, items);
		    this.layoutResourceId = layoutResourceId;
		    this.items = items;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    View view = convertView;
		      if (view == null) {
		        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        view = (TextView) layoutInflater.inflate(layoutResourceId, null);
		      }
		      Post task = items.get(position);
		      if (task != null) {
		        TextView postTextView = (TextView) view.findViewById(android.R.id.text1);
		          if (postTextView != null) {
		        	postTextView.setText(task.getCategory());
			        postTextView.setText(task.getDescription());  
		            postTextView.setText(task.getTitle());
		          }
		          view.setTag(task.getId());
		      }
		      return view;
		  }
	}
}
