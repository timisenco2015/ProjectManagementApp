package com.zihron.projectmanagementapp.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.zihron.projectmanagementapp.R;

public class ProgressBarClass {
    private Dialog progressDialog;
    private  ProgressBar progressBar;
    private ChasingDots chasingDots;
    private static Activity activity;
   public ProgressBarClass(Activity activity)
    {

this.activity = activity;

    }

    public void showDialog()
    {
        progressDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        progressDialog.setContentView(R.layout.progressbarlayout);
        progressBar = (ProgressBar)progressDialog.findViewById(R.id.spin_kit);
        chasingDots = new ChasingDots();
        progressBar.setIndeterminateDrawable(chasingDots);
      progressDialog.show();
    }



    public void dismissDialog()
    {

        progressDialog.dismiss();
    }
}
