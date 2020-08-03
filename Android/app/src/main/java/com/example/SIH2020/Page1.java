package com.example.SIH2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.vision.face.FaceDetector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class Page1 extends AppCompatActivity {

    Button pic, pic1, view;
    int flag = 0;
    private static final int CAMERA_REQUEST = 1888;
    ImageView imageView,i2;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    TextView display;
    double lat = 0, lon = 0, str_lat = 0, str_log = 0;
    String add = "", name = "", cont = "",name_entered="";
    int PERMISSION_ID = 44;

    private StorageReference sr;

    double min = Double.MAX_VALUE;
    DatabaseReference reff_pro;
    private FirebaseAuth mAuth;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    final Context context = this;

    // options for model interpreter
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    // tflite graph
    private Interpreter tflite;

    private ByteBuffer imgData = null;

    public float[][] embeddings = new float[1][512];

    // input image dimensions for the Inception Model
    private int DIM_IMG_SIZE_X = 160;
    private int DIM_IMG_SIZE_Y = 160;
    private int DIM_PIXEL_SIZE = 3;
    RelativeLayout relativelayout;

    // int array to hold image data
    private int[] intValues;

    FusedLocationProviderClient mFusedLocationClient;
    FaceDetector faceDetector;

    private static final int REQUEST_LOCATION = 1;
    Button btnGetLocation;
    LocationManager locationManager;
    String latitude, longitude;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    //Mobile number has to be changed
    long mobNo;
    String person="";

    String city = "Error";
    String postalCode = "Error";
    String address = "Error";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1);
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        // initialize array that holds image data
        intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
        faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();

        try{
            tflite = new Interpreter(loadModelFile(), tfliteOptions);
//            labelList = loadLabelList();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        imgData = ByteBuffer.allocateDirect(
                4 * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);

        imgData.order(ByteOrder.nativeOrder());
        pic = findViewById(R.id.pic);
        pic1 = findViewById(R.id.pic1);
        display = findViewById(R.id.display);
        sr = FirebaseStorage.getInstance().getReference();
        imageView = findViewById(R.id.imageView1);


        reff_pro = FirebaseDatabase.getInstance().getReference("Professional");

        mAuth = FirebaseAuth.getInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                //getLastLocation();

                reff_pro.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int flag = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Type proInfo = data.getValue(Type.class);
//                            if (email.equals(proInfo.getEmail())) {
//                                flag = 1;
//                                break;
//                            }
                            double la = proInfo.getLatitude();
                            double ld = proInfo.getLongitude();

                            double res = distance(lat, lon, la, ld);
                            if (min > res) {
                                min = res;
                                str_lat = la;
                                str_log = ld;
                                name = proInfo.getName();
                                cont = proInfo.getContact();
                                //add=proInfo.getAddress();
                            }
                        }
                        Geocoder g = new Geocoder(getApplicationContext(), Locale.getDefault());
                        String mycity = "";
                        try {
                            List<Address> addresses = g.getFromLocation(str_lat, str_log, 1);
                            add = addresses.get(0).getAddressLine(0);
                            mycity = addresses.get(0).getLocality();
                            //Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), Menu_Activity.class);
                startActivity(i);
                finish();

            }
        });



            }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            double minn = 100.0;
            //String person="";
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Frame frame = new Frame.Builder().setBitmap(photo).build();
            SparseArray<Face> faces = faceDetector.detect(frame);
            Face thisFace = faces.valueAt(0);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = thisFace.getWidth();
            float y2 = thisFace.getHeight();
            Bitmap face=Bitmap.createBitmap(photo,(int)(x1+1),(int)(y1+1),(int)(x2+1),(int)(y2+1));
            // resize the bitmap to the required input size to the CNN
            Bitmap bitmap = getResizedBitmap(face, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);
            imageView.setImageBitmap(bitmap);

            // convert bitmap to byte array
            convertBitmapToByteBuffer(bitmap);
            Log.d("length", "Bit Value: " + Float.toString(imgData.get(128)));
            tflite.run(imgData, embeddings);
            Log.d("length", "Value: " + Float.toString(embeddings[0][48]));
            String json = loadJSONFromAsset();
            int length=0;
            try {
                JSONObject obj = new JSONObject(json);
                JSONArray arr = obj.names();
                for(int i=0;i<arr.length();i++){
                    String name = arr.getString(i);
                    JSONArray multiarr = obj.getJSONArray(name);
                    JSONArray a = (JSONArray) multiarr.get(0);
                    double[] embeds = new double[a.length()];
                    for(int index=0;index<a.length();index++){
                        embeds[index] =  (double)a.get(index);
                    }
                    float dist =0f;

                    for(int j=0;j<embeddings[0].length;j++){
                        dist+= Math.pow((embeds[j]-embeddings[0][j]),2);
                    }
                    double d = Math.sqrt(dist);
                    if(d<minn){
                        minn = d;
                        person = name;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Comparison has to be done on Distance
           // Toast.makeText(this, "Name: " + person + " distance: " + minn, Toast.LENGTH_SHORT).show();
            Log.d("Person", "Name: " + person);

            androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(Page1.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Is Worker's Name:"+person+"?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i=new Intent(getApplicationContext(), Page1.class);
                            dialog.dismiss();
                            startActivity(i);
                            finish();
                            Log.d("Attendance Yes","Inside yes");
                            flag = 1;
                        }

                    });
//            if(flag == 1){
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://mgnregaaa.herokuapp.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Log.d("Attendance marking","Attendance initiated");
                int flag1 = 0;
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                    Log.d("Location permission","Location permission NOT granted");
                } else {
                    getLocation();
                    getAddress();
                    flag1 = 1;
                    Log.d("Location permission","Location permission granted");
                }
                if(flag1 == 1){
                    markAttendance(person,latitude,longitude);
                    Log.d("Attendance marking","Attendance done");
                    Toast.makeText(this,"Attendance Marked for " + person,Toast.LENGTH_SHORT).show();
                    flag = 0;
                }
                else{
                    Log.d("Location","Else kai andar");

                }

//            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i=new Intent(getApplicationContext(), Page1.class);
                            flag = -1;
                            dialog.dismiss();
                            startActivity(i);
                            finish();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            if(flag == 1){
                Toast.makeText(this,"Please try again.......attendance not marked",Toast.LENGTH_SHORT).show();
                flag = 0;
            }



           /* if (person.equals(name_entered)) {
                Toast.makeText(this, "Name: " + person + " distance: " + minn, Toast.LENGTH_SHORT).show();
                Log.d("Person", "Name: " + person);

                androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(page1.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Attendence Marked");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
            else {
                //Toast.makeText(this, "Login Unsuccessful" + " Name: " + name_entered, Toast.LENGTH_SHORT).show();

                androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(page1.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Invalid Picture");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Try Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }*/


        }
    }

    private void OnGPS() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                Page1.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                Page1.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                //showLocation.setText("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAddress(){
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            postalCode = addresses.get(0).getPostalCode();
            Log.d("Address","address: " + address);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void markAttendance(String name,String latitude,String longitude) {
        Log.d("Location verification","Latitude: " + latitude + " Longitude: " + longitude);
        //AttendanceMark attendanceMark = new AttendanceMark(name);
        Map<String, String> fields = new HashMap<>();
        fields.put("name", name);
        fields.put("latitude", latitude);
        fields.put("longitude", longitude);
        fields.put("address",address);
        fields.put("city",city);
        fields.put("postalCode",postalCode);
        Call<AttendanceMark> call = jsonPlaceHolderApi.markAttendance(fields);
        call.enqueue(new Callback<AttendanceMark>() {
            @Override
            public void onResponse(Call<AttendanceMark> call, Response<AttendanceMark> response) {
                if (!response.isSuccessful()) {
                    Log.d("API error",response.toString());
                    return;
                }
                AttendanceMark postResponse = response.body(); // Change karna pad sakta hai
                Log.d("API response",postResponse.getcontactNumber());
                mobNo = Long.parseLong(postResponse.getcontactNumber());
                checkForPermission(mobNo);
                Log.d("API response variable",new Long(mobNo).toString());
                Log.d("API successful",response.toString());
            }
            @Override
            public void onFailure(Call<AttendanceMark> call, Throwable t) {
                Log.d("API failure","API has failed");
            }
        });
    }

//    @SuppressLint("MissingPermission")
//    private void getLastLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                mFusedLocationClient.getLastLocation().addOnCompleteListener(
//                        new OnCompleteListener<Location>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Location> task) {
//                                Location location = task.getResult();
//                                if (location == null) {
//                                    requestNewLocationData();
//                                } else {
//                                    //lat.setText("Latitude:-"+location.getLatitude()+"");
//                                    //lon.setText("Longitude:-"+location.getLongitude()+"");
//                                    lat = location.getLatitude();
//                                    lon = location.getLongitude();
//                                }
//                            }
//                        }
//                );
//            } else {
//                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        } else {
//            requestPermissions();
//        }
//    }
//
//
//    @SuppressLint("MissingPermission")
//    private void requestNewLocationData() {
//
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(0);
//        mLocationRequest.setFastestInterval(0);
//        mLocationRequest.setNumUpdates(1);
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mFusedLocationClient.requestLocationUpdates(
//                mLocationRequest, mLocationCallback,
//                Looper.myLooper()
//        );
//
//    }
//
//    private LocationCallback mLocationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            Location mLastLocation = locationResult.getLastLocation();
//            lat = mLastLocation.getLatitude();
//            lon = mLastLocation.getLongitude();
//        }
//    };
//
//    private boolean checkPermissions() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        return false;
//    }
//
//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(
//                this,
//                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                PERMISSION_ID
//        );
//    }
//
//    private boolean isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//                LocationManager.NETWORK_PROVIDER
//        );
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (checkPermissions()) {
//            getLastLocation();
//        }
//
//    }

    ////////////////////////////////////////////////////////

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("facenet.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // resizes bitmap to given dimensions
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    // converts bitmap to byte array which is passed in the tflite graph
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // loop through all pixels
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                // get rgb values from intValues where each int holds the rgb values for a pixel.
                imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);

            }
        }
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("embedding.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    public void checkForPermission(Long mobNo){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) { }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        else{
            sendMessage(mobNo);
        }

    }

    public void sendMessage(long mobileNo){
        SmsManager smgr = SmsManager.getDefault();
        String message;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());
        message = "Dear "+person+" Your attendance for date "+currentDateandTime+" is marked successfully.";
        smgr.sendTextMessage(mobileNo + "",null,message,null,null);

    }


}
