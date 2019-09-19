package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.CreateTemplateRequest;
import com.amazonaws.services.simpleemail.model.CreateTemplateResult;
import com.amazonaws.services.simpleemail.model.DeleteTemplateRequest;
import com.amazonaws.services.simpleemail.model.DeleteTemplateResult;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.amazonaws.services.simpleemail.model.Template;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ResetPasswordQuestionConfirmation extends AppCompatActivity {

   private String userName;
    private String userPassword;
    private JSONArray loginDetailsQuestAndAnsJArray;
    private EditText answersAEditText;
    private EditText answersBEditText;
    private EditText answersCEditText;
    private TextView questionATextView;
    private TextView questionBTextView;
    private TextView questionCTextView;
    private Activity activity;
    private String ansA;
    private String ansB;
    private String ansC;
    private static int ansAError=0;
    private static int ansBError=0;
    private static int ansCError=0;
    private String recordAnsA;
    private String recordAnsB;
    private String recordAnsC;
    private JSONObject loginDetailsObject;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private static final String FROM = "zihronProjectManagementApp@gmail.com";
    AmazonSimpleEmailService amazonSimpleEmailService;
    private static String templateName = "ZihronProjectManagementForgotEmail";
    private static String TO = "";
    private static final String CONFIGSET = "ZihronProjectEmailConfSet";
    private JSONObject loginDetailsJObject;
    private ImageView backArrowLogoImageView;
    private ProgressBarClass progressBarClass;
    private HttpRequestClass httpRequestClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_question_confirmation);
        answersAEditText = (EditText)findViewById(R.id.ansAEditTxtId);
        answersBEditText = (EditText)findViewById(R.id.ansBEditTxtId);
        answersCEditText =(EditText)findViewById(R.id.ansCEditTxtId);
        questionATextView = (TextView)findViewById(R.id.questionATexVwId);
        questionBTextView =(TextView)findViewById(R.id.questionBTexVwId);
        questionCTextView =(TextView)findViewById(R.id.questionCTexVwId);
        backArrowLogoImageView = (ImageView)findViewById(R.id.backArrowLogoId);
       loginDetailsObject = new JSONObject();
        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);
        alertDialogBuilder = new AlertDialog.Builder(activity);
        userName = getIntent().getStringExtra("userName");
        TO = userName;
        userPassword = getIntent().getStringExtra("userPassword");
        try {
            loginDetailsJObject = new JSONObject();
            loginDetailsJObject.put("userName", userName);
            loginDetailsJObject.put("userPassword", userPassword);
            String result = getLoginDetailsRestQuestions(userName);
            if(result!=null)
            {

                loginDetailsQuestAndAnsJArray = new JSONArray(result);


                    questionATextView.setText((loginDetailsQuestAndAnsJArray.getJSONObject(0)).getString("resetQuest"));
                    questionBTextView.setText((loginDetailsQuestAndAnsJArray.getJSONObject(1)).getString("resetQuest"));
                    questionCTextView.setText((loginDetailsQuestAndAnsJArray.getJSONObject(2)).getString("resetQuest"));
                recordAnsA = (loginDetailsQuestAndAnsJArray.getJSONObject(0)).getString("resetQuestAns");
                recordAnsB = (loginDetailsQuestAndAnsJArray.getJSONObject(1)).getString("resetQuestAns");
                recordAnsC = (loginDetailsQuestAndAnsJArray.getJSONObject(2)).getString("resetQuestAns");


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        backArrowLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rPQCIntent = new Intent(ResetPasswordQuestionConfirmation.this, ForgotLoginDetailsActivity.class);
                startActivity(rPQCIntent);
            }
        });
    }
    @Override
    public void onBackPressed() {

    }
    public void confirmSecurityQuestsAns(View view)
    {
        ansA = answersAEditText.getText().toString();
        ansB= answersBEditText.getText().toString();
        ansC= answersCEditText.getText().toString();
        boolean isError=false;
       if(ansAError<3 && ansBError<3 && ansCError<3) {
           if (ansA.length() == 0) {
               answersAEditText.setError("Please enter your answer in this field");
               isError =true;
           } else {
               if (!recordAnsA.equalsIgnoreCase(ansA)) {
                   answersAEditText.setError("Your answer does match with the answer we have in our record for this question");
                   ansAError++;
                   isError =true;
               }
           }

           if (ansB.length() == 0) {
               answersBEditText.setError("Please enter your answer in this field");
               isError =true;
           } else {
               if (!recordAnsB.equalsIgnoreCase(ansB)) {
                   answersBEditText.setError("Your answer does match with the answer we have in our record for this question");
                   ansBError++;
                   isError =true;
               }
           }

           if (ansC.length() == 0) {
               answersCEditText.setError("Please enter your answer in this field");
               isError =true;
           } else {
               if (!recordAnsC.equalsIgnoreCase(ansC)) {
                   answersCEditText.setError("Your answer does match with the answer we have in our record for this question");
                   isError =true;
                   ansCError++;
               }
           }

           if(!isError)
           {
               alertDialogBuilder.setMessage("Login details sent to your email we have on record");
               alertDialogBuilder.setPositiveButton("Okay",
                       new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface arg0, int arg1) {
                               sendLoginDetailsToEmail();
                               Intent rPQCIntent = new Intent(ResetPasswordQuestionConfirmation.this, LoginActivity.class);
                               startActivity(rPQCIntent);
                           }
                       });
               alertDialogBuilder.show();
           }
       }
       else
       {
           final Dialog dialog = new Dialog(activity, R.style.WideDialog);
           dialog.setContentView(R.layout.passwordnotresetlayout);
           Window window = dialog.getWindow();
           window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
           TextView okayTextView = (TextView)dialog.findViewById(R.id.homePressedId);
           okayTextView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {



                   Intent rPQCIntent = new Intent(ResetPasswordQuestionConfirmation.this, LoginActivity.class);
                   startActivity(rPQCIntent);
                   dialog.dismiss();
               }
           });
           dialog.show();
       }

    }

    private void sendEmail()  throws IOException {

        amazonSimpleEmailService = ZihronProjectManagmentApplication.get().getAwsCrediantils();
       if(verifyEmailAddress(ZihronProjectManagmentApplication.get().getAmazonSimpleEmailService(), TO)) {
            deleteTemplate();
            Template template = new Template();
            template.setTemplateName(templateName);
            template.setSubjectPart("Password Reset");
            template.setHtmlPart("<head>\n" +
                    "\t  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "\t  <!--[if !mso]><!-->\n" +
                    "\t  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "\t  <!--<![endif]-->\n" +
                    "\t  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "\t  <title></title>\n" +
                    "\t  <!--[if !mso]><!-->\n" +
                    "\t  <style type=\"text/css\">\n" +
                    "\t  @import url('https://fonts.googleapis.com/css?family=PT+Serif');\n" +
                    "\t  @import url('https://fonts.googleapis.com/css?family=Ruslan+Display');\n" +
                    "\t  @import url('https://fonts.googleapis.com/css?family=Slabo+27px');\n" +
                    "\t  @import url('https://fonts.googleapis.com/css?family=Anton');\n" +
                    "\t\n" +
                    "\t  </style>\n" +
                    "\t  <!--<![endif]-->\n" +
                    "\t  <!--[if (gte mso 9)|(IE)]>\n" +
                    "\t\t<style type=\"text/css\">\n" +
                    "\t\t\t.address-description a {color: #000000 ; text-decoration: none;}\n" +
                    "\t\t\ttable {border-collapse: collapse ;}\n" +
                    "\t\t</style>\n" +
                    "\t\t<![endif]-->\n" +
                    "\t</head>\n" +
                    "\n" +
                    "\t<body bgcolor=\"#e1e5e8\" style=\"margin-top:0 ;margin-bottom:0 ;margin-right:0 ;margin-left:0 ;padding-top:0px;padding-bottom:0px;padding-right:0px;padding-left:0px;background-color:#e1e5e8;\">\n" +
                    "\t  <!--[if gte mso 9]>\n" +
                    "\t<center>\n" +
                    "\t<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\"><tr><td valign=\"top\">\n" +
                    "\t<![endif]-->\n" +
                    "\t  <center style=\"width:100%;table-layout:fixed;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#e1e5e8;\">\n" +
                    "\t\t<div style=\"max-width:600px;margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;\">\n" +
                    "\t\t  <table align=\"center\" cellpadding=\"0\" style=\"border-spacing:0;font-family:'Muli',Arial,sans-serif;color:#333333;Margin:0 auto;width:100%;max-width:600px;\">\n" +
                    "\t\t\t<tbody>\n" +
                    "\t\t\t\n" +
                    "\t\t\t  <tr>\n" +
                    "\t\t\t \n" +
                    "\t\t\t\t\t<td style=\"font-family: 'Ruslan Display', cursive; font-weight:100;font-size:50px;padding-top:40px; padding-bottom:10px;\"><span ><center>Zihron</center></span></td>\n" +
                    "\t\t\t\t</tr>\n" +
                    "\t\t\t  <!-- Start of Email Body-->\n" +
                    "\t\t\t  <tr>\n" +
                    "\t\t\t\t<td class=\"one-column\" style=\"padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;background-color:#ffffff;\">\n" +
                    "\t\t\t\t  <!--[if gte mso 9]>\n" +
                    "\t\t\t\t\t\t<center>\n" +
                    "\t\t\t\t\t\t<table width=\"80%\" cellpadding=\"20\" cellspacing=\"30\"><tr><td valign=\"top\">\n" +
                    "\t\t\t\t\t\t<![endif]-->\n" +
                    "\t\t\t\t  <table style=\"border-spacing:0;\" width=\"100%\">\n" +
                    "\t\t\t\t\t<tbody>\n" +
                    "\t\t\t\t\t  <tr>\n" +
                    "\t\t\t\t\t\t<td align=\"center\" class=\"inner\" style=\"padding-top:15px;padding-bottom:15px;padding-right:30px;padding-left:30px;\" valign=\"middle\"><span class=\"sg-image\" data-imagelibrary=\"%7B%22width%22%3A%22255%22%2C%22height%22%3A93%2C%22alt_text%22%3A%22Forgot%20Password%22%2C%22alignment%22%3A%22%22%2C%22border%22%3A0%2C%22src%22%3A%22https%3A//marketing-image-production.s3.amazonaws.com/uploads/35c763626fdef42b2197c1ef7f6a199115df7ff779f7c2d839bd5c6a8c2a6375e92a28a01737e4d72f42defcac337682878bf6b71a5403d2ff9dd39d431201db.png%22%2C%22classes%22%3A%7B%22sg-image%22%3A1%7D%7D\"><img alt=\"Forgot Password\" class=\"banner\" height=\"93\" src=\"https://marketing-image-production.s3.amazonaws.com/uploads/35c763626fdef42b2197c1ef7f6a199115df7ff779f7c2d839bd5c6a8c2a6375e92a28a01737e4d72f42defcac337682878bf6b71a5403d2ff9dd39d431201db.png\" style=\"border-width: 0px; margin-top: 30px; width: 255px; height: 93px;\" width=\"255\"></span></td>\n" +
                    "\t\t\t\t\t  </tr>\n" +
                    "\t\t\t\t\t  <tr>\n" +
                    "\t\t\t\t\t\t<td class=\"inner contents center\" style=\"padding-top:15px;padding-bottom:15px;padding-right:30px;padding-left:30px;text-align:left;\">\n" +
                    "\t\t\t\t\t\t  <center>\n" +
                    "\t\t\t\t\t\t\t<p class=\"h1 center\" style=\"Margin:0;text-align:center;font-family: 'PT Serif', serif;,'Arial Narrow',Arial;font-weight:100;font-size:30px;Margin-bottom:17px;\">Forgot your password?</p>\n" +
                    "\t <p class=\"description center\" style=\"font-family:'Muli','Arial Narrow',Arial;Margin:0;text-align:center;max-width:340px;color:#a1a8ad;line-height:24px;font-size:15px;Margin-bottom:10px;margin-left: auto; margin-right: auto;\"><span style=\"color: rgb(161, 168, 173); font-family: 'Slabo 27px', serif; font-size: 13px; text-align: center; background-color: rgb(255, 255, 255);\">That's okay, it happens! Your login details are found below.</span></p>\n" +
                    "\t\t\t\t\t\t\t<p style=\"color: rgb(161, 168, 173); font-family: 'Slabo 27px', serif; font-size: 15px; text-align: center; background-color: rgb(255, 255, 255);\"><span>Username:</span><span style=\"font-family: 'Anton', sans-serif;font-size:12px;\"> {{userName}}</></p>\n" +
                    "\t\t\t\t\t\t\t  \n" +
                    "\t <p style=\"color: rgb(161, 168, 173); font-family: 'Slabo 27px', serif; font-size: 15px; text-align: center; background-color: rgb(255, 255, 255);\"><span>Password:</span><span style=\"font-family: 'Anton', sans-serif;font-size:12px;\"> {{userPassword}}</></p>\n" +
                    "\t\t\t\t\t\t\t<!--[if (gte mso 9)|(IE)]><br>&nbsp;<![endif]-->\n" +
                    "\t\t\t\t\t\t\t<!--[if (gte mso 9)|(IE)]><br>&nbsp;<![endif]--></center>\n" +
                    "\t\t\t\t\t\t</td>\n" +
                    "\t\t\t\t\t  </tr>\n" +
                    "\t\t\t\t\t</tbody>\n" +
                    "\t\t\t\t  </table>\n" +
                    "\t\t\t\t  <!--[if (gte mso 9)|(IE)]>\n" +
                    "\t\t\t\t\t\t</td></tr></table>\n" +
                    "\t\t\t\t\t\t</center>\n" +
                    "\t\t\t\t\t\t<![endif]-->\n" +
                    "\t\t\t\t</td>\n" +
                    "\t\t\t  </tr>\n" +
                    "\t\t\t  <!-- End of Email Body-->\n" +
                    "\t\t\t \n" +
                    "\t\t\t \n" +
                    "\t\t\t  <!-- whitespace -->\n" +
                    "\t\t\t  <tr>\n" +
                    "\t\t\t\t<td height=\"5\">\n" +
                    "\t\t\t\t  <p style=\"line-height: 5px; padding: 0 0 0 0; margin: 0 0 0 0;\">&nbsp;</p>\n" +
                    "\n" +
                    "\t\t\t\t  <p>&nbsp;</p>\n" +
                    "\t\t\t\t</td>\n" +
                    "\t\t\t  </tr>\n" +
                    "\t\t\t  <!-- Footer -->\n" +
                    "\t\t\t  <tr>\n" +
                    "\t\t\t\t<td style=\"padding-top:0;padding-bottom:0;padding-right:30px;padding-left:30px;text-align:center;Margin-right:auto;Margin-left:auto;\">\n" +
                    "\t\t\t\t  <center>\n" +
                    "\t\t\t\t\t<p style=\"font-family:'Muli',Arial,sans-serif;Margin:0;text-align:center;Margin-right:auto;Margin-left:auto;font-size:15px;color:#a1a8ad;line-height:23px;\">Problems or questions? Call us at\n" +
                    "\t\t\t\t\t  <nobr><a class=\"tel\" href=\"tel:2128102899\" style=\"color:#a1a8ad;text-decoration:none;\" target=\"_blank\"><span style=\"white-space: nowrap\">212.810.2899</span></a></nobr>\n" +
                    "\t\t\t\t\t</p>\n" +
                    "\n" +
                    "\t\t\t\t\t<p style=\"font-family:'Muli',Arial,sans-serif;Margin:0;text-align:center;Margin-right:auto;Margin-left:auto;font-size:15px;color:#a1a8ad;line-height:23px;\">or email <a href=\"mailto:hello@vervewine.com\" style=\"color:#a1a8ad;text-decoration:underline;\" target=\"_blank\">hello@vervewine.com</a></p>\n" +
                    "\n" +
                    "\t\t\t\t\t<p style=\"font-family:'Muli',Arial,sans-serif;Margin:0;text-align:center;Margin-right:auto;Margin-left:auto;padding-top:10px;padding-bottom:0px;font-size:15px;color:#a1a8ad;line-height:23px;\">© Verve Wine <span style=\"white-space: nowrap\">24 \u200BHubert S\u200Bt\u200B</span>, <span style=\"white-space: nowrap\">Ne\u200Bw Yor\u200Bk,</span> <span style=\"white-space: nowrap\">N\u200BY 1\u200B0013</span></p>\n" +
                    "\t\t\t\t  </center>\n" +
                    "\t\t\t\t</td>\n" +
                    "\t\t\t  </tr>\n" +
                    "\t\t\t  <!-- whitespace -->\n" +
                    "\t\t\t  <tr>\n" +
                    "\t\t\t\t<td height=\"10\">\n" +
                    "\t\t\t\t  <p style=\"line-height: 40px; padding: 0 0 0 0; margin: 0 0 0 0;\">&nbsp;</p>\n" +
                    "\n" +
                    "\t\t\t\t  <p>&nbsp;</p>\n" +
                    "\t\t\t\t</td>\n" +
                    "\t\t\t  </tr>\n" +
                    "\t\t\t</tbody>\n" +
                    "\t\t  </table>\n" +
                    "\t\t</div>\n" +
                    "\t  </center>\n" +
                    "\t  <!--[if gte mso 9]>\n" +
                    "\t</td></tr></table>\n" +
                    "\t</center>\n" +
                    "\t<![endif]-->\n" +
                    "\n" +
                    "\n" +
                    "\t</body>");
            CreateTemplateRequest createTemplateRequest = new CreateTemplateRequest();
            createTemplateRequest.setTemplate(template);
            CreateTemplateResult result = amazonSimpleEmailService.createTemplate(createTemplateRequest);

            VerifyEmailAddressRequest emailAddressRequest = new VerifyEmailAddressRequest();
            emailAddressRequest.withEmailAddress(TO);


            SendTemplatedEmailRequest templateRequest = new SendTemplatedEmailRequest()
                    .withDestination(new Destination().withToAddresses(TO))
                    .withSource(FROM)
                    .withTemplateData(loginDetailsJObject.toString())
                    .withTemplate(templateName)
                    .withConfigurationSetName(CONFIGSET);


            amazonSimpleEmailService.sendTemplatedEmail(templateRequest);

        }

    }
    private  void deleteTemplate() {

        DeleteTemplateRequest deleteTemplateRequest = new DeleteTemplateRequest();

        deleteTemplateRequest.setTemplateName(templateName);

        DeleteTemplateResult result = amazonSimpleEmailService.deleteTemplate(deleteTemplateRequest);



    }


    public void sendLoginDetailsToEmail()
    {
        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    sendEmail();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }







    private boolean verifyEmailAddress(AmazonSimpleEmailService ses, String address) {
        boolean isVerified = true;
        ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
        if (!verifiedEmails.getVerifiedEmailAddresses().contains(address))
        {
            ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
            isVerified = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Because of security and not to send your details to wrong email, Please verify your email first and request for password request again.");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    alertDialog.dismiss();
                    Intent nUAIntnent = new Intent(ResetPasswordQuestionConfirmation.this, LoginActivity.class);
                    startActivity(nUAIntnent);
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }
        return isVerified;
    }


    public String getLoginDetailsRestQuestions(String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("emailOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fldrq/",postDataParams, activity,"text/plain", "application/json");
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
