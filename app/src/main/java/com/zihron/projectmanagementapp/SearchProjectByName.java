package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

public class SearchProjectByName {

   private Activity activity;
   private String searchKeywords;
    private HttpRequestClass httpRequestClass;
   private String userName;
   private JSONArray searchResultJSONArray;
   private ValidationClass validationClass;
    AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private SearchView searchView;
    private EditText queryEditText;
    private Button searchKeyWordButton;
    private ProgressBarClass progressBarClass;
    public SearchProjectByName(Activity activity,final SearchView searchView, String userName,final Button searchKeyWordButton)
    {
        this.activity = activity;
        progressBarClass = new ProgressBarClass(activity);
        this.searchKeywords = searchKeywords;
        this.userName = userName;
        this.searchView = searchView;
        this.searchKeyWordButton = searchKeyWordButton;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
         validationClass = new ValidationClass();
        searchKeyWordButton.setEnabled(false);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);

        queryEditText = (EditText) searchView.findViewById(id);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
// do something on text submit\





                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String message = validationClass.checkInputSearchField(newText);

             if(!message.equalsIgnoreCase("field is okay"))
                {
                    queryEditText.setError(message);
                    searchKeyWordButton.setEnabled(false);
                }
                else
                {
                    searchKeywords = newText;
                    searchKeyWordButton.setEnabled(true);
                }

                return false;
            }
        });
    }


    public JSONArray getSearchResultJSONArray()
    {

        if(searchKeywords!=null) {
            try {
                String result = searchProjectByDateRange(searchKeywords, userName);
                if (result != null) {
                    searchResultJSONArray = new JSONArray(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else
        {
            queryEditText.setError("please enter search value");
               searchKeyWordButton.setEnabled(false);

        }
        return searchResultJSONArray;
    }




    public String searchProjectByDateRange(String searchKeyWords,String userName ) {
        String result = null;
        JSONObject postDataParams = new JSONObject();
        try {

            postDataParams.put("projectNameSearchValue", searchKeyWords);
            postDataParams.put("nameOfUser", userName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/guapsck/", postDataParams, activity, "application/json", "application/json");
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
