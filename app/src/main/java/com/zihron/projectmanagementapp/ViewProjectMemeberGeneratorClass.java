package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProjectMemeberGeneratorClass {

    private JSONArray projectAllMembersJSONArray;
    private Activity activity;
    private LinearLayout memeberLayoutIdHSView;
    private S3ImageClass s3ImageClass;
    private String projectName;
    private boolean checkForPermission = true;
private ViewProjectMemeberAllTasksReview viewProjectMemeberAllTasksReview;
    public ViewProjectMemeberGeneratorClass(JSONArray projectAllMembersJSONArray, Activity activity,LinearLayout memeberLayoutIdHSView,String projectName)
    {
        this.projectAllMembersJSONArray = projectAllMembersJSONArray;
        this.activity = activity;
        this.s3ImageClass = new S3ImageClass();
        this.memeberLayoutIdHSView=memeberLayoutIdHSView;
        this.projectName = projectName;
        int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
               // generateMemberLargeLayout();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                generateMemberNormalLayout();
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

    public void generateMemberNormalLayout()
    {
        if(projectAllMembersJSONArray!=null)
        {
            Typeface otherNameTF =  ResourcesCompat.getFont(activity, R.font.lato);
            memeberLayoutIdHSView.removeAllViews();
            JSONObject memberObject = null;

            for (int i = 0; i < projectAllMembersJSONArray.length(); i++) {
                try {
                    memberObject = new JSONObject(projectAllMembersJSONArray.getString(i));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {


                    RelativeLayout outerLinearLayout = new RelativeLayout(activity);
                    LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    outerLinearLayout.setBackground(activity.getResources().getDrawable(R.drawable.projectmemberbackground,null));
                    outerLinearLayout.setPadding(20,20,20,20);
                    outerLinearLayoutLP.setMargins(0, 0, 20, 0);
                    outerLinearLayout.setLayoutParams(outerLinearLayoutLP);





                    RelativeLayout.LayoutParams llpChatPageContentDateTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    final TextView memberNameTextView = new TextView(activity);
                    llpChatPageContentDateTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.memberLogoId);
                    llpChatPageContentDateTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    memberNameTextView.setLayoutParams(llpChatPageContentDateTxtViewLayout);
                    memberNameTextView.setTextSize(12);
                    memberNameTextView.setSingleLine(true);
                    memberNameTextView.setTypeface(otherNameTF);
                    memberNameTextView.setText(memberObject.getString("firstName") + " " + memberObject.getString("lastName"));
                    memberNameTextView.setId(R.id.memberNameLogoId);
                    memberNameTextView.setTextColor(activity.getResources().getColor(R.color.color43));



                    RelativeLayout.LayoutParams llpMemebrEmailTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    final TextView memberEmailTextView = new TextView(activity);
                    llpMemebrEmailTxtViewLayout.setMargins(0, 20, 0, 0);
                    llpMemebrEmailTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.memberNameLogoId);
                    llpMemebrEmailTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    memberEmailTextView.setLayoutParams(llpMemebrEmailTxtViewLayout);
                    memberEmailTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    memberEmailTextView.setTextSize(12);
                    memberEmailTextView.setSingleLine(true);
                    memberEmailTextView.setTypeface(otherNameTF);
                    memberEmailTextView.setText(memberObject.getString("userName"));
                    memberEmailTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    memberEmailTextView.setId(R.id.memberEmailLogoId);
                    memberEmailTextView.setTextColor(activity.getResources().getColor(R.color.color43));


                    CircleImageView profileImageVW = new CircleImageView(activity);
                    RelativeLayout.LayoutParams llpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(220, 220);
                    llpInnerProfileImageVWLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    llpInnerProfileImageVWLayout.setMargins(0, 20, 0, 0);
                    profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                    profileImageVW.setId(R.id.memberLogoId);

                    if(!s3ImageClass.hasPermission())
                    {
                        checkForPermission = s3ImageClass.getPermission();
                    }

                    if(s3ImageClass.hasPermission() && checkForPermission)
                    {

                        if(s3ImageClass.confirmIfImageInPhone(memberObject.getString("userName")))
                        {
                            profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(memberObject.getString("userName")));
                        }

                        else {
                            s3ImageClass = new S3ImageClass(activity,memberObject.getString("userName"), "profilepicfolder");
                            Bitmap bitMap = s3ImageClass.getImageBitMap();
                            if (s3ImageClass.isObjectExists()) {
                                if(s3ImageClass.hasPermission()) {
                                    s3ImageClass.writeToPhone(memberObject.getString("userName"), bitMap);
                                }
                                profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                                profileImageVW.invalidate();
                            }
                            else
                            {
                                Picasso.with(activity).load("https://ui-avatars.com/api/?name="+memberObject.getString("firstName") + " " + memberObject.getString("lastName")+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                            }
                        }
                    }






                    outerLinearLayout.addView(profileImageVW);
                    outerLinearLayout.addView(memberNameTextView);
                    outerLinearLayout.addView(memberEmailTextView);
                    outerLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          // viewProjectMemeberAllTasksReview = new ViewProjectMemeberAllTasksReview(activity, memberEmailTextView.getText().toString(), memberNameTextView.getText().toString(),projectName);
                           // projectMemberAllTasksView(memberEmailTextView.getText().toString(), memberNameTextView.getText().toString());
                        }
                    });

                    memeberLayoutIdHSView.addView(outerLinearLayout);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void generateMemberLargeLayout()
    {
        if(projectAllMembersJSONArray!=null)
        {
            Typeface otherNameTF =  ResourcesCompat.getFont(activity, R.font.lato);
            memeberLayoutIdHSView.removeAllViews();
            JSONObject memberObject = null;

            for (int i = 0; i < projectAllMembersJSONArray.length(); i++) {
                try {
                    memberObject = new JSONObject(projectAllMembersJSONArray.getString(i));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    RelativeLayout outerLinearLayout = new RelativeLayout(activity);
                    LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    outerLinearLayout.setBackground(activity.getResources().getDrawable(R.drawable.projectmemberbackground,null));
                    outerLinearLayout.setPadding(20,20,20,20);
                    outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                    outerLinearLayoutLP.setMargins(0, 0, 40, 0);



                    RelativeLayout.LayoutParams llpChatPageContentDateTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    final TextView memberNameTextView = new TextView(activity);
                    llpChatPageContentDateTxtViewLayout.setMargins(0, 10, 0, 0);
                    llpChatPageContentDateTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.memberLogoId);
                    llpChatPageContentDateTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    memberNameTextView.setLayoutParams(llpChatPageContentDateTxtViewLayout);
                    memberNameTextView.setTextSize(20);
                    memberNameTextView.setSingleLine(true);
                    memberNameTextView.setTypeface(otherNameTF);
                    memberNameTextView.setText(memberObject.getString("firstName") + " " + memberObject.getString("lastName"));
                    memberNameTextView.setId(R.id.memberNameLogoId);
                    memberNameTextView.setTextColor(activity.getResources().getColor(R.color.color43));


                    RelativeLayout.LayoutParams llpMemebrEmailTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    final TextView memberEmailTextView = new TextView(activity);
                    llpMemebrEmailTxtViewLayout.setMargins(0, 10, 0, 0);

                    llpMemebrEmailTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.memberNameLogoId);
                    llpMemebrEmailTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    memberEmailTextView.setLayoutParams(llpMemebrEmailTxtViewLayout);
                    memberEmailTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    memberEmailTextView.setTextSize(18);
                    memberEmailTextView.setTypeface(otherNameTF);
                    memberEmailTextView.setSingleLine(true);
                    memberEmailTextView.setText(memberObject.getString("userName"));
                    memberEmailTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    memberEmailTextView.setId(R.id.memberEmailLogoId);
                    memberEmailTextView.setTextColor(activity.getResources().getColor(R.color.color43));


                    CircleImageView profileImageVW = new CircleImageView(activity);
                    RelativeLayout.LayoutParams llpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(200, 200);
                    llpInnerProfileImageVWLayout.setMargins(0, 10, 0, 0);
                    llpInnerProfileImageVWLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                    profileImageVW.setId(R.id.memberLogoId);

                    if(!s3ImageClass.hasPermission())
                    {
                        checkForPermission = s3ImageClass.getPermission();
                    }

                    if(s3ImageClass.hasPermission() && checkForPermission)
                    {

                        if(s3ImageClass.confirmIfImageInPhone(memberObject.getString("userName")))
                        {
                            profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(memberObject.getString("userName")));
                        }

                        else {
                            s3ImageClass = new S3ImageClass(activity,memberObject.getString("userName"), "profilepicfolder");
                            Bitmap bitMap = s3ImageClass.getImageBitMap();
                            if (s3ImageClass.isObjectExists()) {
                                if(s3ImageClass.hasPermission()) {
                                    s3ImageClass.writeToPhone(memberObject.getString("userName"), bitMap);
                                }
                                profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                                profileImageVW.invalidate();
                            }
                            else
                            {
                                Picasso.with(activity).load("https://ui-avatars.com/api/?name="+memberObject.getString("firstName") + " " + memberObject.getString("lastName")+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                            }
                        }
                    }




                    outerLinearLayout.addView(profileImageVW);
                    outerLinearLayout.addView(memberNameTextView);
                    outerLinearLayout.addView(memberEmailTextView);
                    outerLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                         //   viewProjectMemeberAllTasksReview = new ViewProjectMemeberAllTasksReview(activity, memberEmailTextView.getText().toString(), memberNameTextView.getText().toString(),projectName);

                            // projectMemberAllTasksView(memberEmailTextView.getText().toString(), memberNameTextView.getText().toString());
                        }
                    });

                    memeberLayoutIdHSView.addView(outerLinearLayout);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
