package com.example.sos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*

class LocationOfMap(private val context : MainActivity) {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    var addressName : String = ""
   fun initOfMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
   }
    //check uses permission
    private fun checkPermission() : Boolean {
        if(
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }

    //get uses permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
            context.PLACE_PICKER_REQUEST
            //123
        )
    }
    //check Location service pf device enable
    private fun isLocationEnable() : Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    //
    fun getLastLocation() {
        try {
            //check permission
            if (checkPermission()) {
                //check location service enable
                if (isLocationEnable()) {
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                        val location = task.result
                        if (location == null) {
                            //if location == null then will get the new user's location
                            getNewLocation()
                        } else {
                           addressName =
                                "Lat: ${location.latitude}, Long: ${location.longitude}\n" +
                                        "Location: ${getLocaleName(location.latitude,location.longitude)}\n"
                            context.txtAddress.text = addressName
                            addressName = ""
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "You ever haven't allow enable your Location service",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                requestPermission()
            }
        }catch (e : Exception) {
            Log.e("in getLastLocation: ",e.toString())
            Toast.makeText(context,e.message, Toast.LENGTH_LONG).show()
        }
    }
    private fun getNewLocation() {
        try {
            locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 0
            locationRequest.fastestInterval = 0
            locationRequest.numUpdates = 2
            //
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            //
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
                //create val locationCallback
            )
        }
    catch (e : Exception) {
            Log.e("in getNewLocation: ",e.toString())
            Toast.makeText(context,e.message, Toast.LENGTH_LONG).show()
        }
    }
    private val locationCallback =
        object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val lastLocation = p0.lastLocation
                //set new Location
                addressName = "Lat: ${lastLocation.latitude}, Long: ${lastLocation.longitude}\n" +
                        "Location: ${getLocaleName(lastLocation.latitude,lastLocation.longitude)}\n"
                context.txtAddress.text = addressName
                addressName = ""
            }
        }
    //get location
    fun getLocaleName(lat : Double, long : Double) : String{
        val address : String
        val geoCoder = Geocoder(context, Locale.getDefault())
        val location = geoCoder.getFromLocation(lat, long,1)
        address = location[0].getAddressLine(0)
        return address
    }
}