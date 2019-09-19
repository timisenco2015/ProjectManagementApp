package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class AsyncErrorDialogDisplay {
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    public AsyncErrorDialogDisplay(Activity activity)
    {
        alertDialogBuilder = new AlertDialog.Builder(activity);
    }
    public AsyncErrorDialogDisplay(Context context)
    {
        alertDialogBuilder = new AlertDialog.Builder(context);
    }



    public void errorCodeCheck(int responseCode) {


        switch (responseCode) {

            case 500:
                errorDialogDisplay("It seems you did not have the right value in one of the fields. Please check all the fields again");
                break;
            case 400:
                errorDialogDisplay("You probably sent wrong request to server. please check your request again before sending it to server");
                break;
            case 503:
                errorDialogDisplay("ooooopppPPPSSSS!!! Server currently not available. Please try again in some minutes");
                break;
        }
    }

    public void errorDialogDisplay(String errorDisplay)
    {
        alertDialogBuilder.setMessage(errorDisplay);
        alertDialogBuilder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void handleException(Activity activity)
    {
        Toast.makeText(activity,"Error Occured",Toast.LENGTH_LONG).show();
    }
    public void handleException(Context context)
    {
        Toast.makeText(context,"Error Occured",Toast.LENGTH_LONG).show();
    }

}
