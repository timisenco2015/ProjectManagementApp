package com.zihron.projectmanagementapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.firebase.ui.auth.AuthUI;
//import com.firebase.ui.database.FirebaseListAdapter;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import ZihronChatApp.ZihronWorkChat.WorkChatPageActivity;

import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class ChatActivity extends Fragment {

    View rootView;
    Typeface fontAwesomeIcon;
    private SharedPreferences sharedPreferences;
    private TextView downwardChatOptSeletnTextView;
private TextView colleagueChatTextView;
private TextView friendsChatTextView;
    private RelativeLayout friendsChatLayout;
    private RelativeLayout colleaguesChatLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_chat, container, false);
        sharedPreferences = getContext().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        String userName =  sharedPreferences.getString("userName", null);
        fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/fontawesome-webfont.ttf");
        downwardChatOptSeletnTextView = (TextView) rootView.findViewById(R.id.downwardChatOptSeletnId);
        colleagueChatTextView = (TextView) rootView.findViewById(R.id.colleagueChatTextVWId);
        friendsChatTextView = (TextView)rootView.findViewById(R.id.friendsChatTextVWId);
        friendsChatLayout = (RelativeLayout)rootView.findViewById(R.id.friendsChatLayoutId);
        colleaguesChatLayout = (RelativeLayout)rootView.findViewById(R.id.colleaguesChatLayoutId);

        downwardChatOptSeletnTextView.setTypeface(fontAwesomeIcon);
        downwardChatOptSeletnTextView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slideupanddownanimator));
        friendsChatTextView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fadeinfadeout));
        Animation loadAnimation ;
        loadAnimation = AnimationUtils.loadAnimation( getContext(), R.anim.fadeinfadeout);
        loadAnimation.setStartOffset(30);
        colleagueChatTextView.startAnimation(loadAnimation);
        friendsChatLayout.animate().translationX(0).setDuration(3000);
        colleaguesChatLayout.animate().translationX(0).setDuration(3000);
        /*  getFullProjectDetails = new GetFullProjectDetails(userName);
        String token=null;
        try {
            token = getFullProjectDetails.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        */
        buildSendBirdClient(userName);

        colleaguesChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent workChatPageIntent = new Intent(getActivity(), WorkChatPageActivity.class);
                getActivity().startActivity(workChatPageIntent);
            }
        });

return rootView;
    }

    private void buildSendBirdClient(String userName)
    {
        SendBird.connect(userName, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    // Error.
                    return;
                }
            }
        });
    }







}