package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import java.util.List;
import java.util.Locale;

import ZihronChatApp.ZihronWorkChat.Channels.ZWCGroupChannel;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ProjectGroupCreateClass {
    private Activity activity;
    private boolean[] isSelected;
    private JSONArray allFriendsListJSONArray;
    private String projectName;
    private JSONArray selecetFriendListJSONArray;
    private LinearLayout membersListLinearLayout;
    private S3ImageClass s3ImageClass;
    private LinearLayout    selectedMemberHViewLinearlayout;
    private ImageButton newGroupButtonClick;
    private JSONArray memberUserNameJSONArray;
    private EditText  groupNameEditText;
    private boolean isProjectGroupNameExist_MaxtError;
    private String userName;
    private Gson googleJson;
    private String firstName, lastName;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    public static PopupWindow popUp;
    private HttpRequestClass httpRequestClass;
    private ArrayList<String> selectedMemebrArrayList;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private  String groupName;
    private Spinner groupLevelSpinner;
    private String groupLevel;
    private  List<String> projectLevelArray;
    private ZWCGroupChannel zihronChatAppGroupChannel;
    public ProjectGroupCreateClass(final Activity activity,final String projectName, String userName,String firstName, String lastName, LinearLayout membersListLinearLayout, LinearLayout  selectedMemberHViewLinearlayout, ImageButton newGroupButtonClick,final EditText  groupNameEditText, Spinner groupLevelSpinner,final List<String> projectLevelArray)
    {
        if(ProjectGroupRemoveMemberClass.popUp!=null && ProjectGroupRemoveMemberClass.popUp.isShowing())
        {
            ProjectGroupRemoveMemberClass.popUp.dismiss();
        }
        if(ProjectGroupAddMemberClass.popUp!=null && ProjectGroupAddMemberClass.popUp.isShowing())
        {
            ProjectGroupAddMemberClass.popUp.dismiss();
        }
        this.activity = activity;
        this.projectName = projectName;
        this.userName =userName;
        this.firstName = firstName;
        this.projectLevelArray=projectLevelArray;
        this.lastName = lastName;
        isProjectGroupNameExist_MaxtError = false;
        this.s3ImageClass = new S3ImageClass();
        this.groupNameEditText=groupNameEditText;

        this.newGroupButtonClick=newGroupButtonClick;
        this.membersListLinearLayout= membersListLinearLayout;
        this.selectedMemberHViewLinearlayout = selectedMemberHViewLinearlayout;
        this.groupLevelSpinner = groupLevelSpinner;
        groupLevel = "Task Assigned";
        groupLevelSpinner.setEnabled(true);

        newGroupButtonClick.setEnabled(false);
        zihronChatAppGroupChannel = new ZWCGroupChannel(activity);
        groupLevelSpinner.setSelection(0,false);
        groupLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupLevel = projectLevelArray.get(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        newGroupButtonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupWithMembers();
            }
        });

      String result = null;

        try {
             result = projectAllMembers();
            allFriendsListJSONArray = new JSONArray(result);
            if(allFriendsListJSONArray.length()<=0)
            {

                    popUpView("No members for this group yet or members still pending");

            }
            else
            {


                int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                switch(screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        newGroupFriendsListVwLargeScrnSizeLT();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                        newGroupFriendsListVwNormalScrnSizeLT();
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

        popUp.showAtLocation(customView, Gravity.NO_GRAVITY, 10, 300);




    }

    public void newGroupFriendsListVwNormalScrnSizeLT()
    {
        //  isSelected = new boolean[allFriendsListJSONArray.length()];
        isSelected = new boolean[allFriendsListJSONArray.length()];
        selecetFriendListJSONArray = new JSONArray();
        selectedMemebrArrayList = new ArrayList<String>();
        membersListLinearLayout.removeAllViews();

        selectedMemberHViewLinearlayout.removeAllViews();
        for (int i = 0; i < allFriendsListJSONArray.length(); i++) {
    try {
        JSONObject tempObject = new JSONObject(allFriendsListJSONArray.getString(i));
             final   LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
                outerLinearLayoutLP.setMargins(40,0,0,10);
                outerLinearLayout.setClickable(true);
        outerLinearLayout.setId(i);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                CircleImageView profileImageVW = new CircleImageView(activity);
                LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(120, 120);
                llpInnerProfileImageVWLayout.setMargins(0,40,0,0);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setClickable(true);
                profileImageVW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.friendprofilereviewlayout);
                        ImageView closeFrndReviewDialog = (ImageView)dialog.findViewById(R.id.closeFrndReviewDialogId);
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



        if(s3ImageClass.confirmIfImageInPhone(tempObject.getString("recieverUsername")))
        {
            profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(tempObject.getString("recieverUsername")));
        }
        else {
            s3ImageClass = new S3ImageClass(activity,tempObject.getString("recieverUsername"), "profilepicfolder");

            if (s3ImageClass.isObjectExists()) {
                Bitmap bitMap = s3ImageClass.getImageBitMap();
                s3ImageClass.writeToPhone(tempObject.getString("recieverUsername"),bitMap);
                profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                profileImageVW.invalidate();
            }
            else
            {
                Picasso.with(activity).load("https://ui-avatars.com/api/?name="+tempObject.getString("firstName") + " " + tempObject.getString("lastName")+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
            }
        }

                LinearLayout innerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerLinearLayoutParams.setMargins(40,45,0,0);
                innerLinearLayout.setLayoutParams(innerLinearLayoutParams);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerLinearLayout.setGravity(Gravity.CENTER_VERTICAL);



                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(15);

                friendNameTextView.setText(tempObject.getString("firstName")+" "+tempObject.getString("lastName"));

                friendNameTextView.setSingleLine(true);
                friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                friendNameTxtViewLayout.setMargins(0, 0, 0, 0);


                LinearLayout.LayoutParams friendUserNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendUsernameTextView = new TextView(activity);
                friendUsernameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendUsernameTextView.setTextSize(15);
                friendUsernameTextView.setText(tempObject.getString("recieverUsername"));
                friendUsernameTextView.setSingleLine(true);

                friendUsernameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendUsernameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                friendUserNameTxtViewLayout.setMargins(0, 0, 0, 0);




                LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerSelectionLinearLayoutLP.setMargins(20,50,0,0);
                innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);
                innerSelectionLinearLayout.setGravity(Gravity.RIGHT);


                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);


                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);

                outerLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int index =  outerLinearLayout.getId();
                        int index2=0;
                        JSONObject tempSelectedObject = null;

                        try {

                            tempSelectedObject = new JSONObject(allFriendsListJSONArray.getString(index));
                            newGroupButtonClick.setEnabled(true);


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        LinearLayout tempLinearLayout = (LinearLayout) membersListLinearLayout.getChildAt(index);
                    //    createEntryLinearLayout.setVisibility(View.VISIBLE);
                        if (isSelected[index])
                        {
                            try {
                                index2 = selectedMemebrArrayList.indexOf(tempSelectedObject.getString("recieverUsername"));
                                selecetFriendListJSONArray.remove(index2);
                                selectedMemebrArrayList.remove(index2);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            tempLinearLayout.setBackgroundColor(0);
                            isSelected[index]=false;
                            selectedMemberHViewLinearlayout.removeViewAt(index2);
                            if(selecetFriendListJSONArray.length()==0)
                            {
                                newGroupButtonClick.setEnabled(false);
                         //       createEntryLinearLayout.setVisibility(View.GONE);
                            }

                        }
                        else
                        {
                            try {
                                selecetFriendListJSONArray.put(tempSelectedObject.getString("recieverUsername"));
                                selectedMemebrArrayList.add(tempSelectedObject.getString("recieverUsername"));

                                tempLinearLayout.setBackgroundColor(activity.getResources().getColor(R.color.color32));
                                createSelectedFriendHorizontalNormalView(tempSelectedObject,index);
                                isSelected[index]=true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }




                        }
                    }
                });


        membersListLinearLayout.addView(outerLinearLayout);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    public void newGroupFriendsListVwLargeScrnSizeLT()
    {
        //  isSelected = new boolean[allFriendsListJSONArray.length()];
        isSelected = new boolean[allFriendsListJSONArray.length()];
        selecetFriendListJSONArray = new JSONArray();
        selectedMemebrArrayList = new ArrayList<String>();
        membersListLinearLayout.removeAllViews();

        selectedMemberHViewLinearlayout.removeAllViews();
        for (int i = 0; i < allFriendsListJSONArray.length(); i++) {
            try {
                JSONObject tempObject = new JSONObject(allFriendsListJSONArray.getString(i));
                final   LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                outerLinearLayoutLP.setMargins(40,0,0,10);
                outerLinearLayout.setClickable(true);
                outerLinearLayout.setId(i);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                CircleImageView profileImageVW = new CircleImageView(activity);
                LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(150, 150);
                llpInnerProfileImageVWLayout.setMargins(20,20,0,0);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setClickable(true);
                profileImageVW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.friendprofilereviewlayout);
                        ImageView closeFrndReviewDialog = (ImageView)dialog.findViewById(R.id.closeFrndReviewDialogId);
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



                if(s3ImageClass.confirmIfImageInPhone(tempObject.getString("recieverUsername")))
                {
                    profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(tempObject.getString("recieverUsername")));
                }
                else {
                    s3ImageClass = new S3ImageClass(activity,tempObject.getString("recieverUsername"), "profilepicfolder");

                    if (s3ImageClass.isObjectExists()) {
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        s3ImageClass.writeToPhone(tempObject.getString("recieverUsername"),bitMap);
                        profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                        profileImageVW.invalidate();
                    }
                    else
                    {
                        Picasso.with(activity).load("https://ui-avatars.com/api/?name="+tempObject.getString("firstName") + " " + tempObject.getString("lastName")+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                    }
                }

                LinearLayout innerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerLinearLayoutParams.setMargins(40,55,0,0);
                innerLinearLayout.setLayoutParams(innerLinearLayoutParams);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerLinearLayout.setGravity(Gravity.CENTER_VERTICAL);



                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(22);

                friendNameTextView.setText(tempObject.getString("firstName")+" "+tempObject.getString("lastName"));

                friendNameTextView.setSingleLine(true);
                friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                friendNameTxtViewLayout.setMargins(0, 0, 0, 10);


                LinearLayout.LayoutParams friendUserNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendUsernameTextView = new TextView(activity);
                friendUsernameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendUsernameTextView.setTextSize(22);
                friendUsernameTextView.setText(tempObject.getString("recieverUsername"));
                friendUsernameTextView.setSingleLine(true);

                friendUsernameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendUsernameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                friendUserNameTxtViewLayout.setMargins(0, 0, 0, 0);



                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);


                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);

                outerLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int index =  outerLinearLayout.getId();
                        int index2=0;
                        JSONObject tempSelectedObject = null;

                        try {

                            tempSelectedObject = new JSONObject(allFriendsListJSONArray.getString(index));
                            newGroupButtonClick.setEnabled(true);


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        LinearLayout tempLinearLayout = (LinearLayout) membersListLinearLayout.getChildAt(index);
                        //    createEntryLinearLayout.setVisibility(View.VISIBLE);
                        if (isSelected[index])
                        {
                            try {
                                index2 = selectedMemebrArrayList.indexOf(tempSelectedObject.getString("recieverUsername"));
                                selecetFriendListJSONArray.remove(index2);
                                selectedMemebrArrayList.remove(index2);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            tempLinearLayout.setBackgroundColor(0);
                            isSelected[index]=false;
                            selectedMemberHViewLinearlayout.removeViewAt(index2);
                            if(selecetFriendListJSONArray.length()==0)
                            {
                                newGroupButtonClick.setEnabled(false);
                                //       createEntryLinearLayout.setVisibility(View.GONE);
                            }

                        }
                        else
                        {
                            try {
                                selecetFriendListJSONArray.put(tempSelectedObject.getString("recieverUsername"));
                                selectedMemebrArrayList.add(tempSelectedObject.getString("recieverUsername"));

                                tempLinearLayout.setBackgroundColor(activity.getResources().getColor(R.color.color32));
                                createSelectedFriendHorizontalLargeView(tempSelectedObject,index);
                                isSelected[index]=true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }




                        }
                    }
                });


                membersListLinearLayout.addView(outerLinearLayout);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }









    //-----------------------------------------------

    public void createSelectedFriendHorizontalNormalView(final JSONObject selectedJObject, int index)
    {

        LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        innerSelectionLinearLayout.setOrientation(LinearLayout.VERTICAL);
        innerSelectionLinearLayout.setId(index);
        innerSelectionLinearLayoutLP.setMargins(20,0,0,0);
        innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);

        CircleImageView profileImageVW = new CircleImageView(activity);
        RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
        rlpInnerProfileImageVWLayout.setMargins(35,0,0,0);
        profileImageVW.setLayoutParams(rlpInnerProfileImageVWLayout);
        profileImageVW.setId(R.id.memberLogoId);
        try {
            if(s3ImageClass.confirmIfImageInPhone(selectedJObject.getString("recieverUsername")))
            {
                profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(selectedJObject.getString("recieverUsername")));
            }
            else {
                s3ImageClass = new S3ImageClass(activity,selectedJObject.getString("recieverUsername"), "profilepicfolder");
                Bitmap bitMap = s3ImageClass.getImageBitMap();
                if (s3ImageClass.isObjectExists()) {
                    s3ImageClass.writeToPhone(selectedJObject.getString("recieverUsername"),bitMap);
                    profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                    profileImageVW.invalidate();
                }
                else
                {
                    Picasso.with(activity).load("https://ui-avatars.com/api/?name="+selectedJObject.getString("firstName")+" "+selectedJObject.getString("lastName")+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




        LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView friendNameTextView = new TextView(activity);
        friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
        friendNameTextView.setTextSize(15);
        friendNameTextView.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
        try {
            friendNameTextView.setText(selectedJObject.getString("recieverUsername"));

            friendNameTextView.setSingleLine(true);
            friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
            friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
            friendNameTxtViewLayout.setMargins(0, 10, 0, 0);




            final CircleImageView closeImageVW = new CircleImageView(activity);
            RelativeLayout.LayoutParams rlpcloseImageVWLayout = new RelativeLayout.LayoutParams(60, 60);
            rlpcloseImageVWLayout.addRule(RelativeLayout.RIGHT_OF,R.id.memberLogoId);
            rlpcloseImageVWLayout.addRule(RelativeLayout.ALIGN_BOTTOM);
            closeImageVW.setLayoutParams(rlpcloseImageVWLayout);
            closeImageVW.setBackground(activity.getResources().getDrawable(R.drawable.closeimage));
            closeImageVW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  LinearLayout tempLayout =(LinearLayout) closeImageVW.getParent().getParent();
                    int index1 = tempLayout.getId();
                    int index2 = 0;
                    try {
                        index2 = selectedMemebrArrayList.indexOf(selectedJObject.getString("recieverUsername"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    selecetFriendListJSONArray.remove(index2);
                    selectedMemebrArrayList.remove(index2);
                    if(selecetFriendListJSONArray.length()==0)
                    {
                    //    createEntryLinearLayout.setVisibility(View.GONE);
                    }
                    selectedMemberHViewLinearlayout.removeViewAt(index2);
                    LinearLayout tempLinearLayout = (LinearLayout) membersListLinearLayout.getChildAt(index1);
                    tempLinearLayout.setBackgroundColor(0);
                    isSelected[index1]=false;

                }
            });





            RelativeLayout innerSelectionRelativeLayout = new RelativeLayout(activity);
            RelativeLayout.LayoutParams innerSelectionRelativeLayoutLP = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            innerSelectionRelativeLayout.setLayoutParams(innerSelectionRelativeLayoutLP);

            innerSelectionRelativeLayout.addView(profileImageVW);
            innerSelectionRelativeLayout.addView(closeImageVW);

            innerSelectionLinearLayout.addView(innerSelectionRelativeLayout );
            innerSelectionLinearLayout.addView(friendNameTextView);
            selectedMemberHViewLinearlayout.addView(innerSelectionLinearLayout);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void createSelectedFriendHorizontalLargeView(final JSONObject selectedJObject, int index)
    {

        LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        innerSelectionLinearLayout.setOrientation(LinearLayout.VERTICAL);
        innerSelectionLinearLayout.setId(index);
        innerSelectionLinearLayoutLP.setMargins(20,0,0,0);
        innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);

        CircleImageView profileImageVW = new CircleImageView(activity);
        RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
        rlpInnerProfileImageVWLayout.setMargins(35,0,0,0);
        profileImageVW.setLayoutParams(rlpInnerProfileImageVWLayout);
        profileImageVW.setId(R.id.memberLogoId);
        try {
            if(s3ImageClass.confirmIfImageInPhone(selectedJObject.getString("recieverUsername")))
            {
                profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(selectedJObject.getString("recieverUsername")));
            }
            else {
                s3ImageClass = new S3ImageClass(activity,selectedJObject.getString("recieverUsername"), "profilepicfolder");
                Bitmap bitMap = s3ImageClass.getImageBitMap();
                if (s3ImageClass.isObjectExists()) {
                    s3ImageClass.writeToPhone(selectedJObject.getString("recieverUsername"),bitMap);
                    profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                    profileImageVW.invalidate();
                }
                else
                {
                    Picasso.with(activity).load("https://ui-avatars.com/api/?name="+selectedJObject.getString("firstName")+" "+selectedJObject.getString("lastName")+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




        LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView friendNameTextView = new TextView(activity);
        friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
        friendNameTextView.setTextSize(22);
        friendNameTextView.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
        try {
            friendNameTextView.setText(selectedJObject.getString("recieverUsername"));

            friendNameTextView.setSingleLine(true);
            friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
            friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
            friendNameTxtViewLayout.setMargins(0, 10, 0, 0);




            final CircleImageView closeImageVW = new CircleImageView(activity);
            RelativeLayout.LayoutParams rlpcloseImageVWLayout = new RelativeLayout.LayoutParams(60, 60);
            rlpcloseImageVWLayout.addRule(RelativeLayout.RIGHT_OF,R.id.memberLogoId);
            rlpcloseImageVWLayout.addRule(RelativeLayout.ALIGN_BOTTOM);
            closeImageVW.setLayoutParams(rlpcloseImageVWLayout);
            closeImageVW.setBackground(activity.getResources().getDrawable(R.drawable.closeimage));
            closeImageVW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout tempLayout =(LinearLayout) closeImageVW.getParent().getParent();
                    int index1 = tempLayout.getId();
                    int index2 = 0;
                    try {
                        index2 = selectedMemebrArrayList.indexOf(selectedJObject.getString("recieverUsername"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    selecetFriendListJSONArray.remove(index2);
                    selectedMemebrArrayList.remove(index2);
                    if(selecetFriendListJSONArray.length()==0)
                    {
                        //    createEntryLinearLayout.setVisibility(View.GONE);
                    }
                    selectedMemberHViewLinearlayout.removeViewAt(index2);
                    LinearLayout tempLinearLayout = (LinearLayout) membersListLinearLayout.getChildAt(index1);
                    tempLinearLayout.setBackgroundColor(0);
                    isSelected[index1]=false;

                }
            });





            RelativeLayout innerSelectionRelativeLayout = new RelativeLayout(activity);
            RelativeLayout.LayoutParams innerSelectionRelativeLayoutLP = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            innerSelectionRelativeLayout.setLayoutParams(innerSelectionRelativeLayoutLP);

            innerSelectionRelativeLayout.addView(profileImageVW);
            innerSelectionRelativeLayout.addView(closeImageVW);

            innerSelectionLinearLayout.addView(innerSelectionRelativeLayout );
            innerSelectionLinearLayout.addView(friendNameTextView);
            selectedMemberHViewLinearlayout.addView(innerSelectionLinearLayout);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void createGroupWithMembers()
    {
        ValidationClass validationClass = new ValidationClass();
        String error = validationClass.validationGroupName( groupNameEditText.getText().toString().trim());
        String result =  isProjectGroupNameExists(projectName,groupNameEditText.getText().toString().trim());
        if(!error.equalsIgnoreCase("field okay"))
                {
                    groupNameEditText.setError(error);
                }
                else if(selecetFriendListJSONArray.length()==0)
                {
                    Toast.makeText(activity,"No member selected",Toast.LENGTH_LONG).show();
                }

                else  if((groupNameEditText.getText().toString()).length()>=40)
            {
                groupNameEditText.setError("Maximum length allowd is 39");
                   // isProjectGroupNameExist_MaxtError=false;
                }
        else if(result.equalsIgnoreCase("False"))
        {
                    groupName = groupNameEditText.getText().toString().trim();
                    if(groupLevel.equalsIgnoreCase("Task Assign"))
                    {
                        groupName = "T "+groupName;
                    }
                    else if (groupLevel.equalsIgnoreCase("Supervisor"))
                {
                    groupName = "S "+groupName;
                 }
                    result = createGroup(projectName,userName,groupName, selecetFriendListJSONArray.toString(), groupLevel);
                        if(result.equalsIgnoreCase("Record Inserted"))
                        {
                            Intent pGCCIntent = new Intent(activity,ProjectMembersGroupInviteActivity.class);
                            googleJson = new Gson();
                            ArrayList<String> groupMemberArrayList =googleJson.fromJson( selecetFriendListJSONArray.toString(), ArrayList.class);
                            Log.e("++==f",groupMemberArrayList.toString()+"--"+groupName);
                            zihronChatAppGroupChannel.createGroupChannel(groupMemberArrayList,groupName);

                            activity.startActivity(pGCCIntent);
                            sendNotificationsToUsers();
                        }
                        else
                        {
                             builder = new AlertDialog.Builder(activity);
                            builder.setMessage("Unable to create your group at this moment. Please try again later");
                            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    groupNameEditText.setText("");
                                    alertDialog.dismiss();
                                }
                            });
                           alertDialog = builder.create();
                            alertDialog.show();
                        }

                }
                else
            {
                groupNameEditText.setError("This name already exists in our database. Please choose a different name.Thanks");

            }

    }

    public void sendNotificationsToUsers()
    {
        String result =null;
        try {
             result =  createNotifications(userName, selecetFriendListJSONArray.toString(),  "02","Requested",groupName,projectName);
             if(result.equalsIgnoreCase("Record Inserted")) {
                 for (int i = 0; i < selecetFriendListJSONArray.length(); i++) {

                     result = getOneSignalIdAttachedUserName(selecetFriendListJSONArray.getString(i));
                     OneSignal.postNotification(new JSONObject("{'contents': {'en':'You have project group add request from "+firstName+" "+lastName+"'}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);


                 }
             }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }




    public String projectAllMembers()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", userName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/faffpm/",postDataParams, activity,"application/json", "application/json");
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





    public String createGroup(String projectName,String userName,String groupName, String allMembersEmailsInString,String groupLevel)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        String dateGroupCreated= new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("dateGroupCreated", dateGroupCreated);
            postDataParams.put("groupOwner", userName);
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("levelOfGroup", groupLevel);
            postDataParams.put("statusOfRequest", "Pending");
            postDataParams.put("membersEmailsInString",  allMembersEmailsInString);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/inpg/",postDataParams, activity,"text/plain", "application/json");
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

    public String isProjectGroupNameExists(String projectName,String groupName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("nameOfProject", projectName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/ipngne/",postDataParams, activity,"text/plain", "application/json");
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
