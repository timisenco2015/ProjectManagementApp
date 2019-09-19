package com.zihron.projectmanagementapp;

/**
 * Created by ayoba on 2017-12-20.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.shapeofview.shapes.BubbleView;

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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zihron.projectmanagementapp.ArrowDirection.RIGHT;


public class CustomUserNotificationsListViewAdapter extends BaseAdapter implements ListAdapter
{
    private JSONArray valueArray;
    private static Context context;
    private static LayoutInflater inflater;
    private TextView notidateTextView;
    private TextView notiStatusTextView;
    private Activity activity;
    private LinearLayout allNotificationsBckgrndLinearLayout;
private  ProjectDelegateNotiReview  projectDelegateNotiReview;
    private String firstName = null;
   private String lastName = null;
   private String notificationSubType=null;
   private  String notificationType = null;
   private String notificationSenderUsername=null;
   private String notificationCreationDate=null;
   private ImageView navToReviewDetailsImageView;
  private  ImageView navToFullDetailsImageView;
private String notificationId;
private String userName;
private FriendRequestNotiReview friendRequestNotiReview;
private boolean endOfListView;
private boolean firstOfListView;
private GroupMemberNotiReview groupMemberNotiReview;
private ProjectTaskAssignedNotiReview projectTaskAssignedNotiReview;
private PopupWindow popupWindow;
private int membersCount =0;
    private int[] location;
    private boolean startOfListView =false;
    private TextView textStatusTextView;
    public CustomUserNotificationsListViewAdapter(Context context, JSONArray valueArray,final Activity activity, String userName)
    {

        this.valueArray =valueArray;
        this.context=context;
        this.activity = activity;
        this.userName = userName;



    }


    @Override
    public int getCount() {
        membersCount = valueArray.length();
        return membersCount;
    }

    @Override
    public JSONArray getItem(int position) {
        JSONArray tempArray = null;
        try {
            tempArray = valueArray.getJSONArray(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempArray;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

JSONArray tempArray =  getItem(position);

        boolean notificationStatus =false;
        try {
            notificationId = tempArray.getString(0);
            notificationSubType = tempArray.getString(1);
            notificationType = tempArray.getString(2);
            notificationSenderUsername = tempArray.getString(3);
            firstName = tempArray.getString(4);
            lastName = tempArray.getString(5);
            notificationCreationDate = tempArray.getString(6);
            notificationStatus = tempArray.getBoolean(7);
        } catch (JSONException e) {
            e.printStackTrace();
        }




      //  Configuration userPhoneConfig = context.getResources().getConfiguration();

            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.usernotificationscustomlayout,parent, false);
        notidateTextView = (TextView)convertView.findViewById(R.id.notidateTextVWId);
        notiStatusTextView = (TextView)convertView.findViewById(R.id.notiStatusTextVWId);
        navToReviewDetailsImageView = (ImageView)convertView.findViewById(R.id.navToReviewDetailsId);
        navToFullDetailsImageView = (ImageView)convertView.findViewById(R.id.navToFullDetailsId);
        textStatusTextView= (TextView)convertView.findViewById(R.id.textStatusTextWVId);

        allNotificationsBckgrndLinearLayout = (LinearLayout)convertView.findViewById(R.id.allNotificationsBckgrndLLT);
        navToReviewDetailsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(position==(valueArray.length()-1) ||position==(valueArray.length()-2))
                {
                    endOfListView=true;
                }

                else
                {
                    endOfListView=false;
                }





                JSONArray tempArray =  getItem(position);
                try {
                    notificationType = tempArray.getString(2);

                if(notificationType.equalsIgnoreCase("01"))
                {

                    friendRequestNotiReview = new FriendRequestNotiReview(tempArray.getString(0), activity, (View)view.getParent().getParent().getParent(),tempArray.getString(4),tempArray.getString(5),endOfListView,membersCount);
                }
                else  if(notificationType.equalsIgnoreCase("02"))
                {
                    groupMemberNotiReview = new GroupMemberNotiReview(tempArray.getString(0), activity, (View)view.getParent().getParent().getParent(),tempArray.getString(4),tempArray.getString(5),endOfListView,startOfListView,membersCount);
                }
                else  if(notificationType.equalsIgnoreCase("03"))
                {

                    projectDelegateNotiReview = new ProjectDelegateNotiReview(tempArray.getString(0), tempArray.getString(1), tempArray.getString(2), activity,  (View)view.getParent().getParent().getParent(),  userName, tempArray.getString(3) ,tempArray.getString(4),tempArray.getString(5),endOfListView,startOfListView);

                }
                else  if(notificationType.equalsIgnoreCase("04"))
                {

                    projectTaskAssignedNotiReview = new ProjectTaskAssignedNotiReview(tempArray.getString(0),  activity,  (View)view.getParent().getParent().getParent(), tempArray.getString(4),tempArray.getString(5),endOfListView,startOfListView);

                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        navToFullDetailsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(notificationType.equalsIgnoreCase("01"))
                {
                    Intent cUNLVAIntent = new Intent(activity,FriendsActivity.class);
                    cUNLVAIntent.putExtra("notificationLoad","yes");
                    activity.startActivity(cUNLVAIntent);
                }
                else  if(notificationType.equalsIgnoreCase("02"))
                {
                    generateGroupReview(view);
                }

            }
        });

        if(!notificationStatus)
        {
            allNotificationsBckgrndLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.color42));
        }
        else
        {
            allNotificationsBckgrndLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.color15));
        }


        switch(notificationType)
        {
            case "01":
                friendRequestNotification(firstName,lastName,notificationSubType, notiStatusTextView);
                break;
            case "02":
                projectGroupMemberAddDelNotification(firstName,lastName,notificationSubType,notiStatusTextView);
                break;
            case "03":
                navToFullDetailsImageView.setVisibility(View.GONE);
                projectDelegatedToMemberNotification(firstName,lastName,notificationSubType,notiStatusTextView);
                break;
            case "04":
                navToFullDetailsImageView.setVisibility(View.GONE);
                projectTaskAssignedToMemberNotification(firstName,lastName, notificationSubType,notiStatusTextView);
                break;
            default:
                break;
        }

        notidateTextView.setText(notificationCreationDate);



        return convertView;

    }


    public void generateGroupReview(View view)
    {
        View customView = inflater.inflate(R.layout.groupdetailsreviewlayout,null);
        CircleImageView groupCircleImageView = customView.findViewById(R.id.groupImageId);
        TextView groupNameTextView = customView.findViewById(R.id.groupNameTxtVWId);
        TextView groupCreadtedByTextView = customView.findViewById(R.id.groupCreadtedByTextVWId);
        LinearLayout groupMembersLinearLayout = customView.findViewById(R.id.groupMembersLLTId);
        this.location = new int[2];

                popupWindow = new PopupWindow(
                customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        );

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setElevation(5.0f);

        BubbleView popUpLsyout =(BubbleView) customView.findViewById(R.id.popLayoutId);


        popupWindow.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, view.getWidth()+location[0]-5, view.getHeight() + location[1]+5);





    }


    public void friendRequestNotification(String firstName,String  lastName,String  friendRequestAction, TextView  notiStatusTextView)
    {
        switch(friendRequestAction)
        {
            case "Accepted":
                notiStatusTextView.setText("Friend request sent to "+firstName+" "+lastName+" has been accepted");
                break;
            case "Requested":
                notiStatusTextView.setText("You have a friend request from "+firstName+" "+lastName);
                break;
            case "Denied":
                notiStatusTextView.setText("Friend request sent to "+firstName+" "+lastName+" has been denied");
                break;
            default:
                break;
        }
    }

    public void projectGroupMemberAddDelNotification(String firstName,String  lastName,String  groupAddAction, TextView  notiStatusTextView)
    {
        switch(groupAddAction)
        {
            case "Accepted":
                notiStatusTextView.setText(firstName+" "+lastName+" has accepted your project add group request");
                break;
            case "Requested":
                notiStatusTextView.setText(firstName+" "+lastName+" has sent a request to add you to a project group");
                break;
            case "Removed":
                notiStatusTextView.setText("You have been removed as a member of a project group by"+firstName+" "+lastName);
                break;
            case "Denied":
                notiStatusTextView.setText(firstName+" "+lastName+" has denied your project add group request");
                break;
            default:
                break;
        }
    }

    public void projectDelegatedToMemberNotification(String firstName,String  lastName,String  groupAddAction, TextView  notiStatusTextView)
{
    switch(groupAddAction)
    {
        case "Accepted":
            notiStatusTextView.setText("A project supervisor request sent to "+firstName+" "+lastName+" has been accepted");
            break;
        case "Requested":
            notiStatusTextView.setText(firstName+" "+lastName+"has sent a project supervisor request to you");
            break;
        case "Removed":
            notiStatusTextView.setText("You have been removed as a project supervisor by "+firstName+" "+lastName);
            break;
        case "Denied":
            notiStatusTextView.setText("A project supervisor request sent to "+firstName+" "+lastName+" has been denied");
            break;
        case "Updated":
            notiStatusTextView.setText(firstName+" "+lastName+" made changes to priviledges you have for a project");
            break;
        default:
            break;
    }
}



    public void projectTaskAssignedToMemberNotification(String firstName,String  lastName,String  groupAddAction, TextView  notiStatusTextView)
    {
        switch(groupAddAction)
        {
            case "Accepted":
                notiStatusTextView.setText("A project task assigned request sent to "+firstName+" "+lastName+" has been accepted");
                break;
            case "Requested":
                notiStatusTextView.setText(firstName+" "+lastName+" has sent a request to assign a project task to you");
                break;
            case "Removed":
                notiStatusTextView.setText("You have been unassigned from a project task by "+firstName+" "+lastName);
                break;
            case "Denied":
                notiStatusTextView.setText("A project task assigned request sent to "+firstName+" "+lastName+" has been Denied");
                break;
            default:
                break;
        }
    }








}