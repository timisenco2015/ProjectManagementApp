package com.zihron.projectmanagementapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by timisenco on 1/19/18.
 */

public class CustomProjectTaskExpandibleAdapter extends BaseExpandableListAdapter {
    private JSONArray listDataHeader;

    private Context _context;
    Typeface fontAwesomeIcon;
    private TextView projectTaskStatusTextView;
    private TextView projectTaskNameTextView;
    private TextView projectTaskEndDateTextView;
    private JSONObject allProjectDeatailsJOBject;
    private JSONArray allMemberJSONArray;
    private ImageView deleteProjectTaskImageView;
private ProgressBar progressBar;

    private int trackIndex;

    public CustomProjectTaskExpandibleAdapter(Context context, JSONArray listDataHeader) {

        this.listDataHeader = listDataHeader;
        _context = context;


    }

    @Override
    public JSONArray getChild(int groupPosition, int childPosititon) {

        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        return convertView;

    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }


    @Override
    public String getGroup(int groupPosition) {
        String jsonArrayToString = null;
        try {
            jsonArrayToString = this.listDataHeader.getString(groupPosition);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArrayToString;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.length();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, final ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.projecttaskexpadinblelistview, null);

        }


      //  PieChart projectStatusChart = (PieChart) convertView.findViewById(R.id.taskStatusChart);
     //   final ImageView expandibleButtonClk = (ImageView) convertView.findViewById(R.id.expandViewId);
        projectTaskNameTextView = (TextView) convertView.findViewById(R.id.projectTaskNameId);
        projectTaskEndDateTextView = (TextView) convertView.findViewById(R.id.endDateTxtvwId);
        projectTaskStatusTextView = (TextView)convertView.findViewById(R.id.projectTaskStatusId);
        progressBar = (ProgressBar)convertView.findViewById(R.id.progressBarId);
        deleteProjectTaskImageView = (ImageView)convertView.findViewById(R.id.deleteProjectTaskImgVWId);
        float donePercentage = 0;

        try {
          //  allMemberJSONArray = getGroup(groupPosition);
            allProjectDeatailsJOBject = new JSONObject(getGroup(groupPosition));

            projectTaskNameTextView.setText(allProjectDeatailsJOBject.getString("taskName"));

            projectTaskEndDateTextView.setText(allProjectDeatailsJOBject.getString("taskEndDate"));
           donePercentage = Float.parseFloat(allProjectDeatailsJOBject.getString("taskDonePercentage"));

            projectTaskStatusTextView.setText(donePercentage+"%");
            int donePercentageInt = Math.round(donePercentage);
            progressBar.setProgress(donePercentageInt);
        } catch (JSONException e) {
            e.printStackTrace();
        }




        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}







