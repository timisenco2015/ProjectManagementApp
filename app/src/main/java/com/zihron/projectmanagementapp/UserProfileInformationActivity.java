package com.zihron.projectmanagementapp;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.shapeofview.shapes.CircleView;
import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileInformationActivity extends Fragment {

    private Button editProfileButton;
    private SharedPreferences sharedPreferences;
    private String userName;
    private Activity activity;
    private S3ImageClass s3ImageClass;
   private CircleView userfriendsCircleView;

    private CircleImageView profileCircleImageView;
    private TextView userFullNameTextView;
    private TextView userEmailTextView;
    private String userFullName;
    private String firstName;
    private String lastName;
    private ImageView logOutImageView;
    private TextView userCreatedProjectCountTextVW;
    private TextView projectDelegatedCountTextView;
    private TextView userCreatedTaskCountTextView;
    private TextView taskAssgndToUserCountTextView;
    private HttpRequestClass httpRequestClass;
    private ProgressBarClass progressBarClass;
    private String oneSignalId;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private boolean checkForPermission = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_user_profile_information, container, false);

        if( ProjectTaskAssignedActivity.popUp !=null)
        {
            ProjectTaskAssignedActivity.popUp.dismiss();
        }

        if( CalendarActivity.popUp !=null)
        {
            CalendarActivity.popUp.dismiss();
        }
        activity = getActivity();
        progressBarClass = new ProgressBarClass(activity);
        Context context = getContext();
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        editProfileButton = rootView.findViewById(R.id.editProfileBtnId);
        profileCircleImageView = rootView.findViewById(R.id.profileImageVWId);
        userfriendsCircleView = (CircleView) rootView.findViewById(R.id.userfriendsLLTId);
        userFullNameTextView = (TextView)rootView.findViewById(R.id.userFullNameTextVWId);
        userEmailTextView = (TextView)rootView.findViewById(R.id.usernameTextVWId);
        logOutImageView = (ImageView)rootView.findViewById(R.id.logOutImageVWId);
        userCreatedProjectCountTextVW = (TextView)rootView.findViewById(R.id.userCreatedProjectCountTxtVwId);
        projectDelegatedCountTextView = (TextView)rootView.findViewById(R.id.projectDelegatedCountTxtVWId);
        userCreatedTaskCountTextView = (TextView)rootView.findViewById(R.id.userCreatedTaskCountTxtVwId);
        taskAssgndToUserCountTextView = (TextView )rootView.findViewById(R.id.taskAssgndToUserCountTxtVWId);
        sharedPreferences = getActivity().getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        firstName = sharedPreferences.getString("firstName", null);
        lastName = sharedPreferences.getString("lastName", null);
        oneSignalId =   sharedPreferences.getString("oneSignalId", null);
        userFullName = firstName+" "+lastName;
        userEmailTextView.setText(userName);
        userFullNameTextView.setText(userFullName);
        s3ImageClass = new S3ImageClass();
       try {
            String result = getUserCountDetails();
            if (result!=null)
            {
                JSONArray tempJArray = new JSONArray(result);
                JSONObject tempObject = new JSONObject(tempJArray.getString(0));

                userCreatedProjectCountTextVW.setText(tempObject.getString("projectCreatedCount"));
                projectDelegatedCountTextView.setText(tempObject.getString("projectDelegatedCount"));
                userCreatedTaskCountTextView.setText(tempObject.getString("projectTaskCreatedCount"));
                taskAssgndToUserCountTextView.setText(tempObject.getString("projectTaskAssignedCount"));
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }


        if(!s3ImageClass.hasPermission())
        {
            checkForPermission = s3ImageClass.getPermission();
        }

        if(s3ImageClass.hasPermission() && checkForPermission)
        {

            if(s3ImageClass.confirmIfImageInPhone(userName))
            {
                profileCircleImageView.setImageBitmap(s3ImageClass.readFromPhone(userName));
            }

            else {
                s3ImageClass = new S3ImageClass(activity,userName, "profilepicfolder");
                Bitmap bitMap = s3ImageClass.getImageBitMap();
                if (s3ImageClass.isObjectExists()) {
                    if(s3ImageClass.hasPermission()) {
                        s3ImageClass.writeToPhone(userName, bitMap);
                    }
                    profileCircleImageView.setImageBitmap(s3ImageClass.getImageBitMap());
                    profileCircleImageView.invalidate();
                }
                else
                {
                    Picasso.with(activity).load("https://ui-avatars.com/api/?name="+firstName + " " + lastName+"&background=90a8a8&color=fff&size=128").into( profileCircleImageView);
                }
            }
        }





        userfriendsCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uPIAIntent = new Intent(getActivity(), FriendsActivity.class);
                uPIAIntent.putExtra("notificationLoad","no");
                getActivity().startActivity(uPIAIntent);
            }
        });
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog groupButtomSheetDialog = new BottomSheetDialog(getActivity());
                View parentView = getLayoutInflater().inflate(R.layout.edituserprofilecustomlayout, null);
                LinearLayout onEditProfilePicsClickLayout = (LinearLayout) parentView.findViewById(R.id.onEditProfilePicsClick);
                LinearLayout onEditGalleryPicsClick = (LinearLayout) parentView.findViewById(R.id.onEditGallaeryPicsClickId);
                LinearLayout onEditEmailAndNamePicsClickLayout = (LinearLayout) parentView.findViewById(R.id.onEditEmailAndNamePicsClick);


                groupButtomSheetDialog.setContentView(parentView);
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
                bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
                groupButtomSheetDialog.show();


                onEditProfilePicsClickLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupButtomSheetDialog.cancel();
                        Intent uPIAIntent = new Intent(getActivity(), CameraActivity.class);
                        getActivity().startActivity(uPIAIntent);

                    }
                });


                onEditGalleryPicsClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupButtomSheetDialog.cancel();
                        Intent uPIAIntent = new Intent(getActivity(), GalleryActivity.class);
                        uPIAIntent.putExtra("fullName",userFullName);
                        uPIAIntent.putExtra("folderType","profilepicfolder");
                        uPIAIntent.putExtra("accesName",userName);
                        uPIAIntent.putExtra("selectionType","Friend");
                        getActivity().startActivity(uPIAIntent);

                    }
                });


                onEditEmailAndNamePicsClickLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupButtomSheetDialog.cancel();
                        Intent uPIAIntent = new Intent(getActivity(), EditUserInformationActivity.class);
                        getActivity().startActivity(uPIAIntent);
                    }
                });




            }
        });

        logOutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutDialog();
            }
        });
        return rootView;
    }



    public void logOutDialog()
    {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.logoutdialoglayout);

        Button logOutAppButton = (Button) dialog.findViewById(R.id.logOutAppBtnId);
        ImageView closeDialogImageView = (ImageView)dialog.findViewById(R.id.closeDialogImageVWId);
        // if button is clicked, close the custom dialog
        logOutAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String result = setOneSignalIsLogout(userName,oneSignalId);
               if(result.equalsIgnoreCase("Record Not Updated"))
               {
                   Toast.makeText(activity,"Can't log you out at this moment. Please try again",Toast.LENGTH_LONG).show();
               }
               else if (result.equalsIgnoreCase("Record Updated"))
               {SharedPreferences.Editor editor =sharedPreferences.edit();
                   editor.clear();
                   editor.commit();
                   Log.e("++--0",""+ZihronProjectManagmentApplication.get().getIsRefreshTokenValid());
                   ZihronProjectManagmentApplication.get().getCognitoUser().signOut();
                   Intent uPIAIntent = new Intent(getActivity(), LoginActivity.class);
                   getActivity().startActivity(uPIAIntent);
               }
                dialog.dismiss();
            }
        });

        closeDialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public String getUserCountDetails()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfuser",userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/futapc/",postDataParams, activity,"application/json", "application/json");
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

    public String setOneSignalIsLogout(String userName,String oneSignalId)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser",userName);
            postDataParams.put("idOfOneSignal",oneSignalId);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/setotlo/",postDataParams, activity,"text/plain", "application/json");
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

