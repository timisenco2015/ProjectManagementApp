package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.shapeofview.shapes.RoundRectView;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GenerateSupervisorListClass {

    private Activity activity;
    private LinearLayout supervisorListLinearLT;
    private String userName;
    private String projectName;
    private String selectedSupervisor;
    private JSONArray allSupervisorsJSOnArray;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private HttpRequestClass httpRequestClass;
    public GenerateSupervisorListClass(LinearLayout supervisorListLinearLT)
    {
        this.supervisorListLinearLT=supervisorListLinearLT;
    }

    public GenerateSupervisorListClass(LinearLayout supervisorListLinearLT,Activity activity,String userName, String projectName)
    {
        this.supervisorListLinearLT=supervisorListLinearLT;
        this.activity =activity;
        this.asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        this.userName =userName;
        this.projectName =projectName;
        this.progressBarClass = new ProgressBarClass(activity);
         try {
            String result = getProjectAllSupervisor(userName, projectName);
             Log.e("--d",result);
             Log.e("--ddd",""+result.length());
             allSupervisorsJSOnArray = new JSONArray(result);
             supervisorsListVwNormalScrnSizeLT();
            if(allSupervisorsJSOnArray.length()>0)
            {



                Log.e("--dd",allSupervisorsJSOnArray.toString());
                int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                switch(screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        //friendSuggestnListVwLargeScrnSizeLT();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                        supervisorsListVwNormalScrnSizeLT();
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

public void supervisorsListVwNormalScrnSizeLT() throws JSONException {
    supervisorListLinearLT.removeViews(1, supervisorListLinearLT.getChildCount() - 1);
    for (int i = 0; i <  allSupervisorsJSOnArray.length(); i++) {
//JSONArray tempArray = allSupervisorsJSOnArray.getJSONArray(i);
        String firstName =allSupervisorsJSOnArray.getJSONArray(i).getString(0);
        String lastName =allSupervisorsJSOnArray.getJSONArray(i).getString(1);
        final com.github.florent37.shapeofview.shapes.CircleView circleView = new com.github.florent37.shapeofview.shapes.CircleView(activity);
        RoundRectView.LayoutParams llpInnerOuterVWLayout = new RoundRectView.LayoutParams(250, 250);
        llpInnerOuterVWLayout.setMargins(20, 0, 0, 0);
        circleView.setId(i+1);
        circleView.setLayoutParams(llpInnerOuterVWLayout);


        final LinearLayout outerLinearLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        outerLinearLayout.setBackgroundColor(activity.getResources().getColor(R.color.color7));
        outerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        outerLinearLayout.setLayoutParams(outerLinearLayoutLP);



        LinearLayout.LayoutParams supervisorFirstNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView supervisorFirstNameTextView = new TextView(activity);
        supervisorFirstNameTextView.setLayoutParams(supervisorFirstNameTxtViewLayout);
        supervisorFirstNameTxtViewLayout.gravity =Gravity.CENTER_HORIZONTAL;
        supervisorFirstNameTextView.setTextSize(13);
        supervisorFirstNameTextView.setText(firstName);
     //  supervisorFirstNameTextView.setText(tempArray.getString(i));
        supervisorFirstNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        supervisorFirstNameTextView.setSingleLine(true);
        supervisorFirstNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        supervisorFirstNameTextView.setTextColor(activity.getResources().getColor(R.color.color15));
        supervisorFirstNameTxtViewLayout.setMargins(0, 60, 0, 0);


        LinearLayout.LayoutParams supervisorLastNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView supervisorLastNameTextView = new TextView(activity);
        supervisorLastNameTextView.setLayoutParams(supervisorLastNameTxtViewLayout);
        supervisorLastNameTextView.setTextSize(13);
        supervisorLastNameTxtViewLayout.gravity =Gravity.CENTER_HORIZONTAL;
        supervisorLastNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        supervisorLastNameTextView.setText(lastName);
       // supervisorLastNameTextView.setText(tempArray.getString(i));
        supervisorLastNameTextView.setSingleLine(true);
        supervisorLastNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        supervisorLastNameTextView.setTextColor(activity.getResources().getColor(R.color.color15));
        supervisorLastNameTxtViewLayout.setMargins(0, 5, 0, 0);

        outerLinearLayout.addView(supervisorFirstNameTextView);
        outerLinearLayout.addView(supervisorLastNameTextView);
        circleView.addView(outerLinearLayout);

        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               LinearLayout innerLinearLayout = (LinearLayout) circleView.getChildAt(0);
                innerLinearLayout.setBackgroundColor(activity.getResources().getColor(R.color.color13));
                // selectedSupervisor = tempArray.getString(circleView.getId());

                changeBackGroundColor(v.getId());
            }
        });
        supervisorListLinearLT.addView(circleView);





    }
}

public void changeBackGroundColor(int index)
{
     for(int i=0; i<supervisorListLinearLT.getChildCount();i++)
    {
        if(i!=index)
        {
            com.github.florent37.shapeofview.shapes.CircleView  circleView = (com.github.florent37.shapeofview.shapes.CircleView)supervisorListLinearLT.getChildAt(i);
            LinearLayout innerLinearLayout = (LinearLayout)circleView.getChildAt(0);
            innerLinearLayout.setBackgroundColor(activity.getResources().getColor(R.color.color7));
        }
    }
}

public String getSelectedSupervisor()
{
    return selectedSupervisor;
}



    public String getProjectAllSupervisor(String userName,String projectName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {

            postDataParams.put("userNameOfAppraisee", userName);
            postDataParams.put("nameOfProject",projectName);
            Log.e("--ddddd",postDataParams.toString());
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fapas/",postDataParams, activity,"application/json", "application/json");
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
