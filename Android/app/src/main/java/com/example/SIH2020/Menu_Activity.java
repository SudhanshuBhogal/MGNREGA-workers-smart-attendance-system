package com.example.SIH2020;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Menu_Activity extends AppCompatActivity {
    Button b1,b2,b3,b4;
    ArrayList<Worker> Absent_worker=new ArrayList<Worker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_);
        b1=findViewById(R.id.b1);
        //b2=findViewById(R.id.b2);
        b3=findViewById(R.id.b3);
        b4=findViewById(R.id.b4);

        //Here we have to add name and mob no of absent workers
        Absent_worker.add(new Worker("ABC",Long.parseLong("1111111111")));
        Absent_worker.add(new Worker("XYZ",Long.parseLong("1111111111")));

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
                sendMessageAbsent();
            }
    });


    }
    public void sendMessageAbsent()
    {
        SmsManager smgr = SmsManager.getDefault();
        String message;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());
        for(Worker x: Absent_worker){
            message =  "Dear "+x.name+" Your attendance for date "+currentDateandTime+" is marked absent.";
            smgr.sendTextMessage(x.mobNo + "",null,message,null,null);
        }
        Toast.makeText(this, "Message sent to absent", Toast.LENGTH_LONG).show();

    }
}
