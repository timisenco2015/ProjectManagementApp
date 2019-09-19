package com.zihron.projectmanagementapp;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
private static final String TAG="MyFirebaseInstanceIDService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedTokens = FirebaseInstanceId.getInstance().getToken();
    }
}
