package com.zing.secureme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zing.secureme.views.MainActivity;

public class AssessmentResult extends AppCompatActivity {

    TextView result,resultAdvice,lastText;
    CardView cardView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_result);
        String y = getIntent().getStringExtra("Yes");
        result = findViewById(R.id.result);
        int yes = Integer.valueOf(y);
        resultAdvice = findViewById(R.id.result_advice);
        cardView = findViewById(R.id.card);
        lastText = findViewById(R.id.last_text);

        if(yes<=1){
            result.setText("You are Safe!");
            resultAdvice.setText(R.string.safe);
            cardView.setBackgroundColor(getColor(R.color.list_color_4));

        }else if(yes<=3){
            result.setText("Isolate Yourself!");
            resultAdvice.setText(R.string.isolate);
            cardView.setCardBackgroundColor(getColor(R.color.yellow));
        }else{
            result.setText("Suspected!");
            resultAdvice.setText(R.string.suspected);
            cardView.setBackgroundResource(R.color.red);
            resultAdvice.setTextColor(getColor(R.color.colorTextTV));
            result.setTextColor(getColor(R.color.colorTextTV));
            lastText.setTextColor(getColor(R.color.colorTextTV));
        }

    }

    public void checkAgain(View view) {
        startActivity(new Intent(this,Disclaimer.class));
        finish();
    }

    public void setReminder(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void onCrossClick(View view) {
        finish();
    }
}