package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class ProjectCommentsActivity extends AppCompatActivity {

    private LinearLayout projectCommentInnerLinearLayout;
    private JSONArray projectCommentArray;
    private String projectName;
    private String userName;
    private EditText projectCommentEditText;
    private Button projectCommentButton;
    private SharedPreferences sharedPreferences;
    private Activity activity;
    private S3ImageClass s3ImageClass;
    private ProgressBarClass progressBarClass;
    private HttpRequestClass httpRequestClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_comments);
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE );
        activity = this;

        userName = sharedPreferences.getString("userName", null);
        projectName = sharedPreferences.getString("projectName", null);
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);
        projectCommentInnerLinearLayout = (LinearLayout) findViewById(R.id.projectCommentInnerLYTId);
        projectCommentEditText = (EditText) findViewById(R.id.projectCommentEditTxtId);
        projectCommentButton = (Button)findViewById(R.id.projectCommentBtnId);
        s3ImageClass = new S3ImageClass();

        try {
            String result = getFullProjectComments();

            projectCommentArray = new JSONArray(result);
            int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

            switch(screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    generateAndPopulateFieldsLarge();
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    generateAndPopulateFieldsNormal();
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


        }  catch (JSONException e) {
            e.printStackTrace();
        }


        projectCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments =  projectCommentEditText.getText().toString();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                String commentDateTime = timestamp.toString();
            //  Toast.makeText(getApplicationContext(), commentDateTime, Toast.LENGTH_SHORT).show();

           String result = createProjectTaskComments(commentDateTime, comments );
                if(result!=null)
                {
                    Toast.makeText(activity,"You have commented on this project",Toast.LENGTH_LONG).show();
                } finish();
                startActivity(getIntent());


            }
        });

    }

    public void generateAndPopulateFieldsNormal()
    {
        projectCommentInnerLinearLayout.removeAllViews();
        LinearLayout groupInnerLLT = null;
        Typeface taskNameTF =  ResourcesCompat.getFont(activity, R.font.vollkorn);
        for(int i=0; i<projectCommentArray.length() && projectCommentArray.length()>0 ; i++)
        {
            try {
                groupInnerLLT = new LinearLayout(this);
                LinearLayout.LayoutParams groupInnerRLP = new LinearLayout.LayoutParams(1200, RelativeLayout.LayoutParams.WRAP_CONTENT);
                groupInnerLLT.setOrientation(LinearLayout.HORIZONTAL);
                groupInnerLLT.setClickable(true);
                groupInnerRLP.setMargins(10,10,10,15);
                groupInnerLLT.setId(i);
                groupInnerLLT.setBackgroundColor((Color.parseColor("#F8F8FF")));
               // groupInnerLLT.setBackground((getResources().getDrawable(R.drawable.border3,null)));
                groupInnerLLT.setLayoutParams(groupInnerRLP);

                ImageView profileImageVW = new ImageView(this);
                LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(180, 180);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setId(R.id.chatLogoId);
/*
                if(s3ImageClass.confirmIfImageInPhone(projectCommentArray.getJSONArray(i).getString(0)))
                {
                    profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(projectCommentArray.getJSONArray(i).getString(0)));
                }
                else {
                    s3ImageClass = new S3ImageClass(activity,projectCommentArray.getJSONArray(i).getString(0), "profilepicfolder");
                    Bitmap bitMap = s3ImageClass.getImageBitMap();
                    if (s3ImageClass.isObjectExists()) {
                        s3ImageClass.writeToPhone(projectCommentArray.getJSONArray(i).getString(0),bitMap);
                        profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                        profileImageVW.invalidate();
                    }
                    */
                 //   else
                 //   {
                        Picasso.with(activity).load("https://ui-avatars.com/api/?name=" + projectCommentArray.getJSONArray(i).getString(4)+projectCommentArray.getJSONArray(i).getString(5) + "&background=90a8a8&color=fff&size=128").into(profileImageVW);

                 //   }
             //   }




                // profileImageVW.setBackgroundColor(getResources().getColor(R.color.color25,null));

                LinearLayout groupInnerInnerLLT = new LinearLayout(this);
                LinearLayout.LayoutParams groupInnerInnerRLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
                groupInnerInnerLLT.setMinimumHeight(170);
                groupInnerInnerRLP.setMargins(15,5,0,0);
                //groupInnerInnerLLT.setBackgroundColor(getResources().getColor(R.color.color29,null));
                groupInnerInnerLLT.setOrientation(LinearLayout.VERTICAL);
                groupInnerInnerLLT.setLayoutParams(groupInnerInnerRLP);




                LinearLayout.LayoutParams llpCommentOwnerTxtViewLayout = new  LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT );
                llpCommentOwnerTxtViewLayout.setMargins(10,0,0,5);
                TextView commentOwnerTextView = new TextView(this);
                commentOwnerTextView.setTypeface(taskNameTF);
                commentOwnerTextView.setLayoutParams(llpCommentOwnerTxtViewLayout);
                commentOwnerTextView.setTextSize(18);
                commentOwnerTextView.setText(projectCommentArray.getJSONArray(i).getString(4)+" "+projectCommentArray.getJSONArray(i).getString(5) );
                commentOwnerTextView.setTextColor((Color.parseColor("#192a56")));

                LinearLayout.LayoutParams llpCommentDateTxtViewLayout = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT );
                llpCommentDateTxtViewLayout.setMargins(10,0,0,20);
                TextView commentDateTextView = new TextView(this);
                commentDateTextView.setLayoutParams(llpCommentDateTxtViewLayout);
                commentDateTextView.setTextSize(15);
                commentDateTextView.setTypeface(taskNameTF);
                commentDateTextView.setText(projectCommentArray.getJSONArray(i).getString(2));
                commentDateTextView.setTextColor((Color.parseColor("#7f8fa6")));




                LinearLayout.LayoutParams llpfullCommentTxtViewLayout = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                llpfullCommentTxtViewLayout.setMargins(20,0,0,0);
                TextView fullCommentTextView = new TextView(this);
                fullCommentTextView.setLayoutParams(llpfullCommentTxtViewLayout);
                fullCommentTextView.setTextSize(17);
                fullCommentTextView.setTypeface(taskNameTF);
                fullCommentTextView.setMovementMethod(new ScrollingMovementMethod());
                fullCommentTextView.setText(projectCommentArray.getJSONArray(i).getString(3));
                fullCommentTextView.setTextColor((Color.parseColor("#273c75")));


                groupInnerInnerLLT.addView(commentOwnerTextView);
                groupInnerInnerLLT.addView(commentDateTextView);
                groupInnerInnerLLT.addView(fullCommentTextView);


                groupInnerLLT.addView(profileImageVW);
                groupInnerLLT.addView( groupInnerInnerLLT);



                projectCommentInnerLinearLayout.addView(groupInnerLLT);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onBackPressed() {

    }


    public void generateAndPopulateFieldsLarge()
    {
        projectCommentInnerLinearLayout.removeAllViews();
        LinearLayout groupInnerLLT = null;
        Typeface taskNameTF =  ResourcesCompat.getFont(activity, R.font.vollkorn);
        for(int i=0; i<projectCommentArray.length() && projectCommentArray.length()>0 ; i++)
        {
            try {
                groupInnerLLT = new LinearLayout(this);
                LinearLayout.LayoutParams groupInnerRLP = new LinearLayout.LayoutParams(1800, RelativeLayout.LayoutParams.WRAP_CONTENT);
                groupInnerLLT.setOrientation(LinearLayout.HORIZONTAL);
                groupInnerLLT.setClickable(true);
                groupInnerRLP.setMargins(10,20,10,15);
                groupInnerLLT.setId(i);
                groupInnerLLT.setBackgroundColor((Color.parseColor("#F8F8FF")));
                groupInnerLLT.setLayoutParams(groupInnerRLP);

                ImageView profileImageVW = new ImageView(this);
                LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(220, 220);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setId(R.id.chatLogoId);

                if(s3ImageClass.confirmIfImageInPhone(projectCommentArray.getJSONArray(i).getString(0)))
                {
                    profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(projectCommentArray.getJSONArray(i).getString(0)));
                }
                else {
                    s3ImageClass = new S3ImageClass(activity,projectCommentArray.getJSONArray(i).getString(0), "profilepicfolder");
                    Bitmap bitMap = s3ImageClass.getImageBitMap();
                    if (s3ImageClass.isObjectExists()) {
                        s3ImageClass.writeToPhone(projectCommentArray.getJSONArray(i).getString(0),bitMap);
                        profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                        profileImageVW.invalidate();
                    }
                    else
                    {
                        Picasso.with(activity).load("https://ui-avatars.com/api/?name=" + projectCommentArray.getJSONArray(i).getString(5)+projectCommentArray.getJSONArray(i).getString(6) + "&background=90a8a8&color=fff&size=128").into(profileImageVW);

                    }
                }




                // profileImageVW.setBackgroundColor(getResources().getColor(R.color.color25,null));

                LinearLayout groupInnerInnerLLT = new LinearLayout(this);
                LinearLayout.LayoutParams groupInnerInnerRLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
                groupInnerInnerLLT.setMinimumHeight(190);
                groupInnerInnerRLP.setMargins(25,5,0,0);
                //groupInnerInnerLLT.setBackgroundColor(getResources().getColor(R.color.color29,null));
                groupInnerInnerLLT.setOrientation(LinearLayout.VERTICAL);
                groupInnerInnerLLT.setLayoutParams(groupInnerInnerRLP);




                LinearLayout.LayoutParams llpCommentOwnerTxtViewLayout = new  LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT );
                llpCommentOwnerTxtViewLayout.setMargins(10,0,0,5);
                TextView commentOwnerTextView = new TextView(this);
                commentOwnerTextView.setLayoutParams(llpCommentOwnerTxtViewLayout);
                 commentOwnerTextView.setTextSize(24);
                commentOwnerTextView.setTypeface(taskNameTF);
                commentOwnerTextView.setText(projectCommentArray.getJSONArray(i).getString(4)+" "+projectCommentArray.getJSONArray(i).getString(5) );
                commentOwnerTextView.setTextColor((Color.parseColor("#192a56")));



                LinearLayout.LayoutParams llpCommentDateTxtViewLayout = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT );
                llpCommentDateTxtViewLayout.setMargins(10,0,0,20);
                TextView commentDateTextView = new TextView(this);
                commentDateTextView.setLayoutParams(llpCommentDateTxtViewLayout);
                 commentDateTextView.setTextSize(23);
                commentDateTextView.setTypeface(taskNameTF);
                commentDateTextView.setText(projectCommentArray.getJSONArray(i).getString(2));
                commentDateTextView.setTextColor((Color.parseColor("#7f8fa6")));





                LinearLayout.LayoutParams llpfullCommentTxtViewLayout = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                llpfullCommentTxtViewLayout.setMargins(10,0,0,0);
                TextView fullCommentTextView = new TextView(this);
                fullCommentTextView.setLayoutParams(llpfullCommentTxtViewLayout);
                fullCommentTextView.setTextSize(24);
                fullCommentTextView.setTypeface(taskNameTF);
                fullCommentTextView.setMovementMethod(new ScrollingMovementMethod());
                fullCommentTextView.setText(projectCommentArray.getJSONArray(i).getString(3));
                fullCommentTextView.setTextColor((Color.parseColor("#273c75")));




                groupInnerInnerLLT.addView(commentOwnerTextView);
                groupInnerInnerLLT.addView(commentDateTextView);
                groupInnerInnerLLT.addView(fullCommentTextView);


                groupInnerLLT.addView(profileImageVW);
                groupInnerLLT.addView( groupInnerInnerLLT);



                projectCommentInnerLinearLayout.addView(groupInnerLLT);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }



    public void backToViewProjectActivity (View view)
    {
        Intent VPAIntent = new Intent(ProjectCommentsActivity.this, ViewProjectActivity.class);
        ProjectCommentsActivity.this.startActivity(VPAIntent);
    }


    public String getFullProjectComments()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/gspc/",postDataParams, activity,"application/json", "application/json");
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
                Toast.makeText(activity, "Error with connection, Please re-launch this page", Toast.LENGTH_LONG).show();
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String createProjectTaskComments(String commentDateTime, String memberComments )
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfUserName", userName);
            postDataParams.put("dateTimeOfComment",commentDateTime );
            postDataParams.put("commentFromMember",memberComments);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/cspc/",postDataParams, activity,"text/plain", "application/json");
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
                Toast.makeText(activity, "Error with connection, Please re-launch this page", Toast.LENGTH_LONG).show();
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


}
