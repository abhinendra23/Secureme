package com.zing.secureme;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.zing.secureme.Model.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private String username;

    FirebaseAuth auth;
    FirebaseUser user;
    Person person;
    EditText place;
    private TextView welcomeUserTextView;
    LinearLayout historyll,handwashll,selfAssessmentLL,safetyll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        welcomeUserTextView = findViewById(R.id.main_greeting_textview);
        place = findViewById(R.id.place);
        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        historyll = findViewById(R.id.main_stats_button);
        handwashll = findViewById(R.id.handwash);
        safetyll = findViewById(R.id.safety_measures);
        selfAssessmentLL = findViewById(R.id.self_assessment);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                person = new Person();
                person = snapshot.getValue(Person.class);
                if(person!=null) {
                    username = person.getName();
                    welcomeUserTextView.setText("Hey, " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

      historyll.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
           onStatsButtonClick();
          }
      });

      handwashll.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            onHandWashButtonClick();
          }
      });

      safetyll.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              onSafetyButtonClick();
          }
      });

      selfAssessmentLL.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              onSelfAssessButtonClick();
          }
      });
    }

    public void bindView()
    {

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)  {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            Intent intent = new Intent(this, StartingActivity.class);
//            startActivity(intent);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public void onStartTripButtonClick(View view)
    {
        Intent startActivityIntent = new Intent(this, MapsActivityCurrentPlace.class);
        startActivityIntent.putExtra(StartingActivity.USERNAME, username);
        startActivityIntent.putExtra("Place",place.getText().toString());
        startActivity(startActivityIntent);
    }

    public void onStatsButtonClick()
    {
        startActivity(new Intent(this, HistoryActivity.class));
    }
    public void onHandWashButtonClick()
    {
        startActivity(new Intent(this, com.zing.secureme.views.MainActivity.class));
    }

    public void onSafetyButtonClick()
    {
        startActivity(new Intent(this, SafetyMeasures.class));
    }

    public void onSelfAssessButtonClick()
    {
        startActivity(new Intent(this, Disclaimer.class));
    }


}
