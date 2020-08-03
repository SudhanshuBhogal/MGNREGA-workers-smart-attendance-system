package com.example.SIH2020;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Menu_Activity extends AppCompatActivity {
    Button b1,b2,b3,b4;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    List<Worker> Absent_worker = new ArrayList<Worker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_);
        b1=findViewById(R.id.b1);
        //b2=findViewById(R.id.b2);
        b3=findViewById(R.id.b3);
        b4=findViewById(R.id.b4);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://mgnregaaa.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = "";
                try {
                    FileInputStream fin = openFileInput("xyz");
                    int c;
                    //String temp = "";
                    while ((c = fin.read()) != -1) {
                        temp = temp + Character.toString((char) c);
                    }
                    //text.setText(temp);
                    //Toast.makeText(getBaseContext(), "file read:  "+temp, Toast.LENGTH_SHORT).show();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(temp=="")
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(Menu_Activity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please add a working site");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }
                else
                {
                    Intent i=new Intent(getApplicationContext(), Page1.class);
                    startActivity(i);
                    finish();
                }


            }
        });



        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), Take_Attendance.class);
                startActivity(i);
                finish();

            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getAbsentWorker("2","8","2020");
            }
    });
    }
    public void getAbsentWorker(String day,String month,String year){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("day", day);
        parameters.put("month", month);
        parameters.put("year", year);
        Call<List<Worker>> call = jsonPlaceHolderApi.getAbsentWorker(parameters);

        call.enqueue(new Callback<List<Worker>>() {
            @Override
            public void onResponse(Call<List<Worker>> call, Response<List<Worker>> response) {
                if (!response.isSuccessful()) {
                    Log.d("Absent worker API error",response.toString());
                    return;
                }
                Absent_worker = response.body();
                Log.d("Absent API response",response.body().toString());
                sendMessageAbsent(Absent_worker);
            }
            @Override
            public void onFailure(Call<List<Worker>> call, Throwable t) {
                Log.d("API failure","Absent worker API has failed");
            }
        });

    }
    public void sendMessageAbsent(List<Worker> Absent_worker)
    {
        SmsManager smgr = SmsManager.getDefault();
        String message;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());
        for(Worker x: Absent_worker){
            message =  "Dear "+x.name+" Your attendance for date "+currentDateandTime+" is marked absent.";
            smgr.sendTextMessage(x.contactNumber + "",null,message,null,null);
            Log.d("SMS absent","Name: " + x.name + " ContactNumber: "  + x.contactNumber);

        }
        Toast.makeText(this, "Message sent to absent", Toast.LENGTH_LONG).show();

    }
}
