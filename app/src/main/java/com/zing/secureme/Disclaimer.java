package com.zing.secureme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Disclaimer extends AppCompatActivity {

    private Button agree,disagree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);
        agree = findViewById(R.id.agree);
        disagree = findViewById(R.id.disagree);
    }

    public void onDisagreeClick(View view) {
        finish();
    }

    public void onAgreeClick(View view) {
        startActivity(new Intent(this, SelfAssessmentActivity.class));
        finish();
    }
}