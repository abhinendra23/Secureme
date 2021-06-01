package com.zing.secureme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;

public class SelfAssessmentActivity extends AppCompatActivity {

    public RadioGroup q1,q2,q3,q4,q5,q6,q7;
    public RadioButton radioButton;
    public int yes=0,no=0;
    HashMap<String, Integer>map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_assessment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        q1=findViewById(R.id.q1);
        q2 = findViewById(R.id.q2);
        q3=findViewById(R.id.q3);
        q4=findViewById(R.id.q4);
        q5=findViewById(R.id.q5);
        q6=findViewById(R.id.q6);
        q7=findViewById(R.id.q7);





    }
    public void onCancelClick(View view){
       finish();
    }

    public void onSubmitClick(View view) {

        if(q1.getCheckedRadioButtonId()!=-1 &&
                q2.getCheckedRadioButtonId()!=-1 &&
                q3.getCheckedRadioButtonId()!=-1 &&
                q4.getCheckedRadioButtonId()!=-1 &&
                q5.getCheckedRadioButtonId()!=-1 &&
                q6.getCheckedRadioButtonId()!=-1 &&
                q7.getCheckedRadioButtonId()!=-1
        ) {

            if (q1.getCheckedRadioButtonId() == R.id.y1 ){
                yes++;
            } else {
                no++;
            }


            if (q2.getCheckedRadioButtonId() == R.id.y2) {
                yes++;
            } else {
                no++;
            }


            if (q3.getCheckedRadioButtonId() == R.id.y3) {
                yes++;
            } else {
                no++;
            }


            if (q4.getCheckedRadioButtonId() == R.id.y4) {
                yes++;
            } else {
                no++;
            }


            if (q5.getCheckedRadioButtonId() == R.id.y5) {
                yes++;
            } else {
                no++;
            }


            if (q6.getCheckedRadioButtonId() == R.id.y6) {
                yes++;
            } else {
                no++;
            }


            if (q7.getCheckedRadioButtonId() == R.id.y7) {
                yes++;
            } else {
                no++;
            }
                Intent intent = new Intent(this, AssessmentResult.class);
                intent.putExtra("Yes", String.valueOf(yes));
                startActivity(intent);
                finish();

        }else{
            Toast.makeText(this,"Mark All Answers",Toast.LENGTH_LONG);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}