package ZihronChatApp.ZihronWorkChat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zihron.projectmanagementapp.AsyncErrorDialogDisplay;
import com.zihron.projectmanagementapp.LoginActivity;
import com.zihron.projectmanagementapp.R;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WorkChatPageActivity extends AppCompatActivity {

   RelativeLayout chatContinerIdLinearLayout;
    LinearLayout selectionContainerLinearLayout;
    ImageView opencloseSelectionContainer;
    ImageView opencloseChatContainer;
    LinearLayout projectSelectionLinearLayout;
    Spinner groupLevelSpinner;
    ListView projectSelectionListView;
    LinearLayout taskSelectionLinearLayout;
    ListView taskSelectionListView;
    AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    HttpRequestClass httpRequestClass;
    boolean isChatContainerOpened = false;
    LinearLayout  groupSelectionLinearLayout;
    ListView groupsSelectionListView;
    ImageView backToProjectTaskTextView;
    ImageView backToProjectTextView;
    AlphaAnimation hideAnimation;
    AlphaAnimation showAnimation;
    Typeface fontAwesomeIcon;
  SharedPreferences sharedPreferences;
  Gson googleJson;
    Activity activity;
    String userName;
    ArrayList<String > projectList;
    ArrayList<String > groupList;
    ArrayList<String > groupLevelList;
    ArrayList<String > taskList;
    String selectedProjectName;
    String selectedProjectTaskName;
    String selectedGroup;
    String selectedGroupLevel;
    RelativeLayout groupChatMemebrsRelativeLayout;
    RecyclerView recyclerMemberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_chat_page);
        chatContinerIdLinearLayout = (RelativeLayout) findViewById(R.id.chatPageLayoutId);
        selectionContainerLinearLayout = (LinearLayout)findViewById(R.id.selectionContainerLLTId) ;
        opencloseSelectionContainer = (ImageView)findViewById(R.id.opencloseSelContainerId);
        opencloseChatContainer = (ImageView) findViewById(R.id.opencloseChatContainerId);
        projectSelectionLinearLayout = (LinearLayout)findViewById(R.id.projectSelectionLLTId);
       groupLevelSpinner = (Spinner)findViewById(R.id.groupLevelSpinnerId);
       projectSelectionListView = (ListView)findViewById(R.id.projectSelectionListViewId);
        taskSelectionLinearLayout = (LinearLayout)findViewById(R.id.taskSelectionLLTId);
        taskSelectionListView =(ListView)findViewById(R.id.taskSelectionListViewId);
        groupSelectionLinearLayout = (LinearLayout)findViewById(R.id.groupSelectionLLTId);
        groupsSelectionListView = (ListView)findViewById(R.id.groupsSelectionListViewId);
        ownerChatsLayoutDisplay(Gravity.LEFT);
        activity=this;
        backToProjectTaskTextView = (ImageView)findViewById(R.id.backToProjectTaskTxtVWId);
        backToProjectTextView =(ImageView)findViewById(R.id.backToProjectTxtVWId);
        groupChatMemebrsRelativeLayout = (RelativeLayout)findViewById(R.id.groupChatMemebrsId);
        recyclerMemberList = (RecyclerView) findViewById(R.id.recycler_member_list);
        taskSelectionLinearLayout.setVisibility(View.GONE);
        groupSelectionLinearLayout.setVisibility(View.GONE);
        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        fontAwesomeIcon = Typeface.createFromAsset(this.getAssets(),"font/fontawesome-webfont.ttf");

        populateGroupLevelSpinner();
        opencloseSelectionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isChatContainerOpened)
                {
                    chatContinerIdLinearLayout.animate().translationX(0);
                }

            }
        });



        opencloseChatContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isChatContainerOpened)
                {
                    chatContinerIdLinearLayout.animate().translationX(700);
                }
                else
                {

                }

            }
        });



        backToProjectTaskTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                projectSelectionLinearLayout.setVisibility(View.GONE);


                groupSelectionLinearLayout.setVisibility(View.GONE);


                projectSelectionLinearLayout.setVisibility(View.VISIBLE);


            }
        });


        backToProjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               groupSelectionLinearLayout.setVisibility(View.GONE);

                taskSelectionLinearLayout.setVisibility(View.GONE);


                projectSelectionLinearLayout.setVisibility(View.VISIBLE);

            }
        });



        projectSelectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               selectedProjectName = projectList.get(position).toString();
                if(selectedGroupLevel.equalsIgnoreCase("Members"))
                {

                    populateTaskListView();

                }
                else if (selectedGroupLevel.equalsIgnoreCase("supervisors"))
                {
                    populategroupListView(userName, selectedProjectName);
                }
            }
        });

        taskSelectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedProjectName = projectList.get(position).toString();
                selectedProjectTaskName = taskList.get(position).toString();
                populategroupListView(userName, selectedProjectName,selectedProjectTaskName);
            }
        });

        groupsSelectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = projectList.get(position).toString();
                selectedProjectTaskName = taskList.get(position).toString();
                populategroupListView(userName, selectedProjectName,selectedProjectTaskName);
            }
        });


        //public List<Member> getChannelMemberList(String urlChannel)
        groupLevelSpinner.setSelection(0,false);
        groupLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedGroupLevel =groupLevelList.get(position).toString();
                populateProjectSpinner(userName, selectedGroupLevel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }


    public void ownerChatsLayoutDisplay(int position)
    {
       // Typeface face = Typeface.createFromAsset(getAssets(),
       //         "font/lato.xml");

        LinearLayout outerOwnerChatLinearLayout = new LinearLayout(this);
        outerOwnerChatLinearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        outerOwnerChatLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llpChatPageLayout = new LinearLayout.LayoutParams(780,260 );
        llpChatPageLayout.gravity= position;
        outerOwnerChatLinearLayout.setLayoutParams(llpChatPageLayout);

        View chatView = new View(this);
        LinearLayout.LayoutParams llpViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2 );
        llpViewLayout.setMargins(0,15,0,0);
        chatView.setBackgroundColor(getResources().getColor(R.color.color24));
        chatView.setLayoutParams(llpViewLayout);

        LinearLayout.LayoutParams llpChatPageContentTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT );
        TextView textContent = new TextView(this);
        llpChatPageContentTxtViewLayout.setMarginStart(5);
        textContent.setLayoutParams(llpChatPageContentTxtViewLayout);
        textContent.setTextSize(19);
        textContent.setText("sggsfgsfgfsdgfsdgfgfgfgfgfgfgggggf");
        textContent.setTextColor(getResources().getColor(R.color.color23));
       // textContent.setTypeface(face, Typeface.NORMAL);
        textContent.setId(R.id.contentTVId);

        RelativeLayout innerOwnerChatRelativeLayout= new RelativeLayout(this);
        RelativeLayout.LayoutParams llpChatPageInnerRLT = new RelativeLayout.LayoutParams(780,260 );
        llpChatPageInnerRLT.setMargins(0,10,0,0);
        innerOwnerChatRelativeLayout.setLayoutParams(llpChatPageInnerRLT);

        RelativeLayout.LayoutParams llpChatPageContentDateTxtViewLayout = new RelativeLayout.LayoutParams(140,RelativeLayout.LayoutParams.WRAP_CONTENT );
        TextView memberSatusTextView = new TextView(this);
        llpChatPageContentDateTxtViewLayout.setMarginStart(15);
        llpChatPageContentDateTxtViewLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        memberSatusTextView.setLayoutParams(llpChatPageContentDateTxtViewLayout);
        memberSatusTextView.setTextSize(17);
        memberSatusTextView.setText("active");
        memberSatusTextView.setId(R.id.contentCreateDateId);
        memberSatusTextView.setTextColor(getResources().getColor(R.color.color23));
       // memberSatusTextView.setTypeface(face, Typeface.NORMAL);


        ImageView profileImageVW = new ImageView(this);
        profileImageVW.setImageDrawable(getDrawable(R.drawable.profilelogo));
        RelativeLayout.LayoutParams llpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(80, 80);
        llpInnerProfileImageVWLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
        profileImageVW.setId(R.id.chatLogoId);

        innerOwnerChatRelativeLayout.addView(memberSatusTextView);
        innerOwnerChatRelativeLayout.addView(profileImageVW);

        outerOwnerChatLinearLayout.addView(textContent);
        outerOwnerChatLinearLayout.addView(chatView);

        outerOwnerChatLinearLayout.addView(innerOwnerChatRelativeLayout);
//        chatContinerIdLLT.addView(outerOwnerChatLinearLayout);
    }



    public void backToChatActivity (View view)
    {

    }

public void populateProjectSpinner(String userName, String groupLevel)
{
    String result = getProjectList(userName,groupLevel);
   if(result!=null) {
       googleJson = new Gson();
       try {
           JSONArray tempArray = new JSONArray(result);
           projectList = googleJson.fromJson(tempArray.toString(), ArrayList.class);
       } catch (JSONException e) {
           e.printStackTrace();
       }

       ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, projectList);
       projectSelectionListView.setAdapter(itemsAdapter);
   }
   }
   public void populateGroupLevelSpinner()
   {
       groupLevelList = new ArrayList<String>();
       groupLevelList.add("Supervisors");
       groupLevelList.add("Members");

       ArrayAdapter<String> itemsAdapter =  new ArrayAdapter<String>(activity, R.layout.custom_textview_to_spinner,  groupLevelList);
       itemsAdapter.setDropDownViewResource(R.layout.custom_textview_to_spinner);
       groupLevelSpinner.setAdapter(itemsAdapter);

   }

   public void populateTaskListView()
   {

       groupSelectionLinearLayout.setVisibility(View.GONE);


       projectSelectionLinearLayout.setVisibility(View.GONE);


       taskSelectionLinearLayout.setVisibility(View.VISIBLE);



       String result =  getProjectTasksList(userName, selectedProjectName);

       if(result!=null)
       {

           googleJson = new Gson();
           try {
               JSONArray tempArray = new JSONArray(result);
               taskList = googleJson.fromJson(tempArray.toString(), ArrayList.class);
           } catch (JSONException e) {
               e.printStackTrace();
           }

           ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, taskList);
           taskSelectionListView.setAdapter(itemsAdapter);
       }

   }
    public void populategroupListView(String userName, String selectedProjectName) {


        taskSelectionLinearLayout.setVisibility(View.GONE);

        projectSelectionLinearLayout.setVisibility(View.GONE);

        groupSelectionLinearLayout.setVisibility(View.VISIBLE);




        String result =  getUserSupervisorsGroupsList(userName, selectedProjectName);
        if(result!=null)
        {

                googleJson = new Gson();
                try {
                    JSONArray tempArray = new JSONArray(result);
                    groupList = googleJson.fromJson(tempArray.toString(), ArrayList.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupList);
            groupsSelectionListView.setAdapter(itemsAdapter);
        }
    }


    public void populategroupListView(String userName, String selectedProjectName, String selectedProjectTaskName) {

        projectSelectionLinearLayout.setVisibility(View.GONE);

        taskSelectionLinearLayout.setVisibility(View.GONE);

        groupSelectionLinearLayout.setVisibility(View.VISIBLE);


        String result =  getUserTaskAssignedGroupsList(userName,selectedProjectName,selectedProjectTaskName);
        if(result!=null)
        {

            googleJson = new Gson();
            try {
                JSONArray tempArray = new JSONArray(result);
                groupList = googleJson.fromJson(tempArray.toString(), ArrayList.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupList);
            groupsSelectionListView.setAdapter(itemsAdapter);
        }
    }





    public String getProjectList(String userName, String groupLevel)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser",userName);
            postDataParams.put("selectionOptions",groupLevel);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fapuis/",postDataParams, activity,"application/json", "application/json");
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




    public String getUserSupervisorsGroupsList(String userName, String selectedProjectName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser",userName);
            postDataParams.put("nameOfProject",selectedProjectName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fusg/",postDataParams, activity,"application/json", "application/json");
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


    public String getUserTaskAssignedGroupsList(String userName, String selectedProjectName,String selectedProjectTaskName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser",userName);
            postDataParams.put("nameOfProject",selectedProjectName);
            postDataParams.put("nameOfProjectTask",selectedProjectTaskName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/futag/",postDataParams, activity,"application/json", "application/json");
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




    public String getProjectTasksList(String userName, String selectedProjectName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser",userName);
            postDataParams.put("nameOfProject",selectedProjectName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/faptuis/",postDataParams, activity,"application/json", "application/json");
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




