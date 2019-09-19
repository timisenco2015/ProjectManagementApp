package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewProjectMemeberAllTasksReview {

    private ProgressBarClass progressBarClass;
    private Activity activity;
   private String memberUsername;
   private String memberFullName;
    private S3ImageClass s3ImageClass;
    private HttpRequestClass httpRequestClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
   private String projectName;
   private  JSONArray memberAllProjectTasksList;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private boolean checkForPermission = true;
    public ViewProjectMemeberAllTasksReview(Activity activity, String memberUsername, String memberFullName,String projectName)
    {
        this.activity = activity;
        this.memberUsername=memberUsername;
        this.memberFullName=memberFullName;
        progressBarClass = new ProgressBarClass(activity);
        this.projectName=projectName;
        this.s3ImageClass = new S3ImageClass(activity);
        String result = null;
        try {
            result = getMemberAllProjectTasks(projectName,memberUsername);
            if(result!=null) {
                memberAllProjectTasksList = new JSONArray(result);
                int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                switch(screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        projectMemberAllTasksViewLarge();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                        projectMemberAllTasksViewNormal();
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
                builder = new AlertDialog.Builder(activity);
                builder.setMessage("No task assigned to this member yet");
                builder.setCancelable(true);
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });



                alertDialog = builder.create();
                alertDialog.show();
            }

        }  catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public void projectMemberAllTasksViewNormal()
    {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.eachprojectmemberalltasksreviewlayout);
        LinearLayout projectMemberAllTasksLinearLT = dialog.findViewById(R.id.projectMemberAllTasksLLTId);
        ImageView closeMbrAllPrjtTasksImageView = dialog.findViewById(R.id.closeMbrAllPrjtTasksImageVWId);
        ImageView profileImageView = dialog.findViewById(R.id.profileImageVWId);
        TextView userNameTextView = dialog.findViewById(R.id.userNameTextVWId);
        ImageView rateMemberImageView = dialog.findViewById(R.id.rateMemberImageVWId);
      //  rateMemberImageView.setVisibility(View.GONE);

        closeMbrAllPrjtTasksImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


                userNameTextView.setText(memberFullName);
          /*      if(s3ImageClass.confirmIfImageInPhone(memberUsername))
                {
                    profileImageView.setImageBitmap(s3ImageClass.readFromPhone(memberUsername));
                }
                else {
                    s3ImageClass = new S3ImageClass(activity, memberUsername, "profilepicfolder");
                    Bitmap bitMap = s3ImageClass.getImageBitMap();
                    if (s3ImageClass.isObjectExists()) {
                        s3ImageClass.writeToPhone(memberUsername,bitMap);
                        profileImageView.setImageBitmap(s3ImageClass.getImageBitMap());
                        profileImageView.invalidate();
                    }

                }
                */


        if(!s3ImageClass.hasPermission())
        {
            checkForPermission = s3ImageClass.getPermission();
        }

        if(s3ImageClass.hasPermission() && checkForPermission)
        {

            if(s3ImageClass.confirmIfImageInPhone(memberUsername))
            {
                profileImageView.setImageBitmap(s3ImageClass.readFromPhone(memberUsername));
            }

            else {
                s3ImageClass = new S3ImageClass(activity,memberUsername, "profilepicfolder");
                Bitmap bitMap = s3ImageClass.getImageBitMap();
                if (s3ImageClass.isObjectExists()) {
                    if(s3ImageClass.hasPermission()) {
                        s3ImageClass.writeToPhone(memberUsername, bitMap);
                    }
                    profileImageView.setImageBitmap(s3ImageClass.getImageBitMap());
                    profileImageView.invalidate();
                }
                else
                {
                    Picasso.with(activity).load("https://ui-avatars.com/api/?name="+memberFullName+"&background=90a8a8&color=fff&size=128").into(profileImageView);
                }
            }
        }




        for(int i=0; i<memberAllProjectTasksList.length();i++) {
                    try {
                    LinearLayout outerLinearLayout = new LinearLayout(activity);
                    LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    outerLinearLayoutLP.setMargins(20, 40, 0, 40);
                    outerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                    outerLinearLayout.setLayoutParams(outerLinearLayoutLP);


                    JSONObject tempObject = null;

                        tempObject = new JSONObject(memberAllProjectTasksList.getString(i));



                    LinearLayout.LayoutParams taskNameTxtViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView taskNameTextView = new TextView(activity);
                    taskNameTxtViewLLP.setMargins(0, 0, 0, 20);
                    taskNameTextView.setLayoutParams(taskNameTxtViewLLP);
                    taskNameTextView.setTextSize(16);
                   // taskNameTextView.setTypeface(activity.getResources().getFont(R.font.montserrat));
                    taskNameTextView.setText(tempObject.getString("userName"));
                    taskNameTextView.setTextColor(activity.getResources().getColor(R.color.color28));


                    LinearLayout yourProjectTaskStatusLinearLayout = new LinearLayout(activity);
                    LinearLayout.LayoutParams yourProjectTaskStatusLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    yourProjectTaskStatusLinearLayoutLP.setMargins(0, 0, 0, 40);
                    yourProjectTaskStatusLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    yourProjectTaskStatusLinearLayout.setLayoutParams(yourProjectTaskStatusLinearLayoutLP);

                    LinearLayout.LayoutParams taskStatusTextTxtView1LLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView taskStatusTextTxtView1 = new TextView(activity);
                    taskStatusTextTxtView1.setLayoutParams(taskStatusTextTxtView1LLP);
                    taskStatusTextTxtView1.setTextSize(14);
                  //  taskStatusTextTxtView1.setTypeface(getResources().getFont(R.font.montserrat));
                    taskStatusTextTxtView1.setText("Your done percentage is ");
                    taskStatusTextTxtView1.setTextColor(activity.getResources().getColor(R.color.color27));


                    RelativeLayout yourProjectTaskStatusCircleInnerLLT = new RelativeLayout(activity);

                    yourProjectTaskStatusCircleInnerLLT.setBackground(activity.getResources().getDrawable(R.drawable.curveradiusbackgrnd, null));
                    RelativeLayout.LayoutParams yourProjectTaskStatusCircleInnerLLTLP = new RelativeLayout.LayoutParams(150,70);
                    yourProjectTaskStatusCircleInnerLLTLP.setMargins(160, 0, 0, 0);
                    yourProjectTaskStatusCircleInnerLLT.setLayoutParams(yourProjectTaskStatusCircleInnerLLTLP);


                    RelativeLayout.LayoutParams yourProjectTaskStatusTxtViewLLP = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    yourProjectTaskStatusTxtViewLLP.addRule(RelativeLayout.CENTER_IN_PARENT);
                    TextView yourProjectTaskStatusTVW = new TextView(activity);
                    yourProjectTaskStatusTVW.setLayoutParams(yourProjectTaskStatusTxtViewLLP);
                    yourProjectTaskStatusTVW.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    yourProjectTaskStatusTVW.setTextSize(12);
                 //   yourProjectTaskStatusTVW.setTypeface(getResources().getFont(R.font.montserrat));
                    yourProjectTaskStatusTVW.setText(tempObject.getString("memberTskDonePer") + "%");
                    yourProjectTaskStatusTVW.setTextColor(activity.getResources().getColor(R.color.color27));

                    yourProjectTaskStatusCircleInnerLLT.addView(yourProjectTaskStatusTVW);


                    yourProjectTaskStatusLinearLayout.addView(taskStatusTextTxtView1);
                    yourProjectTaskStatusLinearLayout.addView(yourProjectTaskStatusCircleInnerLLT);


                    LinearLayout projectTaskStatusLinearLayout = new LinearLayout(activity);
                    LinearLayout.LayoutParams projectTaskStatusLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    projectTaskStatusLinearLayoutLP.setMargins(0, 0, 0, 5);
                    projectTaskStatusLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    projectTaskStatusLinearLayout.setLayoutParams(projectTaskStatusLinearLayoutLP);

                    LinearLayout.LayoutParams taskStatusTextTxtView2LLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView taskStatusTextTxtView2 = new TextView(activity);
                    taskStatusTextTxtView2.setLayoutParams(taskStatusTextTxtView2LLP);
                    taskStatusTextTxtView2.setTextSize(14);
                    //taskStatusTextTxtView2.setTypeface(getResources().getFont(R.font.montserrat));
                    taskStatusTextTxtView2.setText("Task total done percentage is");
                    taskStatusTextTxtView2.setTextColor(activity.getResources().getColor(R.color.color27));


                    RelativeLayout projectTaskStatusCircleInnerLLT = new RelativeLayout(activity);
                    RelativeLayout.LayoutParams projectTaskStatusCircleInnerLLTLP = new RelativeLayout.LayoutParams(150, 70);
                    projectTaskStatusCircleInnerLLT.setBackground(activity.getResources().getDrawable(R.drawable.curveradiusbackgrnd, null));
                    projectTaskStatusCircleInnerLLTLP.setMargins(80, 0, 0, 0);
                    projectTaskStatusCircleInnerLLT.setLayoutParams(projectTaskStatusCircleInnerLLTLP);

                    RelativeLayout.LayoutParams projectTaskStatusTxtViewLLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    projectTaskStatusTxtViewLLP.addRule(RelativeLayout.CENTER_IN_PARENT);
                    TextView projectTaskStatusTVW = new TextView(activity);
                    projectTaskStatusTVW.setLayoutParams(projectTaskStatusTxtViewLLP);
                    projectTaskStatusTVW.setTextSize(12);
                  //  projectTaskStatusTVW.setTypeface(getResources().getFont(R.font.montserrat));
                    projectTaskStatusTVW.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    projectTaskStatusTVW.setText(tempObject.getString("taskDonePercentage") + "%");
                    projectTaskStatusTVW.setTextColor(activity.getResources().getColor(R.color.color27));

                    projectTaskStatusCircleInnerLLT.addView(projectTaskStatusTVW);
                    projectTaskStatusLinearLayout.addView(taskStatusTextTxtView2);
                    projectTaskStatusLinearLayout.addView(projectTaskStatusCircleInnerLLT);


                    outerLinearLayout.addView(taskNameTextView);
                    outerLinearLayout.addView(yourProjectTaskStatusLinearLayout);
                    outerLinearLayout.addView(projectTaskStatusLinearLayout);


                    projectMemberAllTasksLinearLT.addView(outerLinearLayout);

                    dialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

    public void projectMemberAllTasksViewLarge()
    {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.eachprojectmemberalltasksreviewlayout);
        LinearLayout projectMemberAllTasksLinearLT = dialog.findViewById(R.id.projectMemberAllTasksLLTId);
        ImageView closeMbrAllPrjtTasksImageView = dialog.findViewById(R.id.closeMbrAllPrjtTasksImageVWId);
        ImageView profileImageView = dialog.findViewById(R.id.profileImageVWId);
        TextView userNameTextView = dialog.findViewById(R.id.userNameTextVWId);
        ImageView rateMemberImageView = dialog.findViewById(R.id.rateMemberImageVWId);
     //  rateMemberImageView.setVisibility(View.GONE);

        closeMbrAllPrjtTasksImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        userNameTextView.setText(memberFullName);
          /*      if(s3ImageClass.confirmIfImageInPhone(memberUsername))
                {
                    profileImageView.setImageBitmap(s3ImageClass.readFromPhone(memberUsername));
                }
                else {
                    s3ImageClass = new S3ImageClass(activity, memberUsername, "profilepicfolder");
                    Bitmap bitMap = s3ImageClass.getImageBitMap();
                    if (s3ImageClass.isObjectExists()) {
                        s3ImageClass.writeToPhone(memberUsername,bitMap);
                        profileImageView.setImageBitmap(s3ImageClass.getImageBitMap());
                        profileImageView.invalidate();
                    }

                }
                */
        for(int i=0; i<memberAllProjectTasksList.length();i++) {
            try {
                LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                outerLinearLayoutLP.setMargins(20, 40, 0, 40);
                outerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);


                JSONObject tempObject = null;

                tempObject = new JSONObject(memberAllProjectTasksList.getString(i));



                LinearLayout.LayoutParams taskNameTxtViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView taskNameTextView = new TextView(activity);
                taskNameTxtViewLLP.setMargins(0, 0, 0, 20);
                taskNameTextView.setLayoutParams(taskNameTxtViewLLP);
                taskNameTextView.setTextSize(25);
                // taskNameTextView.setTypeface(activity.getResources().getFont(R.font.montserrat));
                taskNameTextView.setText(tempObject.getString("userName"));
                taskNameTextView.setTextColor(activity.getResources().getColor(R.color.color28));


                LinearLayout yourProjectTaskStatusLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams yourProjectTaskStatusLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                yourProjectTaskStatusLinearLayoutLP.setMargins(0, 0, 0, 40);
                yourProjectTaskStatusLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                yourProjectTaskStatusLinearLayout.setLayoutParams(yourProjectTaskStatusLinearLayoutLP);

                LinearLayout.LayoutParams taskStatusTextTxtView1LLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView taskStatusTextTxtView1 = new TextView(activity);
                taskStatusTextTxtView1LLP.setMargins(0,0,90,0);
                taskStatusTextTxtView1.setLayoutParams(taskStatusTextTxtView1LLP);
                taskStatusTextTxtView1.setTextSize(23);
                //  taskStatusTextTxtView1.setTypeface(getResources().getFont(R.font.montserrat));
                taskStatusTextTxtView1.setText("Your done percentage is ");
                taskStatusTextTxtView1.setTextColor(activity.getResources().getColor(R.color.color27));


                RelativeLayout yourProjectTaskStatusCircleInnerLLT = new RelativeLayout(activity);
                yourProjectTaskStatusCircleInnerLLT.setBackground(activity.getResources().getDrawable(R.drawable.curveradiusbackgrnd, null));
                RelativeLayout.LayoutParams yourProjectTaskStatusCircleInnerLLTLP = new RelativeLayout.LayoutParams(120, 60);
                yourProjectTaskStatusCircleInnerLLT.setPadding(10,10,10,10);
                yourProjectTaskStatusCircleInnerLLT.setLayoutParams(yourProjectTaskStatusCircleInnerLLTLP);


                RelativeLayout.LayoutParams yourProjectTaskStatusTxtViewLLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                yourProjectTaskStatusTxtViewLLP.addRule(RelativeLayout.CENTER_IN_PARENT);
                TextView yourProjectTaskStatusTVW = new TextView(activity);
                yourProjectTaskStatusTVW.setLayoutParams(yourProjectTaskStatusTxtViewLLP);
                yourProjectTaskStatusTVW.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                yourProjectTaskStatusTVW.setTextSize(21);
                //   yourProjectTaskStatusTVW.setTypeface(getResources().getFont(R.font.montserrat));
                yourProjectTaskStatusTVW.setText(tempObject.getString("memberTskDonePer") + "%");
                yourProjectTaskStatusTVW.setTextColor(activity.getResources().getColor(R.color.color27));

                yourProjectTaskStatusCircleInnerLLT.addView(yourProjectTaskStatusTVW);


                yourProjectTaskStatusLinearLayout.addView(taskStatusTextTxtView1);
                yourProjectTaskStatusLinearLayout.addView(yourProjectTaskStatusCircleInnerLLT);


                LinearLayout projectTaskStatusLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams projectTaskStatusLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                projectTaskStatusLinearLayoutLP.setMargins(0, 0, 0, 5);
                projectTaskStatusLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                projectTaskStatusLinearLayout.setLayoutParams(projectTaskStatusLinearLayoutLP);

                LinearLayout.LayoutParams taskStatusTextTxtView2LLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView taskStatusTextTxtView2 = new TextView(activity);
                taskStatusTextTxtView2LLP.setMargins(0,0,40,0);
                taskStatusTextTxtView2.setLayoutParams(taskStatusTextTxtView2LLP);
                taskStatusTextTxtView2.setTextSize(23);
                //taskStatusTextTxtView2.setTypeface(getResources().getFont(R.font.montserrat));
                taskStatusTextTxtView2.setText("Task total done percentage is");
                taskStatusTextTxtView2.setTextColor(activity.getResources().getColor(R.color.color27));


                RelativeLayout projectTaskStatusCircleInnerLLT = new RelativeLayout(activity);
                RelativeLayout.LayoutParams projectTaskStatusCircleInnerLLTLP = new RelativeLayout.LayoutParams(120, 60);
                projectTaskStatusCircleInnerLLT.setBackground(activity.getResources().getDrawable(R.drawable.curveradiusbackgrnd, null));
                projectTaskStatusCircleInnerLLT.setPadding(10,10,10,10);
                projectTaskStatusCircleInnerLLTLP.setMargins(90, 25, 0, 0);
                projectTaskStatusCircleInnerLLT.setLayoutParams(projectTaskStatusCircleInnerLLTLP);

                RelativeLayout.LayoutParams projectTaskStatusTxtViewLLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                projectTaskStatusTxtViewLLP.addRule(RelativeLayout.CENTER_IN_PARENT);
                TextView projectTaskStatusTVW = new TextView(activity);
                projectTaskStatusTVW.setLayoutParams(projectTaskStatusTxtViewLLP);
                projectTaskStatusTVW.setTextSize(21);
                //  projectTaskStatusTVW.setTypeface(getResources().getFont(R.font.montserrat));
                projectTaskStatusTVW.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                projectTaskStatusTVW.setText(tempObject.getString("taskDonePercentage") + "%");
                projectTaskStatusTVW.setTextColor(activity.getResources().getColor(R.color.color27));

                projectTaskStatusCircleInnerLLT.addView(projectTaskStatusTVW);
                projectTaskStatusLinearLayout.addView(taskStatusTextTxtView2);
                projectTaskStatusLinearLayout.addView(projectTaskStatusCircleInnerLLT);


                outerLinearLayout.addView(taskNameTextView);
                outerLinearLayout.addView(yourProjectTaskStatusLinearLayout);
                outerLinearLayout.addView(projectTaskStatusLinearLayout);


                projectMemberAllTasksLinearLT.addView(outerLinearLayout);

                dialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }




    public String getMemberAllProjectTasks(String projectName, String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfUser",userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/gaptatm/",postDataParams, activity,"application/json", "application/json");
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
