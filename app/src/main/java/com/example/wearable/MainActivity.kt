    package com.example.wearable

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.wearable.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import java.lang.Exception
import java.util.concurrent.TimeUnit

    //    class MainActivity : AppCompatActivity(),LocationListener {

    class MainActivity: AppCompatActivity() {

        private lateinit var _binding : ActivityMainBinding
        val binding get() = _binding

        private lateinit var sharedPreferencesData: SharedPreferences

        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        private lateinit var locationRequest: LocationRequest

        private lateinit var locationCallback: LocationCallback

        private var currentLocation : Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation
            }
        }

        sharedPreferencesData = getSharedPreferences("MyPref", MODE_PRIVATE)
        val editor = sharedPreferencesData.edit()

        binding.btLogout.setOnClickListener {
            editor.putString("name", "")
            editor.putString("number", "")
            editor.commit()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.btAccess.setOnClickListener {
            requestPermissions()
        }

        binding.btLocation.setOnClickListener {
//                subscribeLocation()
                lastLocation()
        }

    }

        fun lastLocation(){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions()
            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                binding.tvLongitude.text = it.longitude.toString()
                binding.tvLatitude.text = it.latitude.toString()
            }
        }

        fun subscribeLocation(){
            Log.d("Main Activity","Subscribed to location updates")
            try{
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper())
            } catch (e: Exception){
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }

        }





        private fun externalStorage() = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        private fun locationForeground() = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        private fun locationBackground() = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED


        private fun requestPermissions(){
            var permissionToRequest = mutableListOf<String>()
            if(!externalStorage()){
                permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if(!locationForeground()){
                permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if(!locationBackground() && locationForeground()){
                permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if(permissionToRequest.isNotEmpty()){
                ActivityCompat.requestPermissions(this,
                    permissionToRequest.toTypedArray(),0)
            }

        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if(requestCode == 0 && grantResults.isNotEmpty()){
                for(i in grantResults.indices){
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        Log.d("Permissions","${permissions[i]} is granted")
                    }
                }
            }
        }



    }