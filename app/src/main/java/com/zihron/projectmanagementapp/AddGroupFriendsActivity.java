package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class AddGroupFriendsActivity extends AppCompatActivity {

    private Activity activity;
    private LinearLayout groupListLinearLYT;
    private S3ImageClass s3ImageClass;
    private boolean[] isSelected;
    private JSONArray allFriendsListJSONArray;
    private JSONArray selecetFriendListJSONArray;
    private LinearLayout selectedFriendHViewLLT;
    private LinearLayout createEntryLinearLayout;
    private ValidationClass validationClass;
    private EditText groupNameEditText;
    private CreateGroup createGroupTask;
    private SharedPreferences sharedPreferences;
    private AlertDialog alertDialog;
    private String userName;
    private String groupName;
    private LayoutInflater inflater;
    private  View popupView;
    public static PopupWindow userGroupsPopUpWindow=null;
    private UserAllFriends userAllFriendsTask;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_friends);
        activity = this;
        this.progressBarClass = new ProgressBarClass(activity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        groupListLinearLYT = (LinearLayout)findViewById(R.id.groupListLLTId);
        selectedFriendHViewLLT = (LinearLayout) findViewById(R.id.selectedFriendHViewLLTId);
        createEntryLinearLayout = (LinearLayout)findViewById(R.id.createEntryLLayoutId);
        groupNameEditText =(EditText)findViewById(R.id.groupNameEditTxtId);
        createEntryLinearLayout.setVisibility(View.GONE);
        validationClass = new ValidationClass();
        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        groupName = sharedPreferences.getString("groupName", null);
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        userAllFriendsTask = new UserAllFriends(userName);
        try {
            String result = userAllFriendsTask.execute().get();
            allFriendsListJSONArray = new JSONArray(result);
            if(allFriendsListJSONArray.length()==0)
            {
                userGroupsPopUpWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
                inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                popupView = inflater.inflate(R.layout.popupwindowlayout, null);
                TextView messageTextView = (TextView) popupView.findViewById(R.id.popTextVWId);
                messageTextView.setText("You don't have any friends yet");
                userGroupsPopUpWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
                userGroupsPopUpWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, 150, 430);
            }
            else
            {
                int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                switch(screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        //friendSuggestnListVwLargeScrnSizeLT();
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu, menu);
        MenuItem search= menu.findItem(R.id.search_menu);
        final SearchView searchView = (SearchView) search.getActionView();
        SearchManager searchManager = (SearchManager)getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

    }



    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();

        if(id==R.id.search_menu)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void newGroupFriendsListVwNormalScrnSizeLT()
    {
      //  isSelected = new boolean[allFriendsListJSONArray.length()];
        isSelected = new boolean[allFriendsListJSONArray.length()];
        selecetFriendListJSONArray = new JSONArray();
        groupListLinearLYT.removeAllViews();
        selectedFriendHViewLLT.removeAllViews();
        for (int i = 0; i < allFriendsListJSONArray.length(); i++) {
      try {
            LinearLayout outerLinearLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
            outerLinearLayoutLP.setMargins(40,0,0,30);
            outerLinearLayout.setClickable(true);
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
            s3ImageClass = new S3ImageClass(activity, allFriendsListJSONArray.getJSONArray(i).getString(0),"profilepicfolder");
            if( s3ImageClass.isObjectExists())
            {
                profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                profileImageVW.invalidate();
            }
            else
            {
                Picasso.with(activity).load("https://ui-avatars.com/api/?name="+allFriendsListJSONArray.getJSONArray(i).getString(1)+" "+allFriendsListJSONArray.getJSONArray(i).getString(2)+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
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

                friendNameTextView.setText(allFriendsListJSONArray.getJSONArray(i).getString(1)+" "+allFriendsListJSONArray.getJSONArray(i).getString(2));

            friendNameTextView.setSingleLine(true);
            friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
            friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
            friendNameTxtViewLayout.setMargins(0, 0, 0, 0);


            LinearLayout.LayoutParams friendUserNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            final TextView friendUsernameTextView = new TextView(activity);
            friendUsernameTextView.setLayoutParams(friendNameTxtViewLayout);
            friendUsernameTextView.setTextSize(15);
            friendUsernameTextView.setText(allFriendsListJSONArray.getJSONArray(i).getString(0));
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
                 int index =  findPositioninJSONArray(friendUsernameTextView.getText().toString());
                    int index2 = findPositioninFriendListJSONArray(friendUsernameTextView.getText().toString());

                    LinearLayout tempLinearLayout = (LinearLayout) groupListLinearLYT.getChildAt(index);
                    createEntryLinearLayout.setVisibility(View.VISIBLE);
                  if (isSelected[index])
                  {
                     selecetFriendListJSONArray.remove(index2);
                      tempLinearLayout.setBackgroundColor(0);
                      isSelected[index]=false;
                     selectedFriendHViewLLT.removeViewAt(index2);
                      if(selecetFriendListJSONArray.length()==0)
                      {
                          createEntryLinearLayout.setVisibility(View.GONE);
                      }

                  }
                  else
                  {
                      try {
                          selecetFriendListJSONArray.put(allFriendsListJSONArray.getJSONArray(index).getString(0));
                          tempLinearLayout.setBackgroundColor(getResources().getColor(R.color.color32));
                          createSelectedFriendHorizontalView(allFriendsListJSONArray.getJSONArray(index));
                          isSelected[index]=true;
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }




                  }
                }
            });


            groupListLinearLYT.addView(outerLinearLayout);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

public void createSelectedFriendHorizontalView(JSONArray tempSelectedArray)
{
    CircleImageView profileImageVW = new CircleImageView(activity);
    RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
    rlpInnerProfileImageVWLayout.setMargins(35,0,0,0);
    profileImageVW.setLayoutParams(rlpInnerProfileImageVWLayout);
    profileImageVW.setId(R.id.memberLogoId);
    s3ImageClass = new S3ImageClass(activity, "testing","profilepicfolder");
    if( s3ImageClass.isObjectExists())
    {
        profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
        profileImageVW.invalidate();
    }
    else
    {
        Picasso.with(activity).load("https://ui-avatars.com/api/?name="+"hhh"+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
    }


    LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    final TextView friendNameTextView = new TextView(activity);
    friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
    friendNameTextView.setTextSize(15);
    friendNameTextView.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
    try {
        friendNameTextView.setText(tempSelectedArray.getString(0));

    friendNameTextView.setSingleLine(true);
    friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
    friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
    friendNameTxtViewLayout.setMargins(0, 10, 0, 0);


    LinearLayout.LayoutParams hiddenTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    final TextView hiddenTextView = new TextView(activity);
    hiddenTextView.setLayoutParams(hiddenTxtViewLayout);
    hiddenTextView.setTextSize(15);
    hiddenTextView.setText(tempSelectedArray.getString(0));
    hiddenTextView.setVisibility(View.GONE);



    CircleImageView closeImageVW = new CircleImageView(activity);
    RelativeLayout.LayoutParams rlpcloseImageVWLayout = new RelativeLayout.LayoutParams(60, 60);
    rlpcloseImageVWLayout.addRule(RelativeLayout.RIGHT_OF,R.id.memberLogoId);
    rlpcloseImageVWLayout.addRule(RelativeLayout.ALIGN_BOTTOM);
    closeImageVW.setLayoutParams(rlpcloseImageVWLayout);
    closeImageVW.setBackground(getResources().getDrawable(R.drawable.closeimage));
    closeImageVW.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), hiddenTextView.getText().toString(),Toast.LENGTH_LONG).show();
       int index1 = findPositioninJSONArray(hiddenTextView.getText().toString());
            int index2 =  findPositioninFriendListJSONArray(hiddenTextView.getText().toString());


            selecetFriendListJSONArray.remove(index2);
            if(selecetFriendListJSONArray.length()==0)
            {
                createEntryLinearLayout.setVisibility(View.GONE);
            }
            selectedFriendHViewLLT.removeViewAt(index2);
            LinearLayout tempLinearLayout = (LinearLayout) groupListLinearLYT.getChildAt(index1);
            tempLinearLayout.setBackgroundColor(0);
            isSelected[index1]=false;

        }
    });


    LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
    LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    innerSelectionLinearLayout.setOrientation(LinearLayout.VERTICAL);
    innerSelectionLinearLayoutLP.setMargins(20,0,0,0);
    innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);


    RelativeLayout innerSelectionRelativeLayout = new RelativeLayout(activity);
    RelativeLayout.LayoutParams innerSelectionRelativeLayoutLP = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    innerSelectionRelativeLayout.setLayoutParams(innerSelectionRelativeLayoutLP);

    innerSelectionRelativeLayout.addView(profileImageVW);
    innerSelectionRelativeLayout.addView(closeImageVW);

    innerSelectionLinearLayout.addView(innerSelectionRelativeLayout );
    innerSelectionLinearLayout.addView(friendNameTextView);
    selectedFriendHViewLLT.addView(innerSelectionLinearLayout);
    } catch (JSONException e) {
        e.printStackTrace();
    }
}




    private int findPositioninJSONArray(String recieveName)
    {
        int index=-1;
        boolean check = false;
        for(int i=0; i<allFriendsListJSONArray.length() && !check; i++)
        {

            try {

                JSONArray tempArray =allFriendsListJSONArray.getJSONArray(i);
                if(  (tempArray.getString(0).equalsIgnoreCase(recieveName))) {
                    index = i;
                    check = true;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return index;
    }


    private int findPositioninFriendListJSONArray(String recieveName)
    {
        int index=-1;
        boolean check = false;

        for(int i=0; i<selecetFriendListJSONArray.length() && !check; i++)
        {

            try {


                if(  (selecetFriendListJSONArray.getString(i).equalsIgnoreCase(recieveName))) {
                    index = i;
                    check = true;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return index;
    }

    public void createGroup(View view)
    {
        validationClass = new ValidationClass();
        Toast.makeText(getApplicationContext(),"groupNameError",Toast.LENGTH_LONG).show();
        String groupNameError = validationClass.checkFieldValue(groupNameEditText.getText().toString().trim());

        if (!groupNameError.equalsIgnoreCase("field okay"))
        {
            groupNameEditText.setError(groupNameError);
        }
        else {
            createGroupTask = new CreateGroup( userName,groupName,selecetFriendListJSONArray.toString());
            try {
                String result = createGroupTask.execute().get();
                if(result.equalsIgnoreCase("Record Not Inserted"))
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Group not created due to network");
                            alertDialogBuilder.setPositiveButton("Okay",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            alertDialog.dismiss();
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    });

                     alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                else
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Group created");
                    alertDialogBuilder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    alertDialog.dismiss();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });

                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    public class CreateGroup extends AsyncTask<String,String,String>
    {

        private String userName;
        private String groupName;
        private String allGroupMemebers;
        private String groupCreatedDate;

        private URL url;
        private String result;

        public CreateGroup ( String userName,String groupName,String allGroupMemebers )
        {

            this.groupName=groupName;
            this.userName = userName;
            this.allGroupMemebers = allGroupMemebers;
            this.groupCreatedDate  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        }



        @Override
        protected void onPreExecute() {

            progressBarClass.showDialog();
        }

        @Override
        protected String doInBackground(String... arg0) {

            JSONObject postDataParams= new JSONObject();

            try {

                postDataParams.put("groupName", groupName);
                postDataParams.put("userName", userName);
                postDataParams.put("membersOfTheGroup", allGroupMemebers);
                postDataParams.put("dateGroupCreated",groupCreatedDate);

                url = new URL("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/insertNewUserGroup/");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "text/plain");
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(postDataParams.toString());

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    result = sb.toString();

                } else {
                   // asyncErrorDialogDisplay.errorcodecheck(responseCode);
                }
            } catch (Exception e) {
                asyncErrorDialogDisplay.handleException(activity);
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {


            progressBarClass.dismissDialog();

        }


    }



    public class UserAllFriends extends AsyncTask<String,String,String>
    {

        private String userName;
private String result;


        public UserAllFriends (String userName)
        {


            this.userName = userName;



        }

        @Override
        protected void onPreExecute() {
             progressBarClass.showDialog();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fecthAllUserFriendsUsernames/"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("nameOfUser", userName);

                Log.e("ttt",postDataParams.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(postDataParams.toString());

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    result = sb.toString();

                } else {
                  //  asyncErrorDialogDisplay.errorcodecheck(responseCode);
                }
            } catch (Exception e) {
                asyncErrorDialogDisplay.handleException(activity);
            }
return result;
        }

        @Override
        protected void onPostExecute(String result) {


            progressBarClass.dismissDialog();

        }


    }



}
