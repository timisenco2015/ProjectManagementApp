package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ProjectActivity extends AppCompatActivity {
    private ListView projectListView;
    private CustomProjectListViewAdapter customProjectListViewAdapter;
    private SharedPreferences sharedPreferences;
    private String userName;
    private int projectListCount;
    private ImageView supervisorReviewImageView;
    private SearchProjectByName searchProjectByName;
    ;
    private JSONArray allProjectsArray = null;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private RelativeLayout advanceSearchLLTIdLinearLT;
    Typeface fontAwesomeIcon;
    TextView otherOwnerProjectTextView;
    TextView onwnedProjectTextView;
    SwipeRefreshLayout projectActivityRefresh;
    private Dialog sortDialog;
    private static PopupWindow popUp;
    TextView sortByDropDownTxtIdTextView;
    private LinearLayout projectNameLayoutIdLLT;
    private LinearLayout projectCreateDateLayoutIdLLT;
    private LinearLayout projectStartDateLayoutIdLLT;
    private LinearLayout projectEndDateLayoutIdLLT;
    private RadioButton projectNameBtn;
    private RadioButton projectCreateDateBtn;
    private RadioButton projectStartDateBtn;
    private RadioButton projectEndDateBtn;
    private RadioGroup projectCountRadioGroup;
    private boolean projectNameRadioBtnChecked = false;
    private boolean projectCreateDateRadioBtnChecked = false;
    private boolean projectStartDateRadioBtnChecked = false;
    private boolean projectEndDateRadioBtnChecked = false;
    PopupMenu popupMenu;
    int start_year, start_month, start_day;
    TextView chartGraphTextView;
    private String projectName;
    private Activity activity;
    private boolean user_Others_CreatedProjectsSet;
    private SearchProjectByDatePeriod searchProjectByDatePeriod;
    private SearchProjectByDateRange searchProjectByDateRange;
    private ProgressBarClass progressBarClass;
    private HttpRequestClass httpRequestClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        projectListView = (ListView) findViewById(R.id.projectListView);
        projectActivityRefresh = (SwipeRefreshLayout) findViewById(R.id.projectActivityRefreshId);
        supervisorReviewImageView = (ImageView) findViewById(R.id.supervisorReviewImgVWId);
        projectActivityRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (popUp != null && popUp.isShowing()) {
                    popUp.dismiss();
                }

                loadListView();
                projectListView.invalidate();
                projectActivityRefresh.setRefreshing(false);
            }
        });

        projectListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (projectListView == null || projectListView.getChildCount() == 0)
                                ? 0
                                : projectListView.getChildAt(0).getTop();
                projectActivityRefresh.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        fontAwesomeIcon = Typeface.createFromAsset(this.getAssets(), "font/fontawesome-webfont.ttf");
        otherOwnerProjectTextView = (TextView) findViewById(R.id.otherOwnerProjectView);
        final Calendar calendar = Calendar.getInstance();
        user_Others_CreatedProjectsSet = false;
        start_year = calendar.get(Calendar.YEAR);
        start_month = calendar.get(Calendar.MONTH);
        start_day = calendar.get(Calendar.DAY_OF_MONTH);
        activity = this;
        progressBarClass = new ProgressBarClass(activity);
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        advanceSearchLLTIdLinearLT = (RelativeLayout) findViewById(R.id.advanceSearchLLTId);
        onwnedProjectTextView = (TextView) findViewById(R.id.onwnedProjectView);
        sortByDropDownTxtIdTextView = (TextView) findViewById(R.id.sortByDropDownTxtId);
        projectCountRadioGroup = (RadioGroup) findViewById(R.id.projectCountRadioGroupId);

        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        chartGraphTextView = (TextView) findViewById(R.id.chartGraphView);
        otherOwnerProjectTextView.setTypeface(fontAwesomeIcon);
        onwnedProjectTextView.setTypeface(fontAwesomeIcon);
        sortByDropDownTxtIdTextView.setTypeface(fontAwesomeIcon);
        chartGraphTextView.setTypeface(fontAwesomeIcon);
        sharedPreferences = this.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        user_Others_CreatedProjectsSet = false;

        loadListView();

        final String[] optionsArray = new String[]{"Delete Project", "View Project"};
        projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                final TextView projectNameView = (TextView) view.findViewById(R.id.projectNameViewId);
                final TextView projectOwnerView = (TextView) view.findViewById(R.id.projectownerId);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                builder.setTitle("What do you want to do")
                        .setItems(optionsArray, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                if (index == 0) {
                                    if (user_Others_CreatedProjectsSet) {


                                            String result = deleteProjectRecord();
                                            if (result.equalsIgnoreCase("deleted")) {

                                            } else if (result.equalsIgnoreCase("this project cannot be deleted") || result.equalsIgnoreCase("project record does not exist")) {
                                                Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(activity, "There is a network issu. Please try again", Toast.LENGTH_LONG).show();
                                            }

                                        activity.finish();
                                        startActivity(activity.getIntent());
                                    } else {
                                        Toast.makeText(getApplication(), "You cann't delete project not created by you", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Intent PTAIntent = new Intent(getApplication(), ViewProjectActivity.class);


                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("projectName", projectNameView.getText().toString());
                                    editor.commit();

                                    PTAIntent.putExtra("projectName", projectName);

                                    ProjectActivity.this.startActivity(PTAIntent);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builder.create();
                builder.show();

//
                //

            }
        });

        otherOwnerProjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_Others_CreatedProjectsSet = false;
                getAllUserProjectsListList(userName, user_Others_CreatedProjectsSet, projectListCount);

            }
        });


        onwnedProjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_Others_CreatedProjectsSet = true;
                getAllUserProjectsListList(userName, user_Others_CreatedProjectsSet, projectListCount);
            }
        });


        //
        advanceSearchLLTIdLinearLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PTAIntent = new Intent(activity, ProjectSearchActivity.class);
                startActivityForResult(PTAIntent, 1);


            }
        });


        supervisorReviewImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(activity, supervisorReviewImageView);
                popupMenu.getMenuInflater().inflate(R.menu.supervisorallappraiseereview, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent paIntent = new Intent(ProjectActivity.this, SupervisorAllAppraiseeReports.class);
                        startActivity(paIntent);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        projectCountRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton radioBtn = (RadioButton) findViewById(checkedId);
                projectListCount = Integer.parseInt(radioBtn.getText().toString());
                if (allProjectsArray != null || allProjectsArray.length() != 0) {
                    int number = allProjectsArray.length();
                    if (projectListCount <= number) {
                        reduceJsonArrayElement(allProjectsArray, projectListCount);
                    } else {
                        getAllUserProjectsListList(userName, user_Others_CreatedProjectsSet, projectListCount);

                    }

                }
                radioBtn.setTextColor(ContextCompat.getColor(activity, R.color.color13));
                changeSelectedRadiButtnInRadioGrpBckGround(checkedId, projectCountRadioGroup);
            }
        });

        sortByDropDownTxtIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialog = sortProjectDialog();
                Window dialogWindow = sortDialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
                int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                switch (screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        lp.x = 100; // The new position of the X coordinates
                        lp.y = 150;
                        dialogWindow.setAttributes(lp);
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                        lp.x = 100; // The new position of the X coordinates
                        lp.y = 300;
                        dialogWindow.setAttributes(lp);
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


                sortDialog.show();
            }
        });


    }
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");

                try {

                    allProjectsArray = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (popUp != null && popUp.isShowing()) {
                    popUp.dismiss();
                }
                if (allProjectsArray.length() == 0) {

                    popUpView("No search result");


                }
                customProjectListViewAdapter = new CustomProjectListViewAdapter(activity, allProjectsArray);
                projectListView.setAdapter(customProjectListViewAdapter);
                projectListView.invalidateViews();

            }
        }
    }//o

    public void loadListView() {
        projectListCount = 10;
        ((RadioButton) projectCountRadioGroup.getChildAt(0)).setTextColor(ContextCompat.getColor(activity, R.color.color13));
        changeSelectedRadiButtnInRadioGrpBckGround(((RadioButton) projectCountRadioGroup.getChildAt(0)).getId(), projectCountRadioGroup);
        user_Others_CreatedProjectsSet = true;

        try {
            String result = getAllProjectDetails(userName, user_Others_CreatedProjectsSet, projectListCount);

            allProjectsArray = new JSONArray(result);
            if (result == null) {

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        popUpView("No project to display");
                    }
                }, 100);


            } else {


                customProjectListViewAdapter = new CustomProjectListViewAdapter(this, allProjectsArray);
                projectListView.setAdapter(customProjectListViewAdapter);
            }
        } catch (JSONException e) {
            asyncErrorDialogDisplay.handleException(activity);

        }
    }

    public void popUpView(String textMessage) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        TextView tv;

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popupwindowlayout, null);
        LinearLayout popUpLsyout = (LinearLayout) customView.findViewById(R.id.popLayoutId);

        tv = (TextView) customView.findViewById(R.id.popTextVWId);
        tv.setText(textMessage);
        popUp = new PopupWindow(
                customView,
                770,
                120
        );

        popUp.setElevation(5.0f);
        popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, 170, 830);


    }


    public void changeSelectedRadiButtnInRadioGrpBckGround(int checkId, RadioGroup radioGroup) {

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton view = (RadioButton) radioGroup.getChildAt(i);
            if (view.getId() != checkId) {
                view.setTextColor(activity.getResources().getColor(R.color.color12));

            }
        }
    }


    public void getAllUserProjectsListList(String userName, boolean projectOriginalOwner, int projectListCount) {


        try {
            String result = getAllProjectDetails(userName, projectOriginalOwner, projectListCount);
            allProjectsArray = new JSONArray(result);
            if (popUp != null && popUp.isShowing()) {
                popUp.dismiss();
            }

            if (allProjectsArray.length() == 0) {
                popUpView("No project to display");
            }

            customProjectListViewAdapter = new CustomProjectListViewAdapter(activity, allProjectsArray);
            projectListView.setAdapter(customProjectListViewAdapter);
            projectListView.invalidateViews();
        } catch (JSONException e) {
            asyncErrorDialogDisplay.handleException(activity);
        }
    }


    public void reduceJsonArrayElement(JSONArray holdersArray, int limit) {
        JSONArray returnArray = null;
        returnArray = new JSONArray();
        for (int i = 0; i < limit; i++) {
            try {
                returnArray.put(i, holdersArray.getJSONArray(i));
            } catch (JSONException e) {
                asyncErrorDialogDisplay.handleException(activity);
            }
        }
        customProjectListViewAdapter = new CustomProjectListViewAdapter(activity, returnArray);
        projectListView.setAdapter(customProjectListViewAdapter);
        projectListView.invalidateViews();
    }


    public Dialog sortProjectDialog() {
        final Dialog sortProjectDialog = new Dialog(activity);
        sortProjectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sortProjectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        sortProjectDialog.setContentView(R.layout.sortprojectlayout);
        projectNameLayoutIdLLT = (LinearLayout) sortProjectDialog.findViewById(R.id.projectNameLayoutId);
        projectCreateDateLayoutIdLLT = (LinearLayout) sortProjectDialog.findViewById(R.id.projectCreateDateLayoutId);
        projectStartDateLayoutIdLLT = (LinearLayout) sortProjectDialog.findViewById(R.id.projectStartDateLayoutId);
        projectNameBtn = (RadioButton) sortProjectDialog.findViewById(R.id.projectName);

        projectCreateDateBtn = (RadioButton) sortProjectDialog.findViewById(R.id.projectCreateDate);
        projectStartDateBtn = (RadioButton) sortProjectDialog.findViewById(R.id.projectStartDate);
        projectEndDateBtn = (RadioButton) sortProjectDialog.findViewById(R.id.projectEndDate);
        projectEndDateLayoutIdLLT = (LinearLayout) sortProjectDialog.findViewById(R.id.projectEndDateLayoutId);


        projectNameBtn.setChecked(projectNameRadioBtnChecked);
        projectCreateDateBtn.setChecked(projectCreateDateRadioBtnChecked);
        projectStartDateBtn.setChecked(projectStartDateRadioBtnChecked);
        projectEndDateBtn.setChecked(projectEndDateRadioBtnChecked);


        projectNameLayoutIdLLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectNameBtn.setChecked(true);
                projectCreateDateBtn.setChecked(false);
                projectStartDateBtn.setChecked(false);
                projectEndDateBtn.setChecked(false);
                projectNameRadioBtnChecked = true;
                projectCreateDateRadioBtnChecked = false;
                projectStartDateRadioBtnChecked = false;
                projectEndDateRadioBtnChecked = false;
                sortDialog.hide();
                sortProjectist("projectName");

            }
        });

        projectCreateDateLayoutIdLLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectNameRadioBtnChecked = false;
                projectCreateDateRadioBtnChecked = true;
                projectStartDateRadioBtnChecked = false;
                projectEndDateRadioBtnChecked = false;
                projectNameBtn.setChecked(false);
                projectCreateDateBtn.setChecked(true);
                projectStartDateBtn.setChecked(false);
                projectEndDateBtn.setChecked(false);
                sortDialog.hide();
                sortProjectist("projectCreateDate");
            }
        });


        projectStartDateLayoutIdLLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectNameBtn.setChecked(false);
                projectCreateDateBtn.setChecked(false);
                projectStartDateBtn.setChecked(true);
                projectEndDateBtn.setChecked(false);
                projectNameRadioBtnChecked = false;
                projectCreateDateRadioBtnChecked = false;
                projectStartDateRadioBtnChecked = true;
                projectEndDateRadioBtnChecked = false;
                sortDialog.hide();
                sortProjectist("projectStartDate");
            }
        });

        projectEndDateLayoutIdLLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectNameBtn.setChecked(false);
                projectCreateDateBtn.setChecked(false);
                projectStartDateBtn.setChecked(false);
                projectEndDateBtn.setChecked(true);
                projectNameRadioBtnChecked = false;
                projectCreateDateRadioBtnChecked = false;
                projectStartDateRadioBtnChecked = false;
                projectEndDateRadioBtnChecked = true;
                sortDialog.hide();
                sortProjectist(" projectEndDate");
            }
        });

        return sortProjectDialog;
    }


    public void sortProjectist(String sortBy) {
        int number = allProjectsArray.length();

        if (sortBy.compareTo("projectName") == 0)
            quicksortAllProjectsArray(0, number - 1, 0);
        if (sortBy.compareTo("projectStartDate") == 0)
            quicksortAllProjectsArray(0, number - 1, 2);
        if (sortBy.compareTo("projectCreateDate") == 0)
            quicksortAllProjectsArray(0, number - 1, 1);
        if (sortBy.compareTo("projectEndDate") == 0)
            quicksortAllProjectsArray(0, number - 1, 3);
        customProjectListViewAdapter = new CustomProjectListViewAdapter(activity, allProjectsArray);
        projectListView.setAdapter(customProjectListViewAdapter);
        projectListView.invalidateViews();
    }


    private void quicksortAllProjectsArray(int low, int high, int sortByIndex) {
        int i = low, j = high;
        JSONArray pivot = null;
        try {
            pivot = allProjectsArray.getJSONArray(low + (high - low) / 2);
            while (i <= j) {
                while (allProjectsArray.getJSONArray(i).getString(sortByIndex).compareTo(pivot.getString(sortByIndex)) < 0) {
                    i++;

                }

                while (allProjectsArray.getJSONArray(j).getString(sortByIndex).compareTo(pivot.getString(sortByIndex)) > 0) {
                    j--;
                }

                if (i <= j) {
                    exchangeAllProjectsList(i, j);
                    i++;
                    j--;
                }


            }
        } catch (JSONException e) {
            asyncErrorDialogDisplay.handleException(activity);
        }
        // Recursion
        if (low < j)
            quicksortAllProjectsArray(low, j, sortByIndex);
        if (i < high)
            quicksortAllProjectsArray(i, high, sortByIndex);
    }

    private void exchangeAllProjectsList(int i, int j) {

        JSONArray temp = null;
        try {
            temp = allProjectsArray.getJSONArray(i);
            allProjectsArray.put(i, allProjectsArray.getJSONArray(j));
            allProjectsArray.put(j, temp);
        } catch (JSONException e) {
            asyncErrorDialogDisplay.handleException(activity);
        }

    }

    public void backToViewProjectAssingedActivity(View view) {
        Intent pAIntent = new Intent(activity, HomeActivity.class);
        ProjectActivity.this.startActivity(pAIntent);
    }


    public String getAllProjectDetails(String userName, boolean projectOriginalOwner, int projectListCount) {
        String result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("userEmail", userName);
            postDataParams.put("projectOriginalOwner", projectOriginalOwner);
            postDataParams.put("projectListCount", projectListCount);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/guap/", postDataParams, activity, "application/json", "application/json");
            result = httpRequestClass.getResult();
            asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
            if (result != null) {
                if (result.equalsIgnoreCase("exception")) {
                    asyncErrorDialogDisplay.handleException(activity);
                } else if (result.equalsIgnoreCase("No network")) {
                    Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
                } else if (android.text.TextUtils.isDigitsOnly(result)) {
                    asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
                }
            } else if (result == null) {
                Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    public String deleteProjectRecord() {
        String result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("projectName", projectName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/dpg/", postDataParams, activity, "text/plain", "application/json");
            result = httpRequestClass.getResult();
            asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
            if (result != null) {
                if (result.equalsIgnoreCase("exception")) {
                    asyncErrorDialogDisplay.handleException(activity);
                } else if (result.equalsIgnoreCase("No network")) {
                    Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
                } else if (android.text.TextUtils.isDigitsOnly(result)) {
                    asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
                }
            } else if (result == null) {
                Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


}






































