package com.zihron.projectmanagementapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class AppraisalActivity extends Fragment {

RelativeLayout individualAppriasalReportBtn;
   private RelativeLayout   supervisorAppriasalReportBtn;
  private ImageView supervisorSwitchOptionsButton;
 private   RelativeLayout supervisorOptionslayout;
  private  RelativeLayout selectBySupervisorLayout;
    private  RelativeLayout selectByProjectLayout;
private RelativeLayout selectByGeneralLayout;

   // RelativeLayout supervisorAppriasalReport;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_appraisal, container, false);
        //ProjectTaskAssignedActivity.projectTaskAssignedPopUpWindow.dismiss();

        individualAppriasalReportBtn = (RelativeLayout) rootView.findViewById(R.id.weeklyAppriasalReportId);
        supervisorSwitchOptionsButton = (ImageView) rootView.findViewById(R.id.switchButtonId);
        supervisorOptionslayout = (RelativeLayout) rootView.findViewById(R.id.layout2Id);
       supervisorAppriasalReportBtn = (RelativeLayout) rootView.findViewById(R.id.supervisorAppriasalReportId);
        selectBySupervisorLayout = (RelativeLayout) rootView.findViewById(R.id.selectBySupervisorLLTId);
                selectByProjectLayout= (RelativeLayout) rootView.findViewById(R.id.selectByProjectLLTId);
selectByGeneralLayout = (RelativeLayout) rootView.findViewById(R.id.selectByGeneralLLTId);

        selectBySupervisorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aAIntent = new Intent(getActivity(),SupervisorAppraisalBySupervisor.class);
                AppraisalActivity.this.startActivity(aAIntent);
            }
        });

        selectByProjectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aAIntent = new Intent(getActivity(),SupervisorAppraisalByProjectActivity.class);
                AppraisalActivity.this.startActivity(aAIntent);
            }
        });
        individualAppriasalReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aAIntent = new Intent(getActivity(),IndividualAppraisalActivity.class);
                AppraisalActivity.this.startActivity(aAIntent);
            }
        });

        selectByGeneralLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aAIntent = new Intent(getActivity(),GeneralSupervisorReview.class);
                AppraisalActivity.this.startActivity(aAIntent);
            }
        });

        supervisorAppriasalReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supervisorOptionslayout.setVisibility(View.VISIBLE);
                supervisorAppriasalReportBtn.setVisibility(View.GONE);
                individualAppriasalReportBtn.setVisibility(View.GONE);
            }
        });

        supervisorSwitchOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supervisorOptionslayout.setVisibility(View.GONE);
                supervisorAppriasalReportBtn.setVisibility(View.VISIBLE);
                individualAppriasalReportBtn.setVisibility(View.VISIBLE);
            }
        });
        return rootView;
    }






    }