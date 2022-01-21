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
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.io.*
import java.lang.Exception
import java.util.concurrent.TimeUnit
import java.io.PrintStream




    //    class MainActivity : AppCompatActivity(),LocationListener {

    class MainActivity: AppCompatActivity() {

        private lateinit var _binding : ActivityMainBinding
        val binding get() = _binding

        private lateinit var sharedPreferencesData: SharedPreferences

        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        private lateinit var locationRequest: LocationRequest



        private var locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                    for(currentLocations : Location in locationResult.locations){
                        binding.tvLatitude.text = "Latitude : ${currentLocations.latitude.toString()}"
                        binding.tvLongitude.text = "Longitude : ${currentLocations.longitude.toString()}"
                        fileOutput("Latitude : ${currentLocations.latitude} , Longitude: ${currentLocations.longitude}")
                        Log.d("updated locations","Altitude is ${currentLocations.altitude}")
                    }
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.MINUTES.toMillis(5)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        requestPermissions()
        subscribeLocation()



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
    }


        private fun fileOutput(locations: String){
            val file = File(this@MainActivity.filesDir, "text1")
            if(!file.exists()){
                file.mkdir()
            }
            try{
                val locationFile = File(file, "Locations")
                val writer = FileWriter(locationFile)
                writer.append(locations)
                writer.flush()
                writer.close()
//                val fileInput  = FileOutputStream(file)
//                val printStream = PrintStream(fileInput)
//                printStream.print(locations+"\n")
//                fileInput.close()


                Toast.makeText(this,"File Saved Successfully",Toast.LENGTH_SHORT).show()
            } catch( e: Exception){
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
        }

        override fun onStop() {
            super.onStop()
            stopLocationUpdate()
        }

        private fun stopLocationUpdate(){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }

        private fun subscribeLocation(){
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
                        subscribeLocation()
                    }
                }
            }
        }

    }