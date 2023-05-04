package com.runora_dev.runoraf.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.runora_dev.runoraf.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;



public class MapBoxActivity extends AppCompatActivity implements LocationListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback, PermissionsListener {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://runora-app-ceeee-default-rtdb.firebaseio.com/");
    private static final int PermissionCode = 58;


    LocationManager locationManager;
    TextView HomePageDate;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;

    TelephonyManager telephonyManager;

    FusedLocationProviderClient fusionprovider;
    LocationRequest locationRequest;
    Location start_location, end_location, curr_location;

    TextView distance_counter;
    TextView calories_counter;
    TextView SpdInmph;
    TextView SpdInkmh;
    TextView CountDownTimerView;
    Button play_button, pause_button, stop_btn, homebutton;
    ImageView overlayscreen;

    Chronometer timer;
    CountDownTimer countDownTimer;
    String Distance , Calories, SpeedKm, SpeedIn;
    static final long StartTime = 11000;
    long TimeLeft = StartTime;

    boolean active;
    long update;
    double distance = 0;
    double current_speed;

    private static final int AccessCode = 48;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.home);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        HomePageDate = findViewById(R.id.HomePageDate);
        distance_counter = findViewById(R.id.distance_counter);
        calories_counter = findViewById(R.id.calories_counter);
        play_button = findViewById(R.id.play_button);
        pause_button = findViewById(R.id.pause_button);
        stop_btn = findViewById(R.id.stop_btn);
        SpdInmph = findViewById(R.id.spdInmph);
        SpdInkmh = findViewById(R.id.speedInkmh);
        timer = findViewById(R.id.timer);
        overlayscreen = findViewById(R.id.overlayScreen);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusionprovider = LocationServices.getFusedLocationProviderClient(MapBoxActivity.this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        Distance = distance_counter.getText().toString();
        Calories = calories_counter.getText().toString();
        SpeedKm = SpdInkmh.getText().toString();
        SpeedIn = SpdInmph.getText().toString();
        ToggleTheme();
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - timer.getBase()) >= 86400000) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                }
            }
        });
        CalenderDate();
        FloatingActionButton account =  (FloatingActionButton) findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapBoxActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        play_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ResumeRunnable resumeRunnable = new ResumeRunnable();
                new Thread(resumeRunnable).start();
            }

            class ResumeRunnable implements Runnable {
                @Override
                public void run() {
                    play_button.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!active) {
                                if (ContextCompat.checkSelfPermission(MapBoxActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(MapBoxActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                                        && ContextCompat.checkSelfPermission(MapBoxActivity.this,
                                        Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, MapBoxActivity.this);
                                    createLocationRequest();
                                    timer.setBase(SystemClock.elapsedRealtime() - update);
                                    timer.start();
                                    play_button.setVisibility(View.GONE);
                                    pause_button.setVisibility(View.VISIBLE);
                                    active = true;
                                } else {
                                    RequestPermissions();
                                    {
                                    }
                                    play_button.setVisibility(View.VISIBLE);
                                    active = false;
                                }
                            }
                        }
                    });
                    pause_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PauseRunnable pauseRunnable = new PauseRunnable();
                            new Thread(pauseRunnable).start();
                        }

                        class PauseRunnable implements Runnable {
                            @Override
                            public void run() {
                                pause_button.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (active) {
                                            timer.stop();
                                            active = false;
                                            play_button.setVisibility(View.VISIBLE);
                                            update = SystemClock.elapsedRealtime() - timer.getBase();
                                            locationManager.removeUpdates(MapBoxActivity.this);

                                        }
                                    }
                                });
                            }
                        }
                    });


                    stop_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StopRunnable stopRunnable = new StopRunnable();
                            new Thread(stopRunnable).start();


                        }

                        class StopRunnable implements Runnable {
                            @Override
                            public void run() {
                                stop_btn.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //   SaveData();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("users").child("502093655").child("Distance").setValue(Distance);
                databaseReference.child("users").child("PhoneNumber").child("Calories").setValue(Calories);
                databaseReference.child("users").child("PhoneNumber").child("SpdInkmh").setValue(SpdInkmh);
                databaseReference.child("users").child("PhoneNumber").child("SpdInmph").setValue(SpdInmph);
            }
        });
    }
    private void RequestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MapBoxActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Required")
                    .setMessage("Please allow permission access to proceed.")
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                ActivityCompat.requestPermissions(MapBoxActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                                Manifest.permission.READ_PHONE_STATE}, AccessCode);
                            }
                            ActivityCompat.requestPermissions(MapBoxActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.READ_PHONE_STATE}, AccessCode);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                Manifest.permission.READ_PHONE_STATE}, AccessCode);
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE}, AccessCode);
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void LocationPermissionRequest() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MapBoxActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Required")
                    .setMessage("Please allow permission access to proceed.")
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                ActivityCompat.requestPermissions(MapBoxActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                                Manifest.permission.READ_PHONE_STATE}, PermissionCode);
                            }
                            ActivityCompat.requestPermissions(MapBoxActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.READ_PHONE_STATE}, PermissionCode);
                        }

                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                Manifest.permission.READ_PHONE_STATE}, PermissionCode);
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE}, PermissionCode);
        }
    }


    public void LocationAlert() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
            dialogbuilder.setMessage(" Enable GPS To Continue")
                    .setPositiveButton("Turn location on", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent call_gps_settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(call_gps_settings);


                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog alertDialog = dialogbuilder.create();
            alertDialog.show();

        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }


    private void startRun_interface() {
        Intent intent = new Intent(MapBoxActivity.this, RunInterface.class);
        startActivity(intent);

    }


    @Override
    public void onProviderDisabled(final String provider) {

    }

    public void CalenderDate() {
        Calendar calendar = Calendar.getInstance();
        HomePageDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapBoxActivity.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.OUTDOORS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });

    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private void distanceInMiles() {
        String miles = "mi";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString spannableString = new SpannableString(miles);
        spannableString.setSpan(new RelativeSizeSpan(0.50f), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        distance = distance + (start_location.distanceTo(end_location) * 0.00062137);
        start_location = end_location;
        builder.append(new DecimalFormat("0.00 ").format(distance));
        builder.append(spannableString);
        distance_counter.setText(builder);


    }

    private void distanceInkilometers() {
        String kilometers = "km";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString spannableString = new SpannableString(kilometers);
        spannableString.setSpan(new RelativeSizeSpan(0.50f), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        distance = distance + (start_location.distanceTo(end_location) / 1000);
        start_location = end_location;
        builder.append(new DecimalFormat("0.00 ").format(distance));
        builder.append(spannableString);
        distance_counter.setText(builder);

    }

    private void ToggleTheme() {
        final SharedPreferences LastSelectedItem = getApplicationContext().getSharedPreferences("PriorSelected", Context.MODE_PRIVATE);
        int LastSelection = LastSelectedItem.getInt("LastSelection", 0);
        if (LastSelection == 1) {
            LightTheme();

        }
    }

    private void LightTheme() {
        overlayscreen.setBackgroundResource(R.drawable.light_theme);
        homebutton.setBackgroundResource(R.drawable.light_theme_buttons);
        play_button.setBackgroundResource(R.drawable.light_theme_buttons);
        pause_button.setBackgroundResource(R.drawable.light_theme_buttons);
        stop_btn.setBackgroundResource(R.drawable.light_theme_buttons);
    }

    private void StartCountDown() {
        countDownTimer = new CountDownTimer(TimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                homebutton.setEnabled(false);
                homebutton.setBackgroundResource(R.drawable.disabled_button_resource);

                play_button.setEnabled(false);
                play_button.setBackgroundResource(R.drawable.disabled_button_resource);

                pause_button.setEnabled(false);
                pause_button.setBackgroundResource(R.drawable.disabled_button_resource);

                stop_btn.setEnabled(false);
                stop_btn.setBackgroundResource(R.drawable.disabled_button_resource);



                TimeLeft = millisUntilFinished;
                CountDownViewUpdate();

            }


            @Override
            public void onFinish() {
                active = false;
                AutoStartActivity();
                CountDownTimerView.setVisibility(View.GONE);
                ToggleTheme();

                homebutton.setEnabled(true);
                homebutton.setBackgroundResource(R.drawable.resume_button);

                play_button.setEnabled(true);
                play_button.setBackgroundResource(R.drawable.resume_button);

                pause_button.setEnabled(true);
                pause_button.setBackgroundResource(R.drawable.resume_button);

                stop_btn.setEnabled(true);
                stop_btn.setBackgroundResource(R.drawable.resume_button);




            }
        }.start();
    }


    private void CountDownViewUpdate() {
        int min = (int) (TimeLeft / 1000) / 60;
        int sec = (int) (TimeLeft / 1000) % 60;

        String TimeLeftFormat = String.format(Locale.getDefault(), "%02d:%02d", min, sec);

        CountDownTimerView.setText(TimeLeftFormat);

    }

    private void AutoStartActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            play_button.performContextClick();
        }
        View playButton = findViewById(R.id.play_button);
        playButton.performClick();

    }
    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}




