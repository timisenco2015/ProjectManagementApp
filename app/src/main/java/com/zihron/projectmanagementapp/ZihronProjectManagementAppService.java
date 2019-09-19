package com.zihron.projectmanagementapp;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


public class ZihronProjectManagementAppService extends Service {


    private CountDownTimer countDownTimer;
    private String remainingTime;
    private Boolean isRefreshedTokenExpired = false;
    private int finalDaysRemaining;
    @Override
    public void onCreate() {


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void countDownAcessTokenExp_Refresh(CognitoUserSession userSession)
    {

        Calendar c=new GregorianCalendar();
        Date dateNow =  c.getTime();
        c.add(Calendar.DATE, 30);
        Date dateIn30Days=c.getTime();
       Long dateToMillis = Math.abs(dateIn30Days.getTime() - dateNow.getTime());
        countDownTimer = new CountDownTimer(dateToMillis,1000)
        {
            public void onTick(long millisUntilFinished)
            {
                isRefreshedTokenExpired = false;
               remainingTime = calculateTimeRemaining(millisUntilFinished);

            }

            public void onFinish()
            {
                isRefreshedTokenExpired=true;
                ZihronProjectManagmentApplication.get().setRefreshTokenValid(isRefreshedTokenExpired);
            }
        }.start();

    }


    public String calculateTimeRemaining(long millisUntilFinished) {
        int millis = 0;
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        if (millisUntilFinished > 1000) {
            seconds = (int) millisUntilFinished / 1000;
            millis = (int) millisUntilFinished % 1000;
        }
        if (seconds > 60) {
            minutes = seconds / 60;
            seconds = seconds % 60;

        }

        if (minutes > 60) {
            hours = minutes / 60;
            minutes = minutes % 60;
        }

        if (hours > 24) {
            days = hours / 24;
            hours = hours % 24;
            finalDaysRemaining=days;
        }

        return days+" day(s) "+ hours+" hour(s) " + minutes + " min(s) " + seconds + " secs";
    }


}
