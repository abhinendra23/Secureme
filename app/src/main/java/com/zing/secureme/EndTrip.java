package com.zing.secureme;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.zing.secureme.Model.History;
import com.zing.secureme.Model.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EndTrip extends AppCompatActivity {

    private TextView tripDurationTextView;
    private TextView tripNumberDevicesTextView;
    private TextView tripPlacesVisitedTextView;
    private TextView tripScoreTextView;

    private String username;
    private String duration;
    private String numPlaces;
    private String numDevices;
    private String score;
    FirebaseAuth auth;
    FirebaseUser user;

    Person person;
    History history;

    HashMap<String, History> myHistory;
    int id=0;
    String historyId;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_trip);
        bindView();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        username = getIntent().getStringExtra(StartingActivity.USERNAME);
        duration = getIntent().getStringExtra(MapsActivityCurrentPlace.DURATION);
        numDevices = getIntent().getStringExtra(MapsActivityCurrentPlace.NUMDEVICES);
        numPlaces = getIntent().getStringExtra(MapsActivityCurrentPlace.NUMPLACES);
        score = getIntent().getStringExtra(MapsActivityCurrentPlace.SCORE);
        setTextView();

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                person = snapshot.getValue(Person.class);
//                myHistory = person != null ? person.getMyPost() : null;
//                id = myHistory.size();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//
//        history = new History("delhi",duration,numDevices,score);
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//
//
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        historyId = String.valueOf(id);
//        hashMap.put("/Users/" + user.getUid() + "/myHistory/" + historyId + "_history", history);
//
//        firebaseDatabase.getReference().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @SuppressLint("ShowToast")
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//
//                    Toast.makeText(getApplicationContext(),"History Added",Toast.LENGTH_LONG);
//                }
//            }
//        });


    }
    public void setTextView()
    {
        tripDurationTextView.setText(duration);
        tripNumberDevicesTextView.setText(numDevices);
        tripPlacesVisitedTextView.setText(numPlaces);
        tripScoreTextView.setText(score);
    }

    public void bindView()
    {
        tripDurationTextView = findViewById(R.id.end_trip_duration_value_textView);
        tripNumberDevicesTextView = findViewById(R.id.end_trip_number_value_textView);
        tripPlacesVisitedTextView = findViewById(R.id.end_trip_places_value_textView);
        tripScoreTextView = findViewById(R.id.end_trip_score_value_textView);
    }


    /**
     * On click of home button
     * @param view
     */
    public void onHomeButtonClick(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(StartingActivity.USERNAME, username);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(StartingActivity.USERNAME, username);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
