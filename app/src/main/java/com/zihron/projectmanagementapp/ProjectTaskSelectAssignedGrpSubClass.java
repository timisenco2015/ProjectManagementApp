package com.zihron.projectmanagementapp;

        import android.app.Activity;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.content.res.Configuration;
        import android.graphics.Bitmap;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;
        import com.squareup.picasso.Picasso;
        import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
        import com.zihron.projectmanagementapp.Utility.S3ImageClass;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;
        import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectTaskSelectAssignedGrpSubClass {

    private LinearLayout allGroupsLinearLayout;
    private LinearLayout allGroupsMembersLinearlayout;
    private Button un_AssignedTaskToGrpButton;
    private ProjectTaskSelectAssignedGrpSubMembersClass projectTaskSelectAssignedGrpSubMembersClass;
    private String projectName;
    private String projectTaskName;
    private Activity activity;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private HttpRequestClass httpRequestClass;
    private JSONArray assignedGroupJSONArray;
    private S3ImageClass s3ImageClass;
    private SharedPreferences sharedPreferences;

    public ProjectTaskSelectAssignedGrpSubClass(final Activity activity, LinearLayout allGroupsLinearLayout, LinearLayout allGroupsMembersLinearlayout,final String result)
    {
        this.activity = activity;
        this.allGroupsMembersLinearlayout=allGroupsMembersLinearlayout;
        this.allGroupsLinearLayout = allGroupsLinearLayout;
        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        projectName =  sharedPreferences.getString("projectName", null);
        projectTaskName = sharedPreferences.getString("projectTaskName", null);
        this.s3ImageClass = new S3ImageClass();
        this.un_AssignedTaskToGrpButton = un_AssignedTaskToGrpButton;



        this.asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        getProjectGroupsAssignedTask(result);


    }


    public void getProjectGroupsAssignedTask(String result) {


        // assignedGroupsJSONArray
        try {


            if(result==null)
            {
                Toast.makeText(activity,"You seems not to have any group created for this project yet or network issue", Toast.LENGTH_LONG).show();
            }
            else
            {
                assignedGroupJSONArray = new JSONArray(result);
            }

            projectTaskSelectAssignedGrpSubMembersClass =  new ProjectTaskSelectAssignedGrpSubMembersClass (activity,allGroupsMembersLinearlayout);
            int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
            switch (screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    /// friendsSuggestionsLayout.friendSuggestnListVwLargeScrnSizeLT();
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    groupsAssignedListVwNormalScrnSizeLT();
                    break;
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    //  friendsSuggestionsLayout.friendSuggestnListVwXLargeScrnSizeLT();
                    break;
                case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                    break;

                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                    //   friendsSuggestionsLayout.friendSuggestnListVwSmallScreenSizeLT();
                    break;
                default:

            }

        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void groupsAssignedListVwNormalScrnSizeLT() {
        allGroupsLinearLayout.removeAllViews();
        allGroupsMembersLinearlayout.removeAllViews();
        try {

            for (int i = 0; i < assignedGroupJSONArray.length(); i++) {


                LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                outerLinearLayoutLP.setMargins(10, 0, 10, 10);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout innerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerLinearLayoutLP.setMargins(10, 0, 0, 0);
                innerLinearLayout.setLayoutParams(innerLinearLayoutLP);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);


                RelativeLayout.LayoutParams llpChatPageContentGroupTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                TextView groupNameTextView = new TextView(activity);
                llpChatPageContentGroupTxtViewLayout.setMargins(0, 20, 0, 0);
                groupNameTextView.setLayoutParams(llpChatPageContentGroupTxtViewLayout);
                groupNameTextView.setText(assignedGroupJSONArray.getJSONArray(i).getString(0));
                groupNameTextView.setTextSize(12);
                groupNameTextView.setTextColor(activity.getResources().getColor(R.color.color23));

                RelativeLayout.LayoutParams llpChatPageContentDateTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                TextView friendsNameTextView = new TextView(activity);
                llpChatPageContentDateTxtViewLayout.setMargins(0, 20, 0, 0);
                friendsNameTextView.setLayoutParams(llpChatPageContentDateTxtViewLayout);
                friendsNameTextView.setText("Group Created By: "+assignedGroupJSONArray.getJSONArray(i).getString(2) + " " +assignedGroupJSONArray.getJSONArray(i).getString(3) );

                friendsNameTextView.setTextSize(10);
                friendsNameTextView.setTextColor(activity.getResources().getColor(R.color.color23));

                CircleImageView profileImageVW = new CircleImageView(activity);
                RelativeLayout.LayoutParams llpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(100, 100);
                llpInnerProfileImageVWLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                //profileImageVW.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
                llpInnerProfileImageVWLayout.setMargins(10, 20, 0, 0);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setId(R.id.memberLogoId);



                if(s3ImageClass.confirmIfImageInPhone(assignedGroupJSONArray.getJSONArray(i).getString(0)))
                {
                    profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(assignedGroupJSONArray.getJSONArray(i).getString(0)));
                }
                else {
                    s3ImageClass = new S3ImageClass(activity,assignedGroupJSONArray.getJSONArray(i).getString(0), "groupdisplayimage");
                    Bitmap bitMap = s3ImageClass.getImageBitMap();
                    if (s3ImageClass.isObjectExists()) {
                        s3ImageClass.writeToPhone(assignedGroupJSONArray.getJSONArray(i).getString(0),bitMap);
                        profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                        profileImageVW.invalidate();
                    }
                    else
                    {
                        Picasso.with(activity).load("https://ui-avatars.com/api/?name="+assignedGroupJSONArray.getJSONArray(i).getString(2) + " " +assignedGroupJSONArray.getJSONArray(i).getString(3)+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                    }
                }




                RelativeLayout.LayoutParams llpFunctionCheckBoxLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                llpFunctionCheckBoxLayout.setMargins(5, 0, 0, 0);
                llpFunctionCheckBoxLayout.addRule(RelativeLayout.RIGHT_OF,R.id.memberLogoId);
                final CheckBox allFunctionCheckBox = new CheckBox(activity);
                allFunctionCheckBox.setId(i);
                allFunctionCheckBox.setLayoutParams(llpFunctionCheckBoxLayout);
                allFunctionCheckBox.setTextColor(activity.getResources().getColor(R.color.color23));
                allFunctionCheckBox.setScaleX(.5f);
                allFunctionCheckBox.setScaleY(.5f);

                allFunctionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int index = allFunctionCheckBox.getId();
                        if(isChecked)
                        {

                            String result =null;
                            try {
                                result = getGroupTaskAssignedMembers(assignedGroupJSONArray.getJSONArray(index).getString(0));
                            }catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            if(result==null)
                            {
                                Toast.makeText(activity,"It is very strange that there is member for this group",Toast.LENGTH_LONG).show();
                            }
                            else {

                                allGroupsMembersLinearlayout.removeAllViews();
                                    projectTaskSelectAssignedGrpSubMembersClass.addGroupAllMembers(result);


                            }



                        }

                    }
                });

                innerLinearLayout.addView(groupNameTextView);
                innerLinearLayout.addView(friendsNameTextView);
                outerLinearLayout.addView(allFunctionCheckBox);
                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);

                allGroupsLinearLayout.addView(outerLinearLayout);


            }
        }
        catch (JSONException e1) {
            e1.printStackTrace();
        }
    }




    public String getGroupTaskAssignedMembers(String groupName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfProjectTask",projectTaskName);
            postDataParams.put("nameOfGroup",groupName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fugamta/",postDataParams, activity,"application/json", "application/json");
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

