package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class PostAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Post> mPosts;
	
	public PostAdapter(Context context, ArrayList<Post> mPosts){
		this.mContext = context;
		this.mPosts = mPosts;
	}

	@Override
	public int getCount() {
		return mPosts.size();
	}

	@Override
	public Object getItem(int position) {
		return mPosts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PostRow mPostTextView;
		if (convertView == null) {
			mPostTextView = new PostRow(mContext, mPosts.get(position));
		}else{
			mPostTextView = (PostRow) convertView;
			
			mPostTextView.setTitle(mPosts.get(position).getTitle());
			mPostTextView.setCategory(mPosts.get(position).getCategory());
		}
		
		return mPostTextView;
	}

}
