package com.zihron.projectmanagementapp;

/**
 * Created by ayoba on 2017-12-20.
 */

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


public class Country_Province_State_Cities_Adapter extends BaseAdapter implements ListAdapter
{
    private JSONArray valueArray;
    private final Activity activity;
   private TextView allPurposeTextView;
private Boolean isCity;
    private Boolean isgender;

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public Country_Province_State_Cities_Adapter(Activity activity, JSONArray valueArray,Boolean isCity,Boolean isgender) {


        this.valueArray = valueArray;
        this.activity = activity;
        this.isCity = isCity;
        this.isgender = isgender;
    }


    @Override public int getCount() {
        if(null==valueArray)
            return 0;
        else
            return valueArray.length();

    }

    @Override public JSONArray getItem(int position) {
        JSONArray tempArray = null;
        if(null!=valueArray) {
            try {
                tempArray = valueArray.getJSONArray(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tempArray;
    }



    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.country_province_state_cities_layout, null);

        allPurposeTextView =(TextView)convertView.findViewById(R.id.allPurposeTextViewId);

        JSONArray tempArray = getItem(position);

        if(null!=tempArray )
        {
            try {

if(!isCity && !isgender) {
    allPurposeTextView.setText(tempArray.getString(2) + " (" + tempArray.getString(1) + ")");

}
else if(isCity && !isgender)
{
    allPurposeTextView.setText(tempArray.getString(1) );
}
else if (!isCity && isgender)
{
    allPurposeTextView.setText(tempArray.getString(0) );
}

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return convertView;
    }
}
