package com.example.stech20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Signup extends AppCompatActivity {

    EditText name,mobile,username,password,confirm;
    Button signup;
    TextView signtext;

    double lat=0;
    double lon=0;
    String email,pass,n,mob,cpass,type;

    private FirebaseAuth mAuth;

    DatabaseReference normal;
    //DatabaseReference profess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name=findViewById(R.id.name);
        mobile=findViewById(R.id.mob);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        confirm=findViewById(R.id.cpassword);
        signup=findViewById(R.id.signup);
        signtext=findViewById(R.id.signtext);

        mAuth = FirebaseAuth.getInstance();

        normal = FirebaseDatabase.getInstance().getReference("Normal");
        //profess = FirebaseDatabase.getInstance().getReference("Pro");
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=username.getText().toString().trim();
                pass=password.getText().toString().trim();
                n=name.getText().toString().trim();
                cpass=confirm.getText().toString().trim();
                mob=mobile.getText().toString().trim();

                if(n.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Enter username",Toast.LENGTH_LONG).show();
                }
                if(mob.length()!=10)
                {
                    Toast.makeText(getApplicationContext(),"invalid mobile number",Toast.LENGTH_LONG).show();
                }
                if(email.isEmpty())
                {
                    username.setError("Email is Required");
                    username.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    username.setError("Please enter a valid Email");
                    username.requestFocus();
                    return;
                }

                if(pass.isEmpty())
                {
                    password.setError("Password is Required");
                    password.requestFocus();
                    return;
                }

                if(pass.length()<6)
                {
                    password.setError("Minimum length of password shaould be 6");
                    password.requestFocus();
                    return;
                }

                if(!cpass.equals(pass))
                {
                    Toast.makeText(getApplicationContext(),"invalid password",Toast.LENGTH_LONG).show();
                    return;
                }

                registeruser();
            }
        });
        signtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void registeruser(){

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(),"User Registration Successfull"+mAuth.getCurrentUser().getUid(),Toast.LENGTH_LONG).show();

                    String id = mAuth.getCurrentUser().getUid();

                        Type info = new Type(n,mob,email,pass,"Normal");
                        normal.child(id).setValue(info);
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(),"You are Already Registered",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
