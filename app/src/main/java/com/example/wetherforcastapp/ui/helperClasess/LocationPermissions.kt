package com.example.wetherforcastapp.ui.helperClasess

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.wetherforcastapp.ui.home.view.HomeFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationPermissions(
    private val fragment: Fragment,
    private val locationCallback: LocationResultListener
) {

    init {
        checkLocationPermissions()
    }

     fun checkLocationPermissions() {

        if (hasLocationPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation()
            } else {
                promptEnableLocation()
            }
        } else {
            requestLocationPermissions()
        }
    }

    val REQUEST_LOCATION_CODE = 100

    @SuppressLint("MissingPermission")
     fun getFreshLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
        val locationRequest = LocationRequest.Builder(1000)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val location = locationResult.lastLocation
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.i("Tag", "Location obtained: Lat=$latitude, Lon=$longitude")

                    // Send the location to the fragment using the callback
                    this@LocationPermissions.locationCallback.onLocationReceived(latitude, longitude)

                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun enableLocationServices() {
        Toast.makeText(fragment.requireActivity(), "Turn on location services", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        fragment.requireActivity().startActivity(intent)
    }

     fun isLocationEnabled(): Boolean {
        val locationManager = fragment.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    fun promptEnableLocation() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(fragment.requireActivity())
        builder.setTitle("Enable Location Services")
        builder.setMessage("Location services are required for this app to function. Please enable location services.")
        builder.setPositiveButton("Turn On") { _, _ ->
            enableLocationServices()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(fragment.requireActivity(), "Location services are required for this feature", Toast.LENGTH_SHORT).show()
        }
        builder.create().show()
    }
    private fun requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation dialog here
            val builder = androidx.appcompat.app.AlertDialog.Builder(fragment.requireActivity())
            builder.setTitle("Location Permission Needed")
            builder.setMessage("This app needs location permission to function properly.")
            builder.setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(fragment.requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(fragment.requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(fragment.requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(fragment.requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}

