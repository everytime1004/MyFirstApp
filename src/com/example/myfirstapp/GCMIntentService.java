package com.example.myfirstapp;

import java.util.Iterator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

//GCM

	public class GCMIntentService extends GCMBaseIntentService {
	    private static final String tag = "GCMIntentService";
	    private static final String PROJECT_ID = "180594026587";
	    //구글 api 페이지 주소 [https://code.google.com/apis/console/#project:긴 번호]
	   //#project: 이후의 숫자가 위의 PROJECT_ID 값에 해당한다
	   
	    //public 기본 생성자를 무조건 만들어야 한다.
	    public GCMIntentService(){ this(PROJECT_ID); }
	   
	    public GCMIntentService(String project_id) { super(project_id); }
	 
	    /** 푸시로 받은 메시지 */
	    @Override
	    protected void onMessage(Context context, Intent intent) {
	        Bundle b = intent.getExtras();
	        // b = Bundle[{score=asdf, collapse_key=updated_score, from=180594026587}]
	        
	        Iterator<String> iterator = b.keySet().iterator();
	        // iterator = java.util.HashMap$KeyIterator@422c41a0 이렇게 넘어옴
	        while(iterator.hasNext()) {
	            String key = iterator.next();
	            // 첫 번 째 key값은 score
	            String value = b.get(key).toString();
	            Log.d(tag, "onMessage. "+key+" : "+value);
	        }
	        
	        Iterator<String> iterator_message = b.keySet().iterator();
	        generateNotification(context, (String) b.get(iterator_message.next().toString()));
	    }

	    /**에러 발생시*/
	    @Override
	    protected void onError(Context context, String errorId) {
	        Log.d(tag, "on_error. errorId : "+errorId);
	    }
	 
	    /**단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다*/
	    @Override
	    protected void onRegistered(Context context, String regId) {
	        Log.d(tag, "onRegistered. regId : "+regId);
	    }

	    /**단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다*/
	    @Override
	    protected void onUnregistered(Context context, String regId) {
	        Log.d(tag, "onUnregistered. regId : "+regId);
	    }
	    @Override
	    protected boolean onRecoverableError(Context context, String errorId) {
	        Log.d(tag, "onUnregistered. errorId : "+errorId);
			return false;
	    }
	    
	    private static void generateNotification(Context context, String message) {
	        int icon = R.drawable.ic_launcher;
	        long when = System.currentTimeMillis();
	        NotificationManager notificationManager = (NotificationManager)
	                context.getSystemService(Context.NOTIFICATION_SERVICE);
	        Notification notification = new Notification(icon, message, when);
	        String title = context.getString(R.string.app_name);
	        Intent notificationIntent = new Intent(context, GCMIntentService.class);
	        // set intent so it does not start a new activity
	        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        PendingIntent intent =
	                PendingIntent.getActivity(context, 0, notificationIntent, 0);
	        notification.setLatestEventInfo(context, title, message, intent);
	        notification.flags |= Notification.FLAG_AUTO_CANCEL;
	        notificationManager.notify(0, notification);
	    }
	}
