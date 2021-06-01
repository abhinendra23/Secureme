package com.zing.secureme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zing.secureme.Model.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText registeremail,registerpassword,position,registerName;
    Button registerButton;
    private FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#8151D8")));
        setContentView(R.layout.activity_register);
        registeremail = findViewById(R.id.registerEmail);
        registerpassword = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);
        registerName = findViewById(R.id.registerName);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registeremail.getText().toString();
                String password = registerpassword.getText().toString();
                email = email.trim();
                if(email.equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Email should be valid", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6)
                {
                    Toast.makeText(RegisterActivity.this, "Password should be atleast 6 character long", Toast.LENGTH_SHORT).show();
                }
                else {
                    //customDialogBox.show();
                    register(email, password);
                }

            }
        });

    }
    public void register(final String email, String password){
        Log.i("email","working1");
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = auth.getCurrentUser();
                            Log.i("email","working3");

                            // Sign in success, update UI with the signed-in user's information
                            if (user != null) {

                                Person person = new Person();
                                person.setEmail(email);
                                person.setUserId(user.getUid());
                                person.setName(registerName.getText().toString());
                                person.setMyPost(null);
                                firebaseDatabase.getReference("Users").child(user.getUid()).setValue(person);

                                Toast.makeText(RegisterActivity.this,"Registered Successully",Toast.LENGTH_SHORT);
                                // this section is Email Verfication .
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                                finish();

                            } else {
                                Toast.makeText(RegisterActivity.this,"Error",Toast.LENGTH_SHORT);
                            }


                        }
                    }
                });
    }
    public void LoginClick(View view)
    {
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
        finish();
    }
}