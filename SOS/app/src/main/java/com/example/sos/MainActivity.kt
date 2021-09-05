package com.example.sos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var imageSOS : ImageButton
    private lateinit var recycle : RecyclerView
    lateinit var txtAddress : TextView
    private lateinit var adapter: Adapter
    //2
    val request = 111
    private lateinit var readContact : ReadContact
    //3
    val PLACE_PICKER_REQUEST = 123
   // private lateinit var locationOfMap: LocationOfMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest

    //sms
    private lateinit var sms : sms
    // swipe
    private lateinit var layoutSwipe : LinearLayout
    private lateinit var layoutMain : LinearLayout

    private lateinit var imageSwipe : ImageView
    private lateinit var buttonSwipe : ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //swipe
        layoutSwipe = findViewById(R.id.swipe_layout)
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutSwipe)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        layoutMain()
       //swipeSwipe()
       init()
       recycle()
       showImageGif()
       onClick()
       //
        readContact.readContact()
        isPermission()
        var check = true
       imageSwipe.setOnClickListener {
           if(check) {
               saveStatusSwipe = 9000
               bottomSheetBehavior.peekHeight = saveStatusSwipe
               check = false
           } else {
               saveStatusSwipe = 100
               bottomSheetBehavior.peekHeight = saveStatusSwipe
               check = true
           }
       }
       buttonSwipe.setOnClickListener {
           val intent = Intent(this,MainActivity2::class.java)
           startActivity(intent)
       }
    }



    // vì không thể sử dụng swipe khi main activity đang ở dạng linearLayout vì vậy cần khổi tạo
    // một layout mới chứa các đối tượng
    private fun layoutMain() {
        layoutMain = findViewById(R.id.main_layout)
    }
    override fun onResume() {
        super.onResume()
        readContact.readContact()
        requestPermission()
        getLastLocation()
        isPermission()
    }
    private fun init(){
        txtAddress = layoutMain.findViewById(R.id.txtAddress)
        imageSOS = layoutMain.findViewById(R.id.imageSOS)
        recycle = layoutMain.findViewById(R.id.recycle)
        buttonSwipe = layoutSwipe.findViewById(R.id.button1)
        imageSwipe = layoutSwipe.findViewById(R.id.imgSwipe)
        sms = sms()
        //
        readContact = ReadContact(this)
        //locationOfMap = LocationOfMap(this)
        initOfMap()
        //
    }
    private fun recycle(){
        adapter = Adapter(listContact)
        recycle.adapter = adapter
        adapter.notifyDataSetChanged()
        recycle.setHasFixedSize(true)
    }


    private fun showImageGif(){
        Glide.with(this).load(R.drawable.sos).into(imageSOS)
    }
    private fun onClick(){
        imageSOS.setOnClickListener {
          //  locationOfMap.getLastLocation()
            val listNumber = mutableListOf(
                DataContact("Tinh","0914150321"),
                DataContact("Me","0393363274")
            )
            getLastLocation()
            sms.init(txtAddress.text.toString(), listNumber,this)
        }
    }
    private fun isPermission() {
        try {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS),
                PackageManager.PERMISSION_GRANTED
            )
        }
        catch (e : Exception) {
            Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
        }
    }
    //ReadContact
    //todo read contact from provider when made in ReadContact file (fun readContact() ->
    // onRequestPermissionsResult -> queryContact)
    //2 and 3
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //2
        if(requestCode == request && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            readContact.queryListContact()
        }
        //3
        if(requestCode == PLACE_PICKER_REQUEST) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("in onPermissionResult: ","not permission")
            }
        }
    }
    //3
    private fun initOfMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }
    //3
    //check uses permission
    private fun checkPermission() : Boolean {
        if(
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }
    //3
   // get uses permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
            PLACE_PICKER_REQUEST
        )
    }
    //3
    //check Location service pf device enable
    private fun isLocationEnable() : Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
   // 3
    //get last location
   private fun getLastLocation() {
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
                            val address = "Lat: ${location.latitude}, Long: ${location.longitude}\n" +
                                    "Location: ${getLocaleName(location.latitude,location.longitude)}\n"
                            txtAddress.text = address

                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "You ever haven't allow enable your Location service",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                requestPermission()
            }
        }catch (e : Exception) {
            Log.e("in getLastLocation: ",e.toString())
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
        }
    }

   // 3
    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        //
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        //
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
            //create val locationCallback
        )
    }
   // 3
   private val locationCallback =
        object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val lastLocation = p0.lastLocation
                //set new Location
                val address = "Lat: ${lastLocation.latitude}, Long: ${lastLocation.longitude}\n" +
                        "Location: ${getLocaleName(lastLocation.latitude,lastLocation.longitude)}\n"
                txtAddress.text = address
            }
        }
    fun getLocaleName(lat : Double, long : Double) : String{
    var address = ""
         try {
            val geoCoder = Geocoder(this, Locale.getDefault())
            val location = geoCoder.getFromLocation(lat, long, 1)
             address = location[0].getAddressLine(0)

        }catch (e : Exception) {
             Log.e("in localeName: ", e.toString())
             Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
        }
        return address
    }
}