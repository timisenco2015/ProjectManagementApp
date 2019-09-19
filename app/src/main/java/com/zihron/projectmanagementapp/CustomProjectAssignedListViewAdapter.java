package com.zihron.projectmanagementapp;

/**
 * Created by ayoba on 2017-12-20.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class CustomProjectAssignedListViewAdapter extends  ArrayAdapter<Object[]>
{
    private ArrayList<Object[]> valueArray;
    private S3ImageClass s3ImageClass;
    private final Context context;
    private Activity  activity;
    private LinearLayout projectAssignedContainers;

    @Override
    public long getItemId(int position) {
        return position;
    }


    public CustomProjectAssignedListViewAdapter(Context context,Activity  activity, ArrayList valueArray) {

super(context,0,valueArray);
        this.valueArray = valueArray;
        this.activity = activity;
        this.context = context;


    }


    @Override
    public int getCount() {
        Toast.makeText(context,"hhh",Toast.LENGTH_LONG).show();
        if(null==valueArray)
            return 0;
        else
            return valueArray.size();
    }

    @Override public Object[] getItem(int position) {
        Object[] tempArray = null;

        if(null!=valueArray) {

                tempArray = valueArray.get(position);

        }
        return tempArray;
    }



    @Override public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.projecttaskautocompleteviewlayout, null);
        CircleImageView circleImageView = convertView.findViewById(R.id.imageId);
        TextView fullNameTextView = (TextView) convertView.findViewById(R.id.fullNameTxtVwId);
        ArrayList<String> individualFriendObject = null;
       // try {
          //  individualFriendObject = valueArray.get(position);
//
     //   Toast.makeText(context,individualFriendObject.length(),Toast.LENGTH_LONG).show();
      /*  s3ImageClass = new S3ImageClass(activity, individualFriendObject.getString(0),"profilepicfolder");
        if( s3ImageClass.isObjectExists())
        {
            circleImageView.setImageBitmap(s3ImageClass.getImageBitMap());
            circleImageView.invalidate();
        }
        else
        {
            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+individualFriendObject.getString(1)+individualFriendObject.getString(2)+"&background=90a8a8&color=fff&size=128").into(circleImageView);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
        return convertView;
    }


}
