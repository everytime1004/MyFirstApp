//package com.example.myfirstapp;
//
//import android.app.Fragment;
//import android.app.FragmentTransaction;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//
//public class MainTopFragment extends Fragment {
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
//        Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//    	
//    	View view = inflater.inflate(R.layout.activity_main_top_fragment, container, false);
//    	
//    	Button buttonMySchedule = (Button)view.findViewById(R.id.MakeScheduleButton);
//    	Button buttonLogin = (Button)view.findViewById(R.id.LoginButton);
//    	
//    	buttonMySchedule.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//            	Intent intent = new Intent(v.getContext(), MakeScheduleActivity.class);
//        		startActivity(intent);
//            }
//        });
//        
//    	buttonLogin.setOnClickListener(new Button.OnClickListener(){
//            public void onClick(View v){                
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.framelayout_main, new DynamicTwo());
//                transaction.commit();
//            }
//        });
//        
//        return view;
//    }
//}