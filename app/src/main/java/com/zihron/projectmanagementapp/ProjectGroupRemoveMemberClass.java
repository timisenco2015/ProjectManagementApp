package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ProjectGroupRemoveMemberClass {

   private Activity activity;
   private String projectName;
   private String userName;
    private String groupName;
    private String groupCreatedBy;
   private AutoCompleteTextView groupMbrsRemoveAutoCompleteTextView;
   private LinearLayout removeFriendsFrmGrpLinearLayout;
   private LinearLayout removeMembersBtnLLT;
    private RadioGroup groupRemoveSwitchRadioGroup;
    private String[] allProjectGroupsList;
    private S3ImageClass s3ImageClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private ArrayAdapter arrayJSONAdapter;
    private ArrayList<String> selectedMemberJArray;
    private JSONArray  groupMemberJSONArray;
    private JSONArray allProjectGroupsJArray;
    private String defaultGroupName;
   public static PopupWindow popUp;
    private String firstName, lastName;
    private HttpRequestClass httpRequestClass;
    private ProgressBarClass progressBarClass;
    private boolean checkForPermission=false;
    private boolean isDefaultGrp;
    public ProjectGroupRemoveMemberClass(final Activity activity,final String projectName, String userName,String firstName, String lastName,final AutoCompleteTextView groupMbrsRemoveAutoCompleteTextView,final LinearLayout removeFriendsFrmGrpLinearLayout,final LinearLayout removeMembersBtnLLT, RadioGroup groupRemoveSwitchRadioGroup, String defaultGroupName)
    {
        this.activity=activity;
        this.progressBarClass = new ProgressBarClass(activity);
        this.projectName=projectName;
       this.s3ImageClass = new S3ImageClass();
        isDefaultGrp = true;
if(ProjectGroupAddMemberClass.popUp!=null && ProjectGroupAddMemberClass.popUp.isShowing())
    {
        ProjectGroupAddMemberClass.popUp.dismiss();
    }
        if(ProjectGroupCreateClass.popUp!=null && ProjectGroupCreateClass.popUp.isShowing())
        {
            ProjectGroupCreateClass.popUp.dismiss();
        }
        this.defaultGroupName=defaultGroupName;
        this.userName=userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupMbrsRemoveAutoCompleteTextView=groupMbrsRemoveAutoCompleteTextView;
        this.removeFriendsFrmGrpLinearLayout=removeFriendsFrmGrpLinearLayout;
        this.removeMembersBtnLLT=removeMembersBtnLLT;
        this.groupRemoveSwitchRadioGroup=groupRemoveSwitchRadioGroup;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        removeMembersBtnLLT.setEnabled(false);
        setAutoCompleteTextViewDefault();
        try
{

        String result = null;
        result = getAllProjectGroups(projectName);

        if(result!=null) {

            allProjectGroupsJArray = new JSONArray(result);
        }

        groupRemoveSwitchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(popUp!=null && popUp.isShowing())
                {
                    popUp.dismiss();
                }
                if(checkedId == R.id.groupCreatedRadioBtnId)
                {
                    isDefaultGrp=false;
                    removeFriendsFrmGrpLinearLayout.removeAllViews();
                    allProjectGroupsList=new String[allProjectGroupsJArray.length()];
                    for(int i=0;i<allProjectGroupsJArray.length();i++){
                        try {
                            allProjectGroupsList[i]=allProjectGroupsJArray.getString(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    groupMbrsRemoveAutoCompleteTextView.setEnabled(true);
                    groupMbrsRemoveAutoCompleteTextView.setText("");
                    arrayJSONAdapter = new ArrayAdapter(activity,  android.R.layout.simple_dropdown_item_1line, allProjectGroupsList);
                    groupMbrsRemoveAutoCompleteTextView.setAdapter(arrayJSONAdapter);
                    groupMbrsRemoveAutoCompleteTextView.setThreshold(1);
                    groupMbrsRemoveAutoCompleteTextView.setEnabled(true);

                }
                else if(checkedId==R.id.groupDefaultRadioBtnId)
                {
                    isDefaultGrp=true;
                    removeFriendsFrmGrpLinearLayout.removeAllViews();

                    setAutoCompleteTextViewDefault();


                }
            }
        });

    groupMbrsRemoveAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                groupName = allProjectGroupsJArray.getString(position);

                populateFriendsLayout();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    });

    removeMembersBtnLLT.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(selectedMemberJArray.size()>0) {

              String result = null;

                    result =deleteMembersToProjectGroups(projectName, groupName, groupCreatedBy, selectedMemberJArray.toString());

                if(result.equalsIgnoreCase("Record Not Deleted"))
                {
                    Toast.makeText(activity,"Member(s) not deleted from Group",Toast.LENGTH_LONG).show();
                }
                else if(result.equalsIgnoreCase("Record Deleted"))
                {
                    sendNotificationsToUsers();
                    Toast.makeText(activity,"Member(s) deleted from Group",Toast.LENGTH_LONG).show();
                }


            }
            else
            {
                Toast.makeText(activity, "No member selected to delete",Toast.LENGTH_LONG).show();
            }

            Intent pGRMIntent = new Intent(activity,ProjectMembersGroupInviteActivity.class);
            activity.startActivity(pGRMIntent);
            }

    });

} catch (JSONException e) {

}
    }


        public void setAutoCompleteTextViewDefault()
    {


        groupName = defaultGroupName;
        groupCreatedBy = userName;


        populateFriendsLayout();

        groupMbrsRemoveAutoCompleteTextView.setEnabled(false);
        groupMbrsRemoveAutoCompleteTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                groupMbrsRemoveAutoCompleteTextView.showDropDown();
            }
        },500);
        groupMbrsRemoveAutoCompleteTextView.setText(defaultGroupName);
        groupMbrsRemoveAutoCompleteTextView.setSelection(groupMbrsRemoveAutoCompleteTextView.getText().length());

    }


    public void populateFriendsLayout()
    {
        String result = null;
        try {

            if(isDefaultGrp ) {
                result =  getProjectDefaultGroupAllMembers(projectName);

            }
            else {
                result= getProjectGroupAllMembers(projectName,groupName);
            }

            groupMemberJSONArray = new JSONArray(result);
            if(groupMemberJSONArray.length()>0) {

                int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                switch(screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        createSelectedFriendHorizontalLargeView();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                        createSelectedFriendHorizontalNormalView();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                        // friendsSuggestionsLayout.friendSuggestnListVwXLargeScrnSizeLT();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                        break;

                    case Configuration.SCREENLAYOUT_SIZE_SMALL:
                        // friendsSuggestionsLayout.friendSuggestnListVwSmallScreenSizeLT();
                        break;
                    default:

                }

            }
            else
            {

                popUpView("No members for this group yet or members still pending");
            }


        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendNotificationsToUsers()
    {

        String result =null;
        try {
            result =  createNotifications(userName, selectedMemberJArray.toString(),  "02","Removed",groupName,projectName);
            if(result.equalsIgnoreCase("Record Inserted")) {
                for (int i = 0; i <selectedMemberJArray.size(); i++) {

                    result = getOneSignalIdAttachedUserName(selectedMemberJArray.get(i).toString());
                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'You have project group remove request from "+firstName+" "+lastName+"'}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);


                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }

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
        LinearLayout.LayoutParams llpChatPageInnerRLT = new LinearLayout.LayoutParams(900, 170);

        innerContainer.setLayoutParams(llpChatPageInnerRLT);


        LinearLayout.LayoutParams llpChatProjectNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMargins(30,30,0,0);
        projectNameTextView.setText(textMessage);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(14);
        projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color12));
       // projectNameTextView.setTypeface(activity.getResources().getFont(R.font.montserrat));

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

        popUp.showAtLocation(customView, Gravity.NO_GRAVITY, 10, 400);




    }





    public void createSelectedFriendHorizontalNormalView()
    {
        JSONArray tempArray = null;

        selectedMemberJArray  = new ArrayList<String>();
        for(int i=0;i<groupMemberJSONArray.length(); i++) {
            try {
                tempArray = groupMemberJSONArray.getJSONArray(i);

                LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerSelectionLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerSelectionLinearLayout.setId(i);
                innerSelectionLinearLayoutLP.setMargins(40, 0, 0, 0);
                innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);

                CircleImageView profileImageVW = new CircleImageView(activity);
                RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
                rlpInnerProfileImageVWLayout.setMargins(35, 0, 0, 0);
                profileImageVW.setLayoutParams(rlpInnerProfileImageVWLayout);
                profileImageVW.setId(R.id.memberLogoId);

                if(!s3ImageClass.hasPermission())
                {
                    checkForPermission = s3ImageClass.getPermission();
                }

                if(s3ImageClass.hasPermission() && checkForPermission)
                {

                    if(s3ImageClass.confirmIfImageInPhone(tempArray.getString(0)))
                    {
                        profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(tempArray.getString(0)));
                    }

                    else {
                        s3ImageClass = new S3ImageClass(activity,tempArray.getString(0), "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            if(s3ImageClass.hasPermission()) {
                                s3ImageClass.writeToPhone(tempArray.getString(0), bitMap);
                            }
                            profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageVW.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+tempArray.getString(1) + " " + tempArray.getString(2)+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                        }
                    }
                }







                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(15);
                // friendNameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                try {
                    friendNameTextView.setText(tempArray.getString(1)+" "+tempArray.getString(1));

                    friendNameTextView.setSingleLine(true);
                    friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                    friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                    friendNameTxtViewLayout.setMargins(0, 10, 0, 0);

                    String requestStatus = tempArray.getString(3);
                    LinearLayout.LayoutParams requestStatusTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    requestStatusTxtViewLayout.setMargins(0, 10, 0, 0);

                    final TextView requestStatusTextView = new TextView(activity);
                    requestStatusTextView.setLayoutParams(friendNameTxtViewLayout);
                    requestStatusTextView.setTextSize(12);
                    // friendNameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    requestStatusTextView.setText(requestStatus);
                    requestStatusTextView.setSingleLine(true);
                    requestStatusTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                    requestStatusTextView.setTextColor(activity.getResources().getColor(R.color.color6));





                    final CheckBox selectBox = new CheckBox(activity);
                    selectBox.setScaleX(.8F);
                    selectBox.setScaleY(.8F);
                    selectBox.setId(i);
                    RelativeLayout.LayoutParams rlpcloseImageVWLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlpcloseImageVWLayout.addRule(RelativeLayout.RIGHT_OF, R.id.memberLogoId);
                    rlpcloseImageVWLayout.addRule(RelativeLayout.ALIGN_BOTTOM);
                    selectBox.setLayoutParams(rlpcloseImageVWLayout);
                    selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            int index = selectBox.getId();
                            try {
                                if(isChecked)
                                {

                                    removeMembersBtnLLT.setEnabled(true);
                                    selectedMemberJArray.add(groupMemberJSONArray.getJSONArray(index).getString(0));

                                }
                                else
                                {
                                    selectedMemberJArray.remove(groupMemberJSONArray.getJSONArray(index).getString(0));
                                    if(selectedMemberJArray.size()==0)
                                    {
                                        removeMembersBtnLLT.setEnabled(false);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    });


                    RelativeLayout innerSelectionRelativeLayout = new RelativeLayout(activity);
                    RelativeLayout.LayoutParams innerSelectionRelativeLayoutLP = new RelativeLayout.LayoutParams(450, LinearLayout.LayoutParams.WRAP_CONTENT);
                    innerSelectionRelativeLayout.setLayoutParams(innerSelectionRelativeLayoutLP);
                    innerSelectionRelativeLayout.addView(profileImageVW);
                    innerSelectionRelativeLayout.addView(selectBox);

                    innerSelectionLinearLayout.addView(innerSelectionRelativeLayout);
                    innerSelectionLinearLayout.addView(friendNameTextView);
                    if(requestStatus.equalsIgnoreCase("Pending")) {
                        innerSelectionLinearLayout.addView(requestStatusTextView);
                    }
                    removeFriendsFrmGrpLinearLayout.addView(innerSelectionLinearLayout);
                } catch (JSONException e) {
                    e.printStackTrace();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void createSelectedFriendHorizontalLargeView()
    {
        JSONArray tempArray = null;

        selectedMemberJArray  = new ArrayList<String>();
        for(int i=0;i<groupMemberJSONArray.length(); i++) {
            try {
                tempArray = groupMemberJSONArray.getJSONArray(i);

                LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerSelectionLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerSelectionLinearLayout.setId(i);
                innerSelectionLinearLayoutLP.setMargins(20, 0, 0, 0);
                innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);

                CircleImageView profileImageVW = new CircleImageView(activity);
                RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
                rlpInnerProfileImageVWLayout.setMargins(35, 0, 0, 0);
                profileImageVW.setLayoutParams(rlpInnerProfileImageVWLayout);
                profileImageVW.setId(R.id.memberLogoId);
                if(!s3ImageClass.hasPermission())
                {
                    checkForPermission = s3ImageClass.getPermission();
                }

                if(s3ImageClass.hasPermission() && checkForPermission)
                {

                    if(s3ImageClass.confirmIfImageInPhone(tempArray.getString(0)))
                    {
                        profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(tempArray.getString(0)));
                    }

                    else {
                        s3ImageClass = new S3ImageClass(activity,tempArray.getString(0), "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            if(s3ImageClass.hasPermission()) {
                                s3ImageClass.writeToPhone(tempArray.getString(0), bitMap);
                            }
                            profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageVW.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+tempArray.getString(1) + " " + tempArray.getString(2)+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                        }
                    }
                }

                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(15);
                // friendNameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                try {
                    friendNameTextView.setText(tempArray.getString(1)+" "+tempArray.getString(1));

                    friendNameTextView.setSingleLine(true);
                    friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                    friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                    friendNameTxtViewLayout.setMargins(0, 10, 0, 0);


                    final CheckBox selectBox = new CheckBox(activity);
                    selectBox.setScaleX(.8F);
                    selectBox.setScaleY(.8F);
                    selectBox.setId(i);
                    RelativeLayout.LayoutParams rlpcloseImageVWLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlpcloseImageVWLayout.addRule(RelativeLayout.RIGHT_OF, R.id.memberLogoId);
                    rlpcloseImageVWLayout.addRule(RelativeLayout.ALIGN_BOTTOM);
                    selectBox.setLayoutParams(rlpcloseImageVWLayout);
                    selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            int index = selectBox.getId();
                            try {
                                if(isChecked)
                                {

                                    removeMembersBtnLLT.setEnabled(true);
                                    selectedMemberJArray.add(groupMemberJSONArray.getJSONArray(index).getString(0));

                                }
                                else
                                {
                                    selectedMemberJArray.remove(groupMemberJSONArray.getJSONArray(index).getString(0));
                                    if(selectedMemberJArray.size()==0)
                                    {
                                        removeMembersBtnLLT.setEnabled(false);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    });

String requestStatus = tempArray.getString(3);
                    LinearLayout.LayoutParams requestStatusTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    requestStatusTxtViewLayout.setMargins(0, 10, 0, 0);

                    final TextView requestStatusTextView = new TextView(activity);
                    requestStatusTextView.setLayoutParams(friendNameTxtViewLayout);
                    requestStatusTextView.setTextSize(12);
                    // friendNameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                        requestStatusTextView.setText(requestStatus);
                        requestStatusTextView.setSingleLine(true);
                        requestStatusTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                        requestStatusTextView.setTextColor(activity.getResources().getColor(R.color.color6));




                        RelativeLayout innerSelectionRelativeLayout = new RelativeLayout(activity);
                    RelativeLayout.LayoutParams innerSelectionRelativeLayoutLP = new RelativeLayout.LayoutParams(450, LinearLayout.LayoutParams.WRAP_CONTENT);
                    innerSelectionRelativeLayout.setLayoutParams(innerSelectionRelativeLayoutLP);
                    innerSelectionRelativeLayout.addView(profileImageVW);
                    innerSelectionRelativeLayout.addView(selectBox);

                    innerSelectionLinearLayout.addView(innerSelectionRelativeLayout);
                    innerSelectionLinearLayout.addView(friendNameTextView);
                    if(requestStatus.equalsIgnoreCase("Pending")) {
                        innerSelectionLinearLayout.addView(requestStatusTextView);
                    }
                    removeFriendsFrmGrpLinearLayout.addView(innerSelectionLinearLayout);
                } catch (JSONException e) {
                    e.printStackTrace();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }










    public String deleteMembersToProjectGroups(String projectName, String groupName,String groupCreatedBy, String allMembersToString)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("groupCreatedBy", groupCreatedBy);
            postDataParams.put("allMembersToString", allMembersToString);;

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/dapgm/",postDataParams, activity,"text/plain", "application/json");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getAllProjectGroups(String projectName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fapg/",postDataParams, activity,"application/json", "application/json");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }




    public String getProjectDefaultGroupAllMembers(String projectName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/faudgm/",postDataParams, activity,"application/json", "application/json");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }



    public String getProjectGroupAllMembers(String projectName, String groupName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfGroup", groupName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/faugm/",postDataParams, activity,"application/json", "application/json");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }






    public String getOneSignalIdAttachedUserName(String memberUserName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fosau/",postDataParams, activity,"application/json", "application/json");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    public String createNotifications(String notiSenderName, String allNotiRecieverNamesList,  String entityId,String entityType, String groupName,String projectNamee)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        String  notiCreatedDate=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        try {
            postDataParams.put("userNameOfSender",  notiSenderName);
            postDataParams.put("allRecieversList", allNotiRecieverNamesList);
            postDataParams.put("dateCreated", notiCreatedDate);
            postDataParams.put("entityId", entityId);
            postDataParams.put("entityType", entityType);
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("nameOfProject", projectName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/iun/",postDataParams, activity,"text/plain", "application/json");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

}
