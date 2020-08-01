package com.example.stech20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Admin extends AppCompatActivity {

    EditText name,mobile,username,password,confirm;
    Button signup;
    TextView signtext;
    String email,pass,n,mob,cpass,type;
    String address="";
    double lat=0,lon=0;

    int PERMISSION_ID = 44;

    FusedLocationProviderClient mFusedLocationClient;

    private FirebaseAuth mAuth;

    DatabaseReference profess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        name=findViewById(R.id.name);
        mobile=findViewById(R.id.mob);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        confirm=findViewById(R.id.cpassword);
        signup=findViewById(R.id.signup);
        signtext=findViewById(R.id.signtext);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

//        Geocoder g=new Geocoder(getApplicationContext(), Locale.getDefault());
//        String mycity="";
//        try{
//            List<Address> addresses= g.getFromLocation(lat,lon,1);
//            address=addresses.get(0).getAddressLine(0);
//            mycity=addresses.get(0).getLocality();
//            //Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }

        mAuth = FirebaseAuth.getInstance();

        profess = FirebaseDatabase.getInstance().getReference("Professional");

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
//                    if(type.equals("Pro"))
//                    {

                    Geocoder g=new Geocoder(getApplicationContext(), Locale.getDefault());
                    String mycity="";
                    try{
                        List<Address> addresses= g.getFromLocation(lat,lon,1);
                        address=addresses.get(0).getAddressLine(0);
                        mycity=addresses.get(0).getLocality();
                        //Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }

                    Type info = new Type(n,mob,email,pass,"Professional",address,lat,lon);
                    profess.child(id).setValue(info);
                    // }
//                    else
//                    {
//                        Type info =a new Type(n,mob,email,pass,type);
//                        normal.child(id).setValue(info);
                    //}
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


    ////////////////////////////////////////////////////////

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
//                                    lat.setText("Latitude:-"+location.getLatitude()+"");
//                                    lon.setText("Longitude:-"+location.getLongitude()+"");
                                    lat=location.getLatitude();
                                    lon=location.getLongitude();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
//            lat.setText("Latitude:-"+mLastLocation.getLatitude()+"");
//            lon.setText("Longitude:-"+mLastLocation.getLongitude()+"");
            lat=mLastLocation.getLatitude();
            lon=mLastLocation.getLongitude();
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }



}
