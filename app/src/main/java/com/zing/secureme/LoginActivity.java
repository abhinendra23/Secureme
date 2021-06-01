package com.zing.secureme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.steelkiwi.library.SlidingSquareLoaderView;

public class LoginActivity extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    Button loginButton;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    SlidingSquareLoaderView squareLoaderView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference serviceManReference;
    LinearLayout linearLayout,hide_ll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

        loginButton = findViewById(R.id.loginButton);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        squareLoaderView = findViewById(R.id.view);
        linearLayout = findViewById(R.id.login_ll);
        hide_ll = findViewById(R.id.hide_ll);

        firebaseDatabase = FirebaseDatabase.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                squareLoaderView.show();
                hide_ll.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                email = email.trim();
                if(email.equals(""))
                {
                    Toast.makeText(LoginActivity.this, "Email should be valid", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6)
                {
                    Toast.makeText(LoginActivity.this, "Password should be atleast 6 character long", Toast.LENGTH_SHORT).show();
                }
                else {
                    //customDialogBox.show();
                    login(email, password);

                }

            }
        });
    }

    private void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(getApplicationContext(),"Login Successfully",Toast.LENGTH_SHORT);
                            Intent i = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(i);

                            finish();
                        }

                        else {
                            squareLoaderView.hide();
                            hide_ll.setVisibility(View.INVISIBLE);
                            linearLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"You Entered wrong Id or password",Toast.LENGTH_SHORT);

                        }
                    }
                });
    }


    public void RegisterClick(View view)
    {
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        finish();
    }



}