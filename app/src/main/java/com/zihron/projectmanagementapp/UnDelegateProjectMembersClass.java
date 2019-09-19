package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetBehavior;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class UnDelegateProjectMembersClass {
    private Activity activity;
    private LinearLayout memberListLayout;
    private JSONArray membersListJSONArray;
    private ArrayList<String> selectedMemebersArrayList;
    boolean[] isSelectedMemberJSONArray;
    private S3ImageClass s3ImageClass;
    private String groupName;
    JSONArray finalSelectedMembersArrayList;
    private SharedPreferences sharedPreferences;
    private boolean checkForPermission = true;
    private Button del_unDelMembersButton;
    private UnDelegateProjectMemberTableDisplayClass UndelegateProjectMemberTableDisplayClass;

   public UnDelegateProjectMembersClass(Button getSelectedMembersButton, SearchView friendGroupSearchView, final Button del_unDelMembersButton, final LinearLayout groups_members_LinearLayout, final Activity activity, final TableLayout membersSelectedDelegateOuterTableLayout, String result, final BottomSheetBehavior bottomSheetBehavior) {
        this.memberListLayout = groups_members_LinearLayout;
        this.activity = activity;
       sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        groupName = sharedPreferences.getString("projectDefaultGroup", null);

        try {
            membersListJSONArray = new JSONArray(result);
            finalSelectedMembersArrayList =membersListJSONArray;
            isSelectedMemberJSONArray = new boolean[membersListJSONArray.length()];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        selectedMemebersArrayList = new ArrayList<String>();
        this.s3ImageClass = new S3ImageClass();
        this.del_unDelMembersButton=del_unDelMembersButton;

       layoutDisplaySize();
        friendGroupSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendGroupSearchViewList(newText);
                layoutDisplaySize();
                return false;
            }
        });


        getSelectedMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedMemebersArrayList.size()>0)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                   UndelegateProjectMemberTableDisplayClass = new  UnDelegateProjectMemberTableDisplayClass(activity, membersSelectedDelegateOuterTableLayout, selectedMemebersArrayList,groupName,del_unDelMembersButton);

                }
            }
        });

    }

    public void layoutDisplaySize()
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

    public void friendSuggestnListVwNormalScrnSizeLT()
    {
        memberListLayout.removeAllViews();
        try {


            for (int i = 0; i <finalSelectedMembersArrayList.length(); i++) {

               final String tempResult = finalSelectedMembersArrayList.getString(i);
                JSONObject tempObject = new JSONObject(tempResult);
                String recieverUserName = tempObject.getString("userName");
                String lastName = tempObject.getString("lastName");
                String firstName = tempObject.getString("firstName");

                final LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 180);
                outerLinearLayoutLP.setMargins(0,0,0,30);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                outerLinearLayout.setId(i);
                outerLinearLayout.setClickable(true);
                outerLinearLayout.setBackgroundColor( activity.getResources().getColor(R.color.color41));
                outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                ImageView profileImageVW = new ImageView(activity);
                profileImageVW.setBackground(activity.getDrawable(R.drawable.background4));
                LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(150, 150);
                llpInnerProfileImageVWLayout.setMargins(0,15,0,0);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setBackgroundColor(activity.getResources().getColor(R.color.color22));

                profileImageVW.setId(R.id.memberLogoId);

                if(!s3ImageClass.hasPermission())
                {
                    checkForPermission = s3ImageClass.getPermission();
                }

                if(s3ImageClass.hasPermission() && checkForPermission)
                {

                    if(s3ImageClass.confirmIfImageInPhone(recieverUserName))
                    {
                        profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(recieverUserName));
                    }

                    else {
                        s3ImageClass = new S3ImageClass(activity,recieverUserName, "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            if(s3ImageClass.hasPermission()) {
                                s3ImageClass.writeToPhone(recieverUserName, bitMap);
                            }
                            profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageVW.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+firstName + " " + lastName+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                        }
                    }
                }
                LinearLayout innerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerLinearLayoutParams.setMargins(40,12,0,0);
                innerLinearLayout.setLayoutParams(innerLinearLayoutParams);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerLinearLayout.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(15);
                friendNameTextView.setText(firstName+" "+lastName);
                friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));

                LinearLayout.LayoutParams friendUserNameTxtViewLayout = new LinearLayout.LayoutParams(400, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendUsernameTextView = new TextView(activity);
                friendUsernameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendUsernameTextView.setLayoutParams(friendUserNameTxtViewLayout);
                friendUsernameTextView.setTextSize(15);
                friendUsernameTextView.setText(recieverUserName);
                friendUsernameTextView.setSingleLine(true);
                friendUsernameTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                friendUsernameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendUsernameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                friendUserNameTxtViewLayout.setMargins(0, 6, 0, 0);


                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);

                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedIndex = outerLinearLayout.getId();
                        if(isSelectedMemberJSONArray[selectedIndex])
                        {
                            try {
                                selectedMemebersArrayList.remove(finalSelectedMembersArrayList.getString(selectedIndex));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            outerLinearLayout.setBackgroundColor( activity.getResources().getColor(R.color.color41));
                            isSelectedMemberJSONArray[selectedIndex]=false;
                        }
                        else
                        {
                            try {
                                selectedMemebersArrayList.add(finalSelectedMembersArrayList.getString(selectedIndex));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            outerLinearLayout.setBackgroundColor( activity.getResources().getColor(R.color.color15));
                            isSelectedMemberJSONArray[selectedIndex]=true;

                        }


                    }
                });

                memberListLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void friendGroupSearchViewList(String searchText)
    {


        finalSelectedMembersArrayList=null;
        finalSelectedMembersArrayList=new JSONArray();
        try {
            if(searchText!=null || searchText!="" || searchText.length()>0 ) {
                for (int i = 0; i < membersListJSONArray.length(); i++) {

                    String tempResult = membersListJSONArray.getString(i);
                    JSONObject tempObject = new JSONObject(tempResult);
                    if (tempObject.getString("userName").toLowerCase().startsWith(searchText.toLowerCase())) {
                        finalSelectedMembersArrayList.put(tempResult);
                    }
                }
            }
            else
            {
                finalSelectedMembersArrayList=null;
                finalSelectedMembersArrayList=membersListJSONArray;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
