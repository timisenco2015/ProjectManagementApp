package com.zihron.projectmanagementapp;

/**
 * Created by ayoba on 2017-12-20.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.json.JSONObject;

import java.util.ArrayList;


public class CustomChatsListViewAdapter extends ArrayAdapter<JSONObject>
{
    private ArrayList<JSONObject> valueArray;
    private static Context context;
    private static LayoutInflater inflater;
    private Intent intent;
    public CustomChatsListViewAdapter(Context context, ArrayList<JSONObject> valueArray)
    {
        super(context, R.layout.chatscustomlayout, valueArray);
        this.valueArray =valueArray;
        this.context=context;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){




        Configuration userPhoneConfig = context.getResources().getConfiguration();

         //   Button leftButtonClick;
        //Button rightButtonClick;
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chatscustomlayout,parent, false);

   // leftButtonClick= (Button) convertView.findViewById(R.id.leftButton);
      //  rightButtonClick = (Button) convertView.findViewById(R.id.rightButton);

       // leftButtonClick.setOnClickListener(new View.OnClickListener() {
       //     @Override
      ///      public void onClick(View v) {
       //         Intent PAIntent = new Intent(context, ProjectTasksActivity.class);
       //         context.startActivity(PAIntent);
        //    }
       // });

        //rightButtonClick.setOnClickListener(new View.OnClickListener() {
       //    @Override
         //   public void onClick(View v) {
        //        Intent PAIntent = new Intent(context, ProjectTasksActivity.class);
         //       context.startActivity(PAIntent);
         //   }
      //  });

        //TextView view1 = (TextView) convertView.findViewById(R.id.jobId);
        //TextView view2 = (TextView) convertView.findViewById(R.id.jobRef);
        //TextView view3 = (TextView) convertView.findViewById(R.id.moversName);
        //TextView view4 = (TextView) convertView.findViewById(R.id.moveDate);
        //TextView view5 = (TextView) convertView.findViewById(R.id.moveDays);




        return convertView;

    }














}