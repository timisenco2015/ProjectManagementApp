package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class DelegateProjectMemberTableDisplayClass {
    private Activity activity;
    private TableLayout memberListLayout;
    ArrayList<String> selectedMemebersArrayList;
    private String groupName;
    private HttpRequestClass httpRequestClass;
    private JSONArray selectedMemberJSONArray;
    private S3ImageClass s3ImageClass;
private Typeface fontAwesomeIcon;
    private String projectName;
    private SharedPreferences sharedPreferences;
    private String userName;
    private String firstName;
    private String lastName;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private JSONArray notificationsUserEmailJSONArray;
private Button del_unDelMembersButton;
    public DelegateProjectMemberTableDisplayClass(final Activity activity, TableLayout membersSelectedDelegateOuterTableLayout, ArrayList<String> selectedMemebersArrayList, String groupName, Button del_unDelMembersButton) {
        this.memberListLayout = membersSelectedDelegateOuterTableLayout;
        this.activity = activity;
        this.groupName = groupName;
        this.del_unDelMembersButton=del_unDelMembersButton;
        selectedMemberJSONArray = new JSONArray();
        this.selectedMemebersArrayList = selectedMemebersArrayList;
        notificationsUserEmailJSONArray = new JSONArray();
        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        lastName = sharedPreferences.getString("lastName", null);
        firstName = sharedPreferences.getString("firstName", null);
        userName = sharedPreferences.getString("userName", null);
        projectName = sharedPreferences.getString("projectName", null);
        fontAwesomeIcon = Typeface.createFromAsset(activity.getAssets(), "font/fontawesome-webfont.ttf");
        int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        this.s3ImageClass = new S3ImageClass();
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                /// friendsSuggestionsLayout.friendSuggestnListVwLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                friendSuggestnListVwNormalScrnSizeLT();
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
        del_unDelMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String result = delegateProjectToMembers();
               if(result.equalsIgnoreCase("Record Inserted"))
               {
                   sendNotificationsToUsers();
                   Toast.makeText(activity,"Project delegated to members",Toast.LENGTH_LONG).show();

               }
               else if ((result.equalsIgnoreCase("Record Not Inserted")))
               {
                   Toast.makeText(activity,"Project not delegated to members",Toast.LENGTH_LONG).show();
               }
                Intent intent = new Intent(activity,DelegateUndelegateProjectActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    //sendNotificationsToUsers()
    public void friendSuggestnListVwNormalScrnSizeLT() {
        removeTableLayoutRow();
        selectedMemberJSONArray=new JSONArray();
            for (int i = 0; i < selectedMemebersArrayList.size(); i=(i+2)) {
                Log.e("==++sc",selectedMemebersArrayList.toString());


                    s(i);
                   if((selectedMemebersArrayList.size()-1)>=((i+1))) {
                       t(i + 1);
                   }
                }


    }



    public void s(int rowIndex)
    {
        try
        {
        String getMemberInString = selectedMemebersArrayList.get(rowIndex);

        JSONObject tempObject = new JSONObject(getMemberInString);

        tempObject.put("canCreateTask", false);
        tempObject.put("canAssignTask", false);
        tempObject.put("canDelegateProject", false);
        tempObject.put("canRateMember", false);
        tempObject.put("isSupervisor", false);

        selectedMemberJSONArray.put(tempObject);

        String memberUserName = tempObject.getString("userName");
        String firstName = tempObject.getString("firstName");
        String lastName = tempObject.getString("lastName");
JSONObject notficationObject = new JSONObject();
            notficationObject.put("userName",memberUserName);
            notficationObject.put("groupName",groupName);
            notificationsUserEmailJSONArray.put(notficationObject);

        TableRow tableRow = new TableRow(activity);
        TableRow.LayoutParams tableRowLTP = new TableRow.LayoutParams(300, 140);
        tableRowLTP.setMargins(20,20,0,0);
        tableRow.setBackground(activity.getResources().getDrawable(R.drawable.delegate_undelegate_tablerowborder1));
        tableRow.setLayoutParams(tableRowLTP);


        LinearLayout memberDetailsLinearLayout = new LinearLayout(activity);
        TableRow.LayoutParams memberDetailsLLP = new TableRow.LayoutParams(240, 180);
        memberDetailsLLP.setMargins(20, 20, 0, 0);
        memberDetailsLinearLayout.setLayoutParams(memberDetailsLLP);

        CircleImageView profileImageVW = new CircleImageView(activity);
        TableRow.LayoutParams llpInnerProfileImageVWLayout = new TableRow.LayoutParams(120, 120);
        profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
        profileImageVW.setClickable(true);
        profileImageVW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.friendprofilereviewlayout);
                ImageView closeFrndReviewDialog = (ImageView) dialog.findViewById(R.id.closeFrndReviewDialogId);
                closeFrndReviewDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();


            }
        });
        profileImageVW.setId(R.id.memberLogoId);

            if(s3ImageClass.confirmIfImageInPhone(memberUserName))
            {
                profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(memberUserName));
            }
            else {
                s3ImageClass = new S3ImageClass(activity,memberUserName, "profilepicfolder");
                Bitmap bitMap = s3ImageClass.getImageBitMap();
                if (s3ImageClass.isObjectExists()) {
                    s3ImageClass.writeToPhone(memberUserName,bitMap);
                    profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                    profileImageVW.invalidate();
                }
                else
                {
                    Picasso.with(activity).load("https://ui-avatars.com/api/?name="+firstName + " " + lastName+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                }
            }

        TableRow.LayoutParams friendNameTxtViewLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        final TextView friendNameTextView = new TextView(activity);
        friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
        friendNameTextView.setTextSize(15);
        friendNameTextView.setText(memberUserName);
        // friendNameTextView.setText(allFriendsListJSONArray.getJSONArray(i).getString(1)+" "+allFriendsListJSONArray.getJSONArray(i).getString(2));
        friendNameTextView.setGravity(Gravity.CENTER_VERTICAL);
        friendNameTextView.setSingleLine(true);
            friendNameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
        friendNameTxtViewLayout.setMargins(40, 20, 0, 0);


        memberDetailsLinearLayout.addView(profileImageVW);
        memberDetailsLinearLayout.addView(friendNameTextView);

        LinearLayout memberCanAssignTaskLinearLayout = new LinearLayout(activity);
        TableRow.LayoutParams memberCanAssignTaskLLP = new TableRow.LayoutParams(180, 70);

        memberCanAssignTaskLLP.setMargins(0, 30, 0, 0);
        memberCanAssignTaskLinearLayout.setPadding(190, 0, 0, 0);
        memberCanAssignTaskLinearLayout.setLayoutParams(memberCanAssignTaskLLP);

       final CheckBox canAssignTaskCheckBox = new CheckBox(activity);
        canAssignTaskCheckBox.setScaleX(.6f);
        canAssignTaskCheckBox.setId(rowIndex);
        canAssignTaskCheckBox.setScaleY(.6f);
        canAssignTaskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TableRow row = (TableRow) buttonView.getParent().getParent();

                try {
                    if (isChecked) {


                        selectedMemberJSONArray.getJSONObject(canAssignTaskCheckBox.getId()).put("canAssignTask", true);


                    } else {
                        selectedMemberJSONArray.getJSONObject(canAssignTaskCheckBox.getId()).put("canAssignTask", false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });




        memberCanAssignTaskLinearLayout.addView(canAssignTaskCheckBox);


        LinearLayout memberCanCreateTaskLinearLayout = new LinearLayout(activity);
        TableRow.LayoutParams memberCanCreateTaskLLP = new TableRow.LayoutParams(180, 70);
        memberCanCreateTaskLLP.setMargins(0, 30, 0, 0);
        memberCanCreateTaskLinearLayout.setPadding(190, 0, 0, 0);
        memberCanCreateTaskLinearLayout.setLayoutParams(memberCanCreateTaskLLP);

        final CheckBox canCreateTaskCheckBox = new CheckBox(activity);
        canCreateTaskCheckBox.setScaleX(.6f);
            canCreateTaskCheckBox.setId(rowIndex);
        canCreateTaskCheckBox.setScaleY(.6f);
        canCreateTaskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TableRow row = (TableRow) buttonView.getParent().getParent();

                try {
                    if (isChecked) {



                        selectedMemberJSONArray.getJSONObject(canCreateTaskCheckBox.getId()).put("canCreateTask", true);


                    } else {
                        selectedMemberJSONArray.getJSONObject(canCreateTaskCheckBox.getId()).put("canCreateTask", false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        memberCanCreateTaskLinearLayout.addView(canCreateTaskCheckBox);


        RelativeLayout memberCanDelegateProjectLinearLayout = new RelativeLayout(activity);
        TableRow.LayoutParams memberCanDelegateProjectLLP = new TableRow.LayoutParams(180, 70);
        memberCanDelegateProjectLLP.setMargins(0, 30, 0, 0);
        memberCanDelegateProjectLinearLayout.setLayoutParams(memberCanDelegateProjectLLP);
        memberCanDelegateProjectLinearLayout.setPadding(190, 0, 0, 0);
       final CheckBox memberCanDelegateProjectCheckBox = new CheckBox(activity);
        memberCanDelegateProjectCheckBox.setScaleX(.6f);
            memberCanDelegateProjectCheckBox.setId(rowIndex);
        memberCanDelegateProjectCheckBox.setScaleY(.6f);
        memberCanDelegateProjectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TableRow row = (TableRow) buttonView.getParent().getParent();

                try {
                    if (isChecked) {



                        selectedMemberJSONArray.getJSONObject(memberCanDelegateProjectCheckBox.getId()).put("canDelegateProject", true);


                    } else {
                        selectedMemberJSONArray.getJSONObject(memberCanDelegateProjectCheckBox.getId()).put("canDelegateProject", false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        memberCanDelegateProjectLinearLayout.addView(memberCanDelegateProjectCheckBox);

        LinearLayout canRateMemeberLinearLayout = new LinearLayout(activity);
        TableRow.LayoutParams canRateMemeberLLP = new TableRow.LayoutParams(180, 70);
        canRateMemeberLLP.setMargins(0, 30, 0, 0);
        canRateMemeberLinearLayout.setPadding(190, 0, 0, 0);
        canRateMemeberLinearLayout.setLayoutParams(canRateMemeberLLP);

      final  CheckBox canRateMemeberCheckBox = new CheckBox(activity);
        canRateMemeberCheckBox.setScaleX(.6f);
            canRateMemeberCheckBox.setId(rowIndex);
        canRateMemeberCheckBox.setScaleY(.6f);
        canRateMemeberCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TableRow row = (TableRow) buttonView.getParent().getParent();

                try {
                    if (isChecked) {


                        selectedMemberJSONArray.getJSONObject(canRateMemeberCheckBox.getId()).put("canRateMember", true);


                    } else {
                        selectedMemberJSONArray.getJSONObject(canRateMemeberCheckBox.getId()).put("canRateMember", false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        canRateMemeberLinearLayout.addView(canRateMemeberCheckBox);


            RelativeLayout isProjectSupervisorLinearLayout = new RelativeLayout(activity);
            TableRow.LayoutParams isProjectSupervisorLLP = new TableRow.LayoutParams(180, 70);
            isProjectSupervisorLLP.setMargins(0, 30, 0, 0);
            isProjectSupervisorLinearLayout.setLayoutParams(memberCanDelegateProjectLLP);
            isProjectSupervisorLinearLayout.setPadding(190, 0, 0, 0);
           final CheckBox isProjectSupervisorCheckBox = new CheckBox(activity);
            isProjectSupervisorCheckBox.setScaleX(.6f);
            isProjectSupervisorCheckBox.setId(rowIndex);
            isProjectSupervisorCheckBox.setScaleY(.6f);
            isProjectSupervisorCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    TableRow row = (TableRow) buttonView.getParent().getParent();

                    try {
                        if (isChecked) {
                            Log.e("==++sb1",""+isProjectSupervisorCheckBox.getId());
                            selectedMemberJSONArray.getJSONObject( isProjectSupervisorCheckBox.getId()).put("isSupervisor", true);


                        } else {
                            selectedMemberJSONArray.getJSONObject(isProjectSupervisorCheckBox.getId()).put("isSupervisor", false);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            isProjectSupervisorLinearLayout.addView(isProjectSupervisorCheckBox);




            LinearLayout removeMemberLinearLayout = new LinearLayout(activity);
        TableRow.LayoutParams removeMemberLLP = new TableRow.LayoutParams(180, 70);
        removeMemberLLP.setMargins(0, 30, 0, 0);
        removeMemberLinearLayout.setPadding(190, 0, 0, 0);
        removeMemberLinearLayout.setLayoutParams(removeMemberLLP);


        TableRow.LayoutParams removeMemberTxtViewLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        final TextView removeMemberTextView = new TextView(activity);
        removeMemberTextView.setLayoutParams(removeMemberTxtViewLayout);
        removeMemberTextView.setTextSize(15);
        removeMemberLinearLayout.setClickable(true);
        removeMemberTextView.setGravity(Gravity.CENTER_VERTICAL);
        removeMemberTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        removeMemberTextView.setTextColor(activity.getResources().getColor(R.color.color22));
        removeMemberTextView.setTypeface(fontAwesomeIcon);
        removeMemberTextView.setId(rowIndex);
        removeMemberTextView.setText(activity.getResources().getString(R.string.closebutton));
        removeMemberLinearLayout.addView(removeMemberTextView);
        removeMemberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int index = removeMemberTextView.getId();
                Log.e("==++sa1",""+index);
                selectedMemebersArrayList.remove(index);
                selectedMemberJSONArray.remove(index);
                notificationsUserEmailJSONArray.remove(index);
                friendSuggestnListVwNormalScrnSizeLT();
            }
        });




        tableRow.addView(memberDetailsLinearLayout);
            tableRow.addView(memberCanCreateTaskLinearLayout);
        tableRow.addView(memberCanAssignTaskLinearLayout);
        tableRow.addView(memberCanDelegateProjectLinearLayout);
        tableRow.addView(canRateMemeberLinearLayout);
            tableRow.addView(isProjectSupervisorLinearLayout);

        tableRow.addView( removeMemberLinearLayout);


        memberListLayout.addView(tableRow, tableRowLTP);


} catch (JSONException e) {
        e.printStackTrace();

        }
    }




















    public void t(int rowIndex)
    {
        try
        {
            String getMemberInString = selectedMemebersArrayList.get(rowIndex);

            JSONObject tempObject = new JSONObject(getMemberInString);
            tempObject.put("canCreateTask", false);
            tempObject.put("canAssignTask", false);
            tempObject.put("canDelegateProject", false);
            tempObject.put("canRateMember", false);
            tempObject.put("isSupervisor", false);

            selectedMemberJSONArray.put(tempObject);
            String memberUserName = tempObject.getString("userName");
            String firstName = tempObject.getString("firstName");
            String lastName = tempObject.getString("lastName");

            JSONObject notficationObject = new JSONObject();
            notficationObject.put("userName",memberUserName);
            notficationObject.put("groupName",groupName);
            notificationsUserEmailJSONArray.put(notficationObject);

            TableRow tableRow = new TableRow(activity);
            TableRow.LayoutParams tableRowLTP = new TableRow.LayoutParams(300, 140);
            tableRowLTP.setMargins(20,20,0,0);
            tableRow.setBackground(activity.getResources().getDrawable(R.drawable.delegate_undelegate_tablerowborder2));
            tableRow.setLayoutParams(tableRowLTP);


            LinearLayout memberDetailsLinearLayout = new LinearLayout(activity);
            TableRow.LayoutParams memberDetailsLLP = new TableRow.LayoutParams(240, 180);
            memberDetailsLLP.setMargins(20, 20, 0, 0);
            memberDetailsLinearLayout.setLayoutParams(memberDetailsLLP);

            CircleImageView profileImageVW = new CircleImageView(activity);
            TableRow.LayoutParams llpInnerProfileImageVWLayout = new TableRow.LayoutParams(120, 120);
            profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
            profileImageVW.setClickable(true);
            profileImageVW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.setContentView(R.layout.friendprofilereviewlayout);
                    ImageView closeFrndReviewDialog = (ImageView) dialog.findViewById(R.id.closeFrndReviewDialogId);
                    closeFrndReviewDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();


                }
            });
            profileImageVW.setId(R.id.memberLogoId);
            if(s3ImageClass.confirmIfImageInPhone(memberUserName))
            {
                profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(memberUserName));
            }
            else {
                s3ImageClass = new S3ImageClass(activity,memberUserName, "profilepicfolder");
                Bitmap bitMap = s3ImageClass.getImageBitMap();
                if (s3ImageClass.isObjectExists()) {
                    s3ImageClass.writeToPhone(memberUserName,bitMap);
                    profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                    profileImageVW.invalidate();
                }
                else
                {
                    Picasso.with(activity).load("https://ui-avatars.com/api/?name="+firstName + " " + lastName+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                }
            }

            TableRow.LayoutParams friendNameTxtViewLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

            final TextView friendNameTextView = new TextView(activity);
            friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
            friendNameTextView.setTextSize(15);
            friendNameTextView.setText(memberUserName);
            // friendNameTextView.setText(allFriendsListJSONArray.getJSONArray(i).getString(1)+" "+allFriendsListJSONArray.getJSONArray(i).getString(2));
            friendNameTextView.setGravity(Gravity.CENTER_VERTICAL);
            friendNameTextView.setSingleLine(true);
            friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
            friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
            friendNameTxtViewLayout.setMargins(40, 20, 0, 0);


            memberDetailsLinearLayout.addView(profileImageVW);
            memberDetailsLinearLayout.addView(friendNameTextView);

            LinearLayout memberCanAssignTaskLinearLayout = new LinearLayout(activity);
            TableRow.LayoutParams memberCanAssignTaskLLP = new TableRow.LayoutParams(180, 70);

            memberCanAssignTaskLLP.setMargins(0, 20, 0, 0);
            memberCanAssignTaskLinearLayout.setPadding(190, 0, 0, 0);
            memberCanAssignTaskLinearLayout.setLayoutParams(memberCanAssignTaskLLP);

            final CheckBox canAssignTaskCheckBox = new CheckBox(activity);
            canAssignTaskCheckBox.setScaleX(.6f);
            canAssignTaskCheckBox.setId(rowIndex);
            canAssignTaskCheckBox.setScaleY(.6f);
            canAssignTaskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    try {
                        if (isChecked) {


                            selectedMemberJSONArray.getJSONObject(canAssignTaskCheckBox.getId()).put("canAssignTask", true);


                        } else {
                            selectedMemberJSONArray.getJSONObject(canAssignTaskCheckBox.getId()).put("canAssignTask", false);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });




            memberCanAssignTaskLinearLayout.addView(canAssignTaskCheckBox);


            LinearLayout memberCanCreateTaskLinearLayout = new LinearLayout(activity);
            TableRow.LayoutParams memberCanCreateTaskLLP = new TableRow.LayoutParams(180, 70);
            memberCanCreateTaskLLP.setMargins(0, 30, 0, 0);
            memberCanCreateTaskLinearLayout.setPadding(190, 0, 0, 0);
            memberCanCreateTaskLinearLayout.setLayoutParams(memberCanCreateTaskLLP);

           final CheckBox canCreateTaskCheckBox = new CheckBox(activity);
            canCreateTaskCheckBox.setScaleX(.6f);
            canCreateTaskCheckBox.setId(rowIndex);
            canCreateTaskCheckBox.setScaleY(.6f);
            canCreateTaskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    TableRow row = (TableRow) buttonView.getParent().getParent();

                    try {
                        if (isChecked) {




                            selectedMemberJSONArray.getJSONObject(canCreateTaskCheckBox.getId()).put("canCreateTask", true);


                        } else {
                            selectedMemberJSONArray.getJSONObject(canCreateTaskCheckBox.getId()).put("canCreateTask", false);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            memberCanCreateTaskLinearLayout.addView(canCreateTaskCheckBox);


            RelativeLayout memberCanDelegateProjectLinearLayout = new RelativeLayout(activity);
            TableRow.LayoutParams memberCanDelegateProjectLLP = new TableRow.LayoutParams(180, 70);
            memberCanDelegateProjectLLP.setMargins(0, 30, 0, 0);
            memberCanDelegateProjectLinearLayout.setLayoutParams(memberCanDelegateProjectLLP);
            memberCanDelegateProjectLinearLayout.setPadding(190, 0, 0, 0);
            final CheckBox memberCanDelegateProjectCheckBox = new CheckBox(activity);
            memberCanDelegateProjectCheckBox.setId(rowIndex);
            memberCanDelegateProjectCheckBox.setScaleX(.6f);
            memberCanDelegateProjectCheckBox.setScaleY(.6f);
            memberCanDelegateProjectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    TableRow row = (TableRow) buttonView.getParent().getParent();
                    try {
                        if (isChecked) {


                            selectedMemberJSONArray.getJSONObject(memberCanDelegateProjectCheckBox.getId()).put("canDelegateProject", true);


                        } else {
                            selectedMemberJSONArray.getJSONObject(memberCanDelegateProjectCheckBox.getId()).put("canDelegateProject", false);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });



            memberCanDelegateProjectLinearLayout.addView(memberCanDelegateProjectCheckBox);

            LinearLayout canRateMemeberLinearLayout = new LinearLayout(activity);
            TableRow.LayoutParams canRateMemeberLLP = new TableRow.LayoutParams(180, 70);
            canRateMemeberLLP.setMargins(0, 30, 0, 0);
            canRateMemeberLinearLayout.setPadding(180, 0, 0, 0);
            canRateMemeberLinearLayout.setLayoutParams(canRateMemeberLLP);

            final CheckBox canRateMemeberCheckBox = new CheckBox(activity);
            canRateMemeberCheckBox.setId(rowIndex);
            canRateMemeberCheckBox.setScaleX(.6f);
            canRateMemeberCheckBox.setScaleY(.6f);
            canRateMemeberCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    TableRow row = (TableRow) buttonView.getParent().getParent();

                    try {
                        if (isChecked) {


                            selectedMemberJSONArray.getJSONObject(canRateMemeberCheckBox.getId()).put("canRateMember", true);


                        } else {
                            selectedMemberJSONArray.getJSONObject(canRateMemeberCheckBox.getId()).put("canRateMember", false);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            canRateMemeberLinearLayout.addView(canRateMemeberCheckBox);

            RelativeLayout isProjectSupervisorLinearLayout = new RelativeLayout(activity);
            TableRow.LayoutParams isProjectSupervisorLLP = new TableRow.LayoutParams(180, 70);
            isProjectSupervisorLLP.setMargins(0, 30, 0, 0);
            isProjectSupervisorLinearLayout.setLayoutParams(memberCanDelegateProjectLLP);
            isProjectSupervisorLinearLayout.setPadding(190, 0, 0, 0);
            final CheckBox isProjectSupervisorCheckBox = new CheckBox(activity);
            isProjectSupervisorCheckBox.setScaleX(.6f);
            isProjectSupervisorCheckBox.setId(rowIndex);
            isProjectSupervisorCheckBox.setScaleY(.6f);
            isProjectSupervisorCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    TableRow row = (TableRow) buttonView.getParent().getParent();

                    try {
                        if (isChecked) {
                            Log.e("==++sb2",""+isProjectSupervisorCheckBox.getId());
                            selectedMemberJSONArray.getJSONObject( isProjectSupervisorCheckBox.getId()).put("isSupervisor", true);


                        } else {
                            selectedMemberJSONArray.getJSONObject(isProjectSupervisorCheckBox.getId()).put("isSupervisor", false);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            isProjectSupervisorLinearLayout.addView(isProjectSupervisorCheckBox);



            LinearLayout removeMemberLinearLayout = new LinearLayout(activity);
            TableRow.LayoutParams removeMemberLLP = new TableRow.LayoutParams(180, 70);
            removeMemberLLP.setMargins(0, 30, 0, 0);
            removeMemberLinearLayout.setPadding(180, 0, 0, 0);
            removeMemberLinearLayout.setLayoutParams(removeMemberLLP);


            TableRow.LayoutParams removeMemberTxtViewLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            final TextView removeMemberTextView = new TextView(activity);
            removeMemberTextView.setLayoutParams(removeMemberTxtViewLayout);
            removeMemberTextView.setTextSize(15);

            removeMemberLinearLayout.setClickable(true);
            removeMemberTextView.setGravity(Gravity.CENTER_VERTICAL);
            removeMemberTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
            removeMemberTextView.setTextColor(activity.getResources().getColor(R.color.color22));
            removeMemberTextView.setTypeface(fontAwesomeIcon);
            removeMemberTextView.setId(rowIndex);
            removeMemberTextView.setText(activity.getResources().getString(R.string.closebutton));
            removeMemberLinearLayout.addView(removeMemberTextView);
            removeMemberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = removeMemberTextView.getId();
                    Log.e("==++sa2",""+index);
                    selectedMemebersArrayList.remove(index);
                    selectedMemberJSONArray.remove(index);
                    notificationsUserEmailJSONArray.remove(index);
                    friendSuggestnListVwNormalScrnSizeLT();
                }
            });







            tableRow.addView(memberDetailsLinearLayout);

            tableRow.addView(memberCanCreateTaskLinearLayout);
            tableRow.addView(memberCanAssignTaskLinearLayout);
            tableRow.addView(memberCanDelegateProjectLinearLayout);
            tableRow.addView(canRateMemeberLinearLayout);
            tableRow.addView(isProjectSupervisorLinearLayout);
            tableRow.addView( removeMemberLinearLayout);


            memberListLayout.addView(tableRow, tableRowLTP);


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }




    public JSONArray getResult()
    {
        return selectedMemberJSONArray;
    }

    public void refreshTable()
    {
        int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                /// friendsSuggestionsLayout.friendSuggestnListVwLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                friendSuggestnListVwNormalScrnSizeLT();
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
    }


    public void removeTableLayoutRow() {



        while ( memberListLayout.getChildCount() > 1) {
            TableRow row = (TableRow) memberListLayout.getChildAt(1);
            memberListLayout.removeView(row);
        }
    }

    public String delegateProjectToMembers()
    {
        String  result = null;
        String delegatedDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("isActive", "true");
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("selectedMemberInString", selectedMemberJSONArray.toString());
            postDataParams.put("projectOriginalOwner", false);
            postDataParams.put("dateProjectDelegated", delegatedDate);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/inpod/",postDataParams, activity,"text/plain", "application/json");
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
            postDataParams.put("nameOfUser", memberUserName);
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


    public String createNotifications(String notiSenderName, String allNotiRecieverNamesList,  String entityId,String entityType, String groupName,String projectName)
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


    public void sendNotificationsToUsers()
    {
        try
        {
            String result =null;

            result= createNotifications(userName, notificationsUserEmailJSONArray.toString(),  "03","Requested",groupName,projectName);


            if(result.equalsIgnoreCase("Record Inserted")) {
                for (int i = 0; i < notificationsUserEmailJSONArray.length(); i++) {

                    result = getOneSignalIdAttachedUserName(notificationsUserEmailJSONArray.getJSONObject(i).getString("userName"));
                    if (result!=null) {

                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+firstName+" "+lastName+ "has undelegated you from for project name " + projectName +" under group"+groupName+ "'}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }





}
