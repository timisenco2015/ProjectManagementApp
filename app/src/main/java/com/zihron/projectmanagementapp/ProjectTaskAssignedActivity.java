package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class ProjectTaskAssignedActivity extends Fragment {
    private LinearLayout projectAssignedContainers;
    private Activity activity;
    private ImageButton newProjectActivityBtn;
    private ImageButton viewProjectActivityBtn;
    private String userName;
    private RadioGroup projectSelectByAlphabetRadioGrp;
    private SwipeRefreshLayout projectActivityRefresh;
    private ScrollView taskScrollView;
    private ProgressBarClass progressBarClass;
    public static PopupWindow projectTaskAssignedPopUpWindow=null;
private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    Typeface fontAwesomeIcon;
    private String projectOwnerUserEmail;
 public static PopupWindow popUp;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private HttpRequestClass httpRequestClass;
    private JSONArray projectAssignedList;
    public ProjectTaskAssignedActivity() {
        // Required empty public constructor

    }








    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();

        progressBarClass = new ProgressBarClass(activity);
        sharedPreferences = getActivity().getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        editor = sharedPreferences.edit();

      final  View rootView = inflater.inflate(R.layout.fragment_project_task_assigned, container, false);

        if( ProjectTaskAssignedActivity.popUp !=null)
        {
            ProjectTaskAssignedActivity.popUp.dismiss();
        }

        if( CalendarActivity.popUp !=null)
        {
            CalendarActivity.popUp.dismiss();
        }
      // CustomProjectAssignedListViewAdapter customProjectAssignedListViewAdapter = new CustomProjectAssignedListViewAdapter(getActivity(),null);
        newProjectActivityBtn = (ImageButton) rootView.findViewById(R.id.newProjectButtonClick);
        viewProjectActivityBtn = (ImageButton) rootView.findViewById(R.id.viewProjectButtonClick);
        projectAssignedContainers = (LinearLayout) rootView.findViewById(R.id.taskListContainerId);
        projectSelectByAlphabetRadioGrp = (RadioGroup) rootView.findViewById(R.id.projectSelectByAlphabetRGId);
//        taskScrollView = (ScrollView) rootView.findViewById(R.id.taskScrollViewId);
        fontAwesomeIcon = Typeface.createFromAsset(activity.getAssets(),"font/fontawesome-webfont.ttf");

        projectActivityRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.projectActivityRefreshId);
        projectActivityRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(popUp!=null && popUp.isShowing())
                {
                    popUp.dismiss();
                }
                getProjectTaskAssignedList("All");


                ((RadioButton)  projectSelectByAlphabetRadioGrp.getChildAt(0)).setTextColor(ContextCompat.getColor(activity, R.color.color13));
                changeSelectedRadiButtnInRadioGrpBckGround(((RadioButton)  projectSelectByAlphabetRadioGrp.getChildAt(0)).getId(),  projectSelectByAlphabetRadioGrp);
                projectActivityRefresh.setRefreshing(false);
            }

        });






        getProjectTaskAssignedList("All");
        ((RadioButton)  projectSelectByAlphabetRadioGrp.getChildAt(0)).setTextColor(ContextCompat.getColor(activity, R.color.color13));
        changeSelectedRadiButtnInRadioGrpBckGround(((RadioButton)  projectSelectByAlphabetRadioGrp.getChildAt(0)).getId(),  projectSelectByAlphabetRadioGrp);

        projectSelectByAlphabetRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton radioBtn = (RadioButton) rootView.findViewById(checkedId);
               String selectCriteria = radioBtn.getText().toString();
                getProjectTaskAssignedList(selectCriteria);
                radioBtn.setTextColor(ContextCompat.getColor(activity, R.color.color13));
                changeSelectedRadiButtnInRadioGrpBckGround(checkedId, projectSelectByAlphabetRadioGrp);



                }

            });
        newProjectActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nPAIntent = new Intent(activity, NewProjectActivity.class);

                ProjectTaskAssignedActivity.this.startActivity(nPAIntent);
            }
        });

        viewProjectActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nPAIntent = new Intent(activity, ProjectActivity.class);

                ProjectTaskAssignedActivity.this.startActivity(nPAIntent);
            }
        });

        return rootView;
    }

    public void changeSelectedRadiButtnInRadioGrpBckGround(int checkId, RadioGroup radioGroup)
    {

        for(int i=0; i<radioGroup.getChildCount(); i++)
        {
            RadioButton view = (RadioButton) radioGroup.getChildAt(i) ;
            if(view.getId()!=checkId)
            {
                view.setTextColor(activity.getResources().getColor(R.color.color12));

            }
        }
    }


    public void popUpViewNormal(String textMessage)
    {


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        TextView tv;

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popupwindowlayout,null);
        LinearLayout outerLinearlayout = (LinearLayout)customView.findViewById(R.id.popLayoutId);
        outerLinearlayout.removeAllViews();

        LinearLayout innerContainer = new LinearLayout(activity);
        innerContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llpChatPageInnerRLT =  llpChatPageInnerRLT = new LinearLayout.LayoutParams(780, 130);


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

        popUp.showAtLocation(customView, Gravity.NO_GRAVITY, 30, 430);




    }

    public void popUpViewLarge(String textMessage)
    {


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        TextView tv;

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popupwindowlayout,null);
        LinearLayout outerLinearlayout = (LinearLayout)customView.findViewById(R.id.popLayoutId);
        outerLinearlayout.removeAllViews();

        LinearLayout innerContainer = new LinearLayout(activity);
        innerContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llpChatPageInnerRLT =  llpChatPageInnerRLT = new LinearLayout.LayoutParams(520, 80);


        innerContainer.setLayoutParams(llpChatPageInnerRLT);


        LinearLayout.LayoutParams llpChatProjectNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMargins(30,20,0,0);
        projectNameTextView.setText(textMessage);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(22);
        projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color12));
        // projectNameTextView.setTypeface(activity.getResources().getFont(R.font.montserrat));




        final TextView closeImageVW = new TextView(activity);
        LinearLayout.LayoutParams rlpcloseImageVWLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rlpcloseImageVWLayout.setMargins(30,20,20,0);
        closeImageVW.setLayoutParams(rlpcloseImageVWLayout);
        closeImageVW.setClickable(true);
        closeImageVW.setText(getResources().getString(R.string.closebutton));
        closeImageVW.setTypeface(fontAwesomeIcon);
        closeImageVW.setTextSize(28);
        closeImageVW.setTextColor(getResources().getColor(R.color.color24));
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

        popUp.showAtLocation(customView, Gravity.NO_GRAVITY, 70, 230);




    }



    public void getProjectTaskAssignedList(String alphabet)
    {
        String result=null;

        if(alphabet.equalsIgnoreCase("All")) {
            result = projectAssignedList(userName, 0);

        }
        else {
            int alphabetToInt = (int) alphabet.charAt(0);
            result = projectAssignedList(userName, alphabetToInt);
        }
            try {

                projectAssignedList = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (popUp != null && popUp.isShowing()) {
                popUp.dismiss();
            }
            if (projectAssignedList.length() > 0) {


                generateAssignedTaskToUserView();
            } else {

                projectAssignedContainers.removeAllViews();
                int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                switch (screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        popUpViewLarge("No assigned project to display");
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                        popUpViewNormal("No assigned project to display");
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




    }


    public void generateAssignedTaskToUserView() {
        projectAssignedContainers.removeAllViews();

        for (int i = 0; i < projectAssignedList.length(); i++) {

            int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

            switch(screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    largeScreenSizeLayout(i);
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    normalScreenSizeLayout(i);
                    break;
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    xLargeScreenSizeLayout(i);
                    break;
                case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                    break;

                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                    smallScreenSizeLayour(i);
                    break;
                default:

            }



        }
    }


    public void smallScreenSizeLayour(int i)
    {
        LinearLayout innerContainer = new LinearLayout(activity);
        LinearLayout.LayoutParams llpChatPageInnerRLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        innerContainer.setOrientation(LinearLayout.VERTICAL);
        llpChatPageInnerRLT.setMargins(20, 0, 20, 20);
        innerContainer.setBackground(activity.getResources().getDrawable(R.drawable.projectassignedlistcurvedbackgrnd, null));
        innerContainer.setLayoutParams(llpChatPageInnerRLT);

        RelativeLayout.LayoutParams llpChatProjectNameTxtViewLayout = new RelativeLayout.LayoutParams(340, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMargins(10,10,0,0);
        llpChatProjectNameTxtViewLayout.setMarginStart(15);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(19);
        projectNameTextView.setTextColor(Color.parseColor("#1e272e"));
        //  projectNameTextView.setText("Testing");
        projectNameTextView.setTypeface(Typeface.DEFAULT_BOLD);

        //  projectNameTextView.setText("Testing");
        try {
            projectNameTextView.setText(projectAssignedList.getString(i));
            projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color23));
            //   projectNameTextView.setTypeface(Typeface.createFromFile("font/"));
            //    projectNameTextView.setTypeface(activity.getResources().getFont(R.font.roboto_regular));

        } catch (JSONException e) {
            e.printStackTrace();
        }



        RelativeLayout.LayoutParams circleDesignTextViewLayout = new RelativeLayout.LayoutParams(140, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView circleDesignTextView = new TextView(activity);
        circleDesignTextViewLayout.setMarginStart(15);
        circleDesignTextViewLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        circleDesignTextView.setLayoutParams(circleDesignTextViewLayout);
        circleDesignTextView.setTextSize(37);
        circleDesignTextView.setTypeface(fontAwesomeIcon);
        circleDesignTextView.setText(activity.getResources().getString(R.string.roundimage));
        circleDesignTextView.setId(R.id.contentCreateDateId);
        circleDesignTextView.setTextColor(activity.getResources().getColor(R.color.color17));

        innerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pTAAIntent = new Intent(activity, ProjectTaskAssignedListActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectAssignedName", projectNameTextView.getText().toString());
                editor.commit();
                activity.startActivity(pTAAIntent);
            }
        });
        innerContainer.addView(projectNameTextView);
        innerContainer.addView(circleDesignTextView);
        projectAssignedContainers.addView(innerContainer);
    }

    public void normalScreenSizeLayout(int i)
    {

        RelativeLayout innerContainer = new RelativeLayout(activity);
        RelativeLayout.LayoutParams llpChatPageInnerRLT = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 200);
        llpChatPageInnerRLT.setMargins(20, 0, 20, 20);
        innerContainer.setBackground(activity.getResources().getDrawable(R.drawable.projectassignedlistcurvedbackgrnd, null));
        innerContainer.setLayoutParams(llpChatPageInnerRLT);

        RelativeLayout.LayoutParams llpChatProjectNameTxtViewLayout = new RelativeLayout.LayoutParams(340, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMarginStart(15);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(19);
        projectNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        projectNameTextView.setTextColor(Color.parseColor("#1e272e"));
        //  projectNameTextView.setText("Testing");
        try {
            projectNameTextView.setText(projectAssignedList.getString(i));
            projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color23));
            //   projectNameTextView.setTypeface(Typeface.createFromFile("font/"));
            //    projectNameTextView.setTypeface(activity.getResources().getFont(R.font.roboto_regular));

        } catch (JSONException e) {
            e.printStackTrace();
        }



        RelativeLayout.LayoutParams circleDesignTextViewLayout = new RelativeLayout.LayoutParams(140, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView circleDesignTextView = new TextView(activity);
        circleDesignTextViewLayout.setMarginStart(15);
        circleDesignTextViewLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        circleDesignTextView.setLayoutParams(circleDesignTextViewLayout);
        circleDesignTextView.setTextSize(37);
        circleDesignTextView.setTypeface(fontAwesomeIcon);
        circleDesignTextView.setText(activity.getResources().getString(R.string.roundimage));
        circleDesignTextView.setId(R.id.contentCreateDateId);
        circleDesignTextView.setTextColor(activity.getResources().getColor(R.color.color17));

        innerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pTAAIntent = new Intent(activity, ProjectTaskAssignedListActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectName", projectNameTextView.getText().toString());
                editor.commit();
                activity.startActivity(pTAAIntent);
            }
        });
        innerContainer.addView(projectNameTextView);
        innerContainer.addView(circleDesignTextView);
        projectAssignedContainers.addView(innerContainer);
    }


    public void largeScreenSizeLayout(int i)
    {
        RelativeLayout innerContainer = new RelativeLayout(activity);
        RelativeLayout.LayoutParams llpChatPageInnerRLT = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 200);
        llpChatPageInnerRLT.setMargins(20, 0, 20, 20);
        innerContainer.setBackground(activity.getResources().getDrawable(R.drawable.projectassignedlistcurvedbackgrnd, null));
        innerContainer.setLayoutParams(llpChatPageInnerRLT);

        RelativeLayout.LayoutParams llpChatProjectNameTxtViewLayout = new RelativeLayout.LayoutParams(340, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMarginStart(15);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(19);
        projectNameTextView.setTextColor(Color.parseColor("#1e272e"));
        //  projectNameTextView.setText("Testing");
        projectNameTextView.setTypeface(Typeface.DEFAULT_BOLD);

        //  projectNameTextView.setText("Testing");
        try {
            projectNameTextView.setText(projectAssignedList.getString(i));
            projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color23));
            //   projectNameTextView.setTypeface(Typeface.createFromFile("font/"));
            //    projectNameTextView.setTypeface(activity.getResources().getFont(R.font.roboto_regular));

        } catch (JSONException e) {
            e.printStackTrace();
        }



        RelativeLayout.LayoutParams circleDesignTextViewLayout = new RelativeLayout.LayoutParams(140, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView circleDesignTextView = new TextView(activity);
        circleDesignTextViewLayout.setMarginStart(15);
        circleDesignTextViewLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        circleDesignTextView.setLayoutParams(circleDesignTextViewLayout);
        circleDesignTextView.setTextSize(37);
        circleDesignTextView.setTypeface(fontAwesomeIcon);
        circleDesignTextView.setText(activity.getResources().getString(R.string.roundimage));
        circleDesignTextView.setId(R.id.contentCreateDateId);
        circleDesignTextView.setTextColor(activity.getResources().getColor(R.color.color17));

        innerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pTAAIntent = new Intent(activity, ProjectTaskAssignedListActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectAssignedName", projectNameTextView.getText().toString());
                editor.commit();
                activity.startActivity(pTAAIntent);
            }
        });
        innerContainer.addView(projectNameTextView);
        innerContainer.addView(circleDesignTextView);
        projectAssignedContainers.addView(innerContainer);
    }

    public void xLargeScreenSizeLayout(int i)
    {
        RelativeLayout innerContainer = new RelativeLayout(activity);
        RelativeLayout.LayoutParams llpChatPageInnerRLT = new RelativeLayout.LayoutParams(1520, 130);
        llpChatPageInnerRLT.addRule(RelativeLayout.CENTER_HORIZONTAL);
        llpChatPageInnerRLT.addRule(RelativeLayout.CENTER_VERTICAL);
        innerContainer.setBackground(activity.getResources().getDrawable(R.drawable.projectassignedlistcurvedbackgrnd, null));
        innerContainer.setLayoutParams(llpChatPageInnerRLT);

        RelativeLayout.LayoutParams llpChatProjectNameTxtViewLayout = new RelativeLayout.LayoutParams(340, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMarginStart(15);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(19);
        projectNameTextView.setTypeface(Typeface.DEFAULT_BOLD);

        //  projectNameTextView.setText("Testing");
        try {
            projectNameTextView.setText(projectAssignedList.getString(i));
            projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color23));
            //   projectNameTextView.setTypeface(Typeface.createFromFile("font/"));
            //    projectNameTextView.setTypeface(activity.getResources().getFont(R.font.roboto_regular));

        } catch (JSONException e) {
            e.printStackTrace();
        }



        RelativeLayout.LayoutParams circleDesignTextViewLayout = new RelativeLayout.LayoutParams(140, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView circleDesignTextView = new TextView(activity);
        circleDesignTextViewLayout.setMarginStart(15);
        circleDesignTextViewLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        circleDesignTextView.setLayoutParams(circleDesignTextViewLayout);
        circleDesignTextView.setTextSize(37);
        circleDesignTextView.setTypeface(fontAwesomeIcon);
        circleDesignTextView.setText(activity.getResources().getString(R.string.roundimage));
        circleDesignTextView.setId(R.id.contentCreateDateId);
        circleDesignTextView.setTextColor(activity.getResources().getColor(R.color.color17));

        innerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pTAAIntent = new Intent(activity, ProjectTaskAssignedListActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectAssignedName", projectNameTextView.getText().toString());
                editor.commit();
                activity.startActivity(pTAAIntent);
            }
        });
        innerContainer.addView(projectNameTextView);
        innerContainer.addView(circleDesignTextView);
        projectAssignedContainers.addView(innerContainer);
    }


    public void xLargeLandScreenSizeLayout(int i)
    {
        RelativeLayout innerContainer = new RelativeLayout(activity);
        RelativeLayout.LayoutParams llpChatPageInnerRLT = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 200);
        llpChatPageInnerRLT.setMargins(20, 0, 20, 20);
        innerContainer.setBackground(activity.getResources().getDrawable(R.drawable.projectassignedlistcurvedbackgrnd, null));
        innerContainer.setLayoutParams(llpChatPageInnerRLT);

        RelativeLayout.LayoutParams llpChatProjectNameTxtViewLayout = new RelativeLayout.LayoutParams(340, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMarginStart(15);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(19);
        projectNameTextView.setTypeface(Typeface.DEFAULT_BOLD);

        //  projectNameTextView.setText("Testing");
        try {
            projectNameTextView.setText(projectAssignedList.getString(i));
            projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color23));
            //   projectNameTextView.setTypeface(Typeface.createFromFile("font/"));
            //    projectNameTextView.setTypeface(activity.getResources().getFont(R.font.roboto_regular));

        } catch (JSONException e) {
            e.printStackTrace();
        }



        RelativeLayout.LayoutParams circleDesignTextViewLayout = new RelativeLayout.LayoutParams(140, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView circleDesignTextView = new TextView(activity);
        circleDesignTextViewLayout.setMarginStart(15);
        circleDesignTextViewLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        circleDesignTextViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        circleDesignTextView.setLayoutParams(circleDesignTextViewLayout);
        circleDesignTextView.setTextSize(37);
        circleDesignTextView.setTypeface(fontAwesomeIcon);
        circleDesignTextView.setText(activity.getResources().getString(R.string.roundimage));
        circleDesignTextView.setId(R.id.contentCreateDateId);
        circleDesignTextView.setTextColor(activity.getResources().getColor(R.color.color17));

        innerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pTAAIntent = new Intent(activity, ProjectTaskAssignedListActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectAssignedName", projectNameTextView.getText().toString());
                editor.commit();
                activity.startActivity(pTAAIntent);
            }
        });
        innerContainer.addView(projectNameTextView);
        innerContainer.addView(circleDesignTextView);
        projectAssignedContainers.addView(innerContainer);
    }


    public String projectAssignedList(String userName, int selectCriteria)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("userName", userName);
            postDataParams.put("selectCriteria", selectCriteria);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fpluta/",postDataParams, activity,"application/json", "application/json");
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
