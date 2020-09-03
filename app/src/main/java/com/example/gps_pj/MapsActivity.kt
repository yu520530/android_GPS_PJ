package com.example.gps_pj

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var orilocation : Location? = null
    val TAG = "MapsActivity"
    private  val REQUEST_PERMISSIONS = 1
    val rectOptions: PolylineOptions = PolylineOptions()
    var bf:Location? = null
    var tot:Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSIONS)
        else{
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
        if(bf != null){
            tot = tot + distance(LatLng(bf!!.latitude, bf!!.longitude),lntLng)
            Toast.makeText(this, "總共移動 %s 公尺".format(tot.toString()), Toast.LENGTH_LONG).show()

        }

        bf = orilocation
        mMap.addMarker(MarkerOptions().position(lntLng).title(getNowTimeDetail()).icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_marker)))
        rectOptions.add(lntLng)
        mMap.addPolyline(rectOptions)
        //Toast.makeText(this, "改變位置", Toast.LENGTH_LONG).show()
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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000L,30f,locationListener)
                    //取得上一次的定位
                    orilocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                else if(isNETWORKEnable)
                {
                    //5000 10
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000L,30f,locationListener)
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 12.0f))
            }
            else
            {
                orilocation = location
                drawMarker()
            }

        }
    }
    //取得時間
    fun getNowTimeDetail(): String? {
        val sdf = SimpleDateFormat("HH時mm分ss秒")
        return sdf.format(Date())
    }
    //取得距離
    fun distance(a:LatLng,b:LatLng): Double
    {
        var ans  = (((a.latitude - b.latitude)*110.574).pow(2) + ((a.longitude - b.longitude)*111.320).pow(2)).pow(0.5)
        return ans

    }
    //同意使用再app上使用gps
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isEmpty()) return
        when(requestCode){
            REQUEST_PERMISSIONS -> {
                for (result in grantResults)
                    if(result != PackageManager.PERMISSION_GRANTED)
//                        finish()
                    else {
                        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

                        map.getMapAsync(this)
                    }
            }
        }
    }
}