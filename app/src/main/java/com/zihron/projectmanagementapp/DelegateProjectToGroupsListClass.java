package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DelegateProjectToGroupsListClass {
    private Activity activity;
    private LinearLayout groupsListLayout;
    private JSONArray groupsListJSONArray;
private SearchView friendGroupSearchView;
    private HttpRequestClass httpRequestClass;
private String groupName;
private String projectName;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> selectedMemebersArrayList;
    private ArrayList<String> selectedMemebersIndexArrayList;
    boolean[] isSelectedMemberJSONArray;
    JSONArray finalSelectedGroupArrayList;
    private DelegateProjectMemberTableDisplayClass delegateProjectMemberTableDisplayClass;
    public DelegateProjectToGroupsListClass(final Button getSelectedMembersButton, SearchView friendGroupSearchView, final Button del_unDelMembersButton, final LinearLayout groups_members_LinearLayout, final Activity activity, final TableLayout membersSelectedDelegateOuterTableLayout, String result,final BottomSheetBehavior bottomSheetBehavior)
    {
        this.groupsListLayout=groups_members_LinearLayout;
        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        projectName = sharedPreferences.getString("projectName", null);
        this.activity =activity;
        selectedMemebersArrayList = new ArrayList<String>();
        this.friendGroupSearchView = friendGroupSearchView;
        try {
            groupsListJSONArray = new JSONArray(result);
            finalSelectedGroupArrayList = groupsListJSONArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isSelectedMemberJSONArray  = new boolean[groupsListJSONArray.length()];
        selectedMemebersIndexArrayList = new ArrayList<String>();

        layoutDisplaySize();
        getSelectedMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String result = getProjectGroupNotDelegatedMembers();
               if (result!=null) {
                   selectedMemebersArrayList = new ArrayList<String>();
                   Gson googleJson = new Gson();
                   selectedMemebersArrayList = googleJson.fromJson(result, ArrayList.class);
                   bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                   delegateProjectMemberTableDisplayClass = new DelegateProjectMemberTableDisplayClass(activity, membersSelectedDelegateOuterTableLayout, selectedMemebersArrayList, groupName, del_unDelMembersButton);
               }
               else
               {
                   Toast.makeText(activity,"It seems that you have delegated this project to all members in this group",Toast.LENGTH_LONG).show();
               }
            }
        });

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
    }

    public void layoutDisplaySize()
    {
        int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                /// friendsSuggestionsLayout.friendSuggestnListVwLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                groupsSuggestnListVwNormalScrnSizeLT();
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
    private void groupsSuggestnListVwNormalScrnSizeLT()
    {
        groupsListLayout.removeAllViews();
        try {


            for (int i = 0; i < finalSelectedGroupArrayList.length(); i++) {


             final   JSONArray tempJSONArray = finalSelectedGroupArrayList.getJSONArray(i);


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

                    Picasso.with(activity).load("https://ui-avatars.com/api/?name="+(groupsListJSONArray.getJSONArray(i)).get(0).toString()+"&background=90a8a8&color=fff&size=128").into( profileImageVW);


                LinearLayout innerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerLinearLayoutParams.setMargins(40,12,0,0);
                innerLinearLayout.setLayoutParams(innerLinearLayoutParams);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerLinearLayout.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams groupNameTextViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final TextView groupNameTextView = new TextView(activity);
                groupNameTextView.setLayoutParams(groupNameTextViewLayout);
                groupNameTextView.setTextSize(15);
                groupNameTextView.setText(tempJSONArray.getString(0));
                groupNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                groupNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));

                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(15);
                friendNameTextView.setText("Group Created By: "+tempJSONArray.getString(2)+" "+tempJSONArray.getString(3));
                friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));


                outerLinearLayout.addView(profileImageVW);
                innerLinearLayout.addView(groupNameTextView);

                innerLinearLayout.addView(friendNameTextView);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        outerLinearLayout.setBackgroundColor( activity.getResources().getColor(R.color.color15));
                        try {
                            groupName = finalSelectedGroupArrayList.getJSONArray(outerLinearLayout.getId()).getString(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        changeSelectedLayoutBackground(outerLinearLayout.getId());

                    }
                });

                groupsListLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    private void changeSelectedLayoutBackground(int selectedIndex)
    {
        for(int i=0;  i< groupsListLayout.getChildCount(); i++)
        {
            if(selectedIndex!=i)
            {
                groupsListLayout.getChildAt(i).setBackgroundColor( activity.getResources().getColor(R.color.color41));
            }
        }
    }

    private String getProjectGroupNotDelegatedMembers()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfGroup", groupName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuagmfpd/",postDataParams, activity,"application/json", "application/json");
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

private void friendGroupSearchViewList(String searchText)
{
    finalSelectedGroupArrayList=null;
    finalSelectedGroupArrayList=new JSONArray();
    try {
        if(searchText!=null || searchText!="" || searchText.length()>0 ) {

            for (int i = 0; i < groupsListJSONArray.length(); i++) {

                JSONArray tempArray = groupsListJSONArray.getJSONArray(i);
                Log.e("++kmk",tempArray.getString(0));
                if (tempArray.getString(0).startsWith(searchText.toLowerCase())) {
                    finalSelectedGroupArrayList.put(tempArray);
                }
            }
        }
        else
        {
            finalSelectedGroupArrayList=null;
            finalSelectedGroupArrayList=groupsListJSONArray;
        }
    } catch (JSONException e) {
            e.printStackTrace();
        }

}



}
