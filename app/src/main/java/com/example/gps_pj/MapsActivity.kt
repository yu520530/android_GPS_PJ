package com.example.gps_pj

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat

import android.location.LocationListener
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var orilocation : Location? = null
    val TAG = "MapsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
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

        locationManager()
        //顯示裝置的位子 小藍點
        mMap.isMyLocationEnabled = true

    }
    //標記位置
    fun  drawMarker()
    {
        var lntLng = LatLng(orilocation!!.latitude, orilocation!!.longitude)
        mMap.addMarker(MarkerOptions().position(lntLng).title("I'm here!"))
        Toast.makeText(this, "改變位置", Toast.LENGTH_LONG).show()
    }

    private fun locationManager()
    {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        var isNETWORKEnable = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!(isNETWORKEnable || isGPSEnable))
        {
            Toast.makeText(this, "並未開啟任何定位服務", Toast.LENGTH_SHORT).show()
        }
        else
        {
            try {
                if(isGPSEnable)
                {
                    //註冊 LocationManager 要向哪個服務取得位置更新資訊
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000L,20f,locationListener)
                    //取得上一次的定位
                    orilocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                else if(isNETWORKEnable)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000L,20f,locationListener)
                    orilocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
            } catch (ex:SecurityException)
            {
                Log.d(TAG,ex.message.toString())
            }
            if(orilocation != null) {
                drawMarker()
            }
        }
    }
    //listener locationChange
    private val locationListener: LocationListener = object : LocationListener
    {
        override fun onLocationChanged(location: Location) {
            if(orilocation == null)
            {
                orilocation = location
                drawMarker()
            }
            else
            {
                orilocation = location
                drawMarker()
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 12.0f))
        }
    }
}