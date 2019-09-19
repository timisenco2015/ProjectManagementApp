package com.zihron.projectmanagementapp.Utility;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.zihron.projectmanagementapp.ZihronProjectManagmentApplication;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequestClass {

    private String stringUrl;
    private JSONObject postDataParams;
    private ProgressBarClass progressBarClass;
    private Activity activity;
    GeneralRequest generalRequest;
    private String acceptType;
    private String contentType;

public HttpRequestClass(String stringUrl,JSONObject postDataParams,Activity activity,String acceptType, String contentType)
{
    this.stringUrl = stringUrl;
    this. postDataParams =  postDataParams;
    this.activity =activity;
    generalRequest = new GeneralRequest();
    this.acceptType = acceptType;
    this.contentType = contentType;
}

public String getResult()
{
    String result = null;
    if(ZihronProjectManagmentApplication.get().haveNetworkConnection()) {

        try {
            result = generalRequest.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    else
    {
        result="No network";
    }
    return result;
}




    public class GeneralRequest extends AsyncTask<String,String,String>
    {


        private String result;

        public GeneralRequest ()
        {

            progressBarClass = new ProgressBarClass(activity);

        }

        @Override
        protected void onPreExecute() {

            progressBarClass.showDialog();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(stringUrl); // here is your URL path



                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", contentType+"; charset=UTF-8");
                conn.setRequestProperty("Accept", acceptType);
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
                    progressBarClass.dismissDialog();
                    result = ""+responseCode;
                }
            } catch (Exception e) {
                progressBarClass.dismissDialog();
                result = "exception";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {


            progressBarClass.dismissDialog();

        }


    }


}
