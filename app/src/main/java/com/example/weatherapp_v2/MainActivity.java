package com.example.weatherapp_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;


import com.example.weatherapp_v2.Adapter.ViewPagerAdapter;
import com.example.weatherapp_v2.Common.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CoordinatorLayout coordinatorLayout;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.root_view);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Request permission
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted())
                        {
                           buildLocationRequest();
                           buildLocationCallBack();
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                           fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Snackbar.make(coordinatorLayout,"Permission Denied",Snackbar.LENGTH_LONG)
                                .show();
                    }
                }).check();
    }

   private void buildLocationCallBack() {
        locationCallback = new LocationCallback(){


            @SuppressLint("WrongViewCast")
            @Override
            public void onLocationResult (LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Common.current_location = locationResult.getLastLocation();

                viewPager= (ViewPager)findViewById(R.id.view_pager);
                setupViewPager(viewPager);
                tabLayout = (TabLayout)findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);

                // Log
                Log.d("Location",locationResult.getLastLocation().getLatitude()+"/"+locationResult.getLastLocation().getLongitude());
            }
        };
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter((getSupportFragmentManager()));
                adapter.addFragment(TodayWeatherFragment.getInstance(),"Today");
                adapter.addFragment(ForecastFragment.getInstance(),"5 DAY FORECAST");
                viewPager.setAdapter(adapter);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);

    }
}
