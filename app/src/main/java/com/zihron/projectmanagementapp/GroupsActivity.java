package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsActivity extends AppCompatActivity {

    private Activity activity;
    private SharedPreferences sharedPreferences;
    private String userName;
    public static PopupWindow popUp;
    private JSONArray allGroupsJArray;
    private ArrayList<String> allGroupsArrayList;
    private LayoutInflater inflater;
    private  View popupView;
    private Gson googleJson;
    private ArrayAdapter arrayJSONAdapter;
    private HttpRequestClass httpRequestClass;
    public static PopupWindow userGroupsPopUpWindow=null;
    private ListView allUserGroupsListView;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
allUserGroupsListView = (ListView)findViewById(R.id.allUserGroupsListVWId);

       progressBarClass = new ProgressBarClass(activity);



            String result = getUserAllGroups();
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popupView = inflater.inflate(R.layout.popupwindowlayout, null);
            userGroupsPopUpWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

            if(result!=null) {
                googleJson = new Gson();
                allGroupsArrayList = googleJson.fromJson(result, ArrayList.class);
                arrayJSONAdapter = new ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, allGroupsArrayList);
                allUserGroupsListView.setAdapter(arrayJSONAdapter);
            }



            else
            {

                popUpView("No Group Created Yet");

            }



    }

    @Override
    public void onBackPressed() {

    }
    public void popUpView(String textMessage)
    {


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        TextView tv;

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popupwindowlayout,null);
        LinearLayout outerLinearlayout = (LinearLayout)customView.findViewById(R.id.popLayoutId);
        outerLinearlayout.removeAllViews();

        LinearLayout innerContainer = new LinearLayout(activity);
        innerContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llpChatPageInnerRLT = new LinearLayout.LayoutParams(850, 130);

        innerContainer.setLayoutParams(llpChatPageInnerRLT);


        LinearLayout.LayoutParams llpChatProjectNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMargins(30,30,0,0);
        projectNameTextView.setText(textMessage);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(14);
        projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color12));
        //projectNameTextView.setTypeface(activity.getResources().getFont(R.font.montserrat));

        final CircleImageView closeImageVW = new CircleImageView(activity);
        LinearLayout.LayoutParams rlpcloseImageVWLayout = new LinearLayout.LayoutParams(60, 60);
        rlpcloseImageVWLayout.setMargins(40,40,20,0);
        closeImageVW.setLayoutParams(rlpcloseImageVWLayout);
        closeImageVW.setClickable(true);
        closeImageVW.setBackground(activity.getResources().getDrawable(R.drawable.closeimage,null));
        closeImageVW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.dismiss();
            }
        });
        innerContainer.addView(closeImageVW);
        innerContainer.addView(projectNameTextView);


        outerLinearlayout.addView(innerContainer);

        popUp = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        popUp.setElevation(30.0f);

        popUp.showAtLocation(customView, Gravity.NO_GRAVITY, 30, 800);




    }






    public String getUserAllGroups()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/faucg/",postDataParams, activity,"application/json", "application/json");
            result = httpRequestClass.getResult();
            asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
            if(result!=null) {
                if (result.equalsIgnoreCase("exception")) {
                    asyncErrorDialogDisplay.handleException(activity);
                } else if (result.equalsIgnoreCase("No network")) {
                    Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
                } else if (android.text.TextUtils.isDigitsOnly(result)) {
                    asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
                }
            }
            else if(result==null)
            {
                Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

}


