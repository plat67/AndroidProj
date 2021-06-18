package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.study.hanselandphotograph.DBHelper.MyDBHelper
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.databinding.ActivityShowStoryBinding
import com.android.study.hanselandphotograph.model.Location
import com.android.study.hanselandphotograph.model.Picture
import com.android.study.hanselandphotograph.model.Story
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.time.LocalDate

class ShowStoryActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnPolylineClickListener {
    private lateinit var binding: ActivityShowStoryBinding
    private lateinit var name: String
    private lateinit var date: String
    private lateinit var comment: String
    private lateinit var route: ArrayList<Location>
    private lateinit var picture: ArrayList<Picture>
    private lateinit var map: GoogleMap

    private lateinit var dbHelper: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initToolbar()
        init()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        binding.toolbar.title = "스토리 입력"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    private fun init() {
        dbHelper = MyDBHelper(this)

        Log.i("story", "2")
        val intent = intent
        val story = intent.getSerializableExtra("story") as Story
        name = story.name
        date = story.date
        comment = story.comment
        route = dbHelper.getLocation(story.id)
        picture = dbHelper.getPicture(story.id)

        binding.apply {
            binding.toolbar.title = name
            showDate.text = date.toString()
            showComment.text = comment
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.showStoryMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("story", "1")
        map = googleMap
        if (route.size != 0) {
            val polyLineOptions = PolylineOptions()
            polyLineOptions.color(0xffff0000.toInt())
            polyLineOptions.add()
            var averageX = 0.0
            var averageY = 0.0
            for (xy in route) {
                polyLineOptions.add(LatLng(xy.x, xy.y))
                averageX += xy.x
                averageY += xy.y
            }
            map.addPolyline(polyLineOptions)

            Log.i("ShowStoryActivity Image Marker: ", picture[0].toString())
            for (xy in picture) {
                val markerOptions = MarkerOptions()
                markerOptions.position(LatLng(xy.lat, xy.long))
                val cameraIcon = BitmapFactory.decodeResource(resources, R.drawable.camera_icon)
                markerOptions.icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                            cameraIcon,
                            120,
                            120,
                            true
                        )
                    )
                )
                map.addMarker(markerOptions)
                Log.i("ShowStoryActivity Image Marker: (for) ", xy.toString())
            }

            map.setOnMarkerClickListener {
                it.position
                var pTitle = ""
                var pPath = ""
                for (p in picture) {
                    if (it.position == LatLng(p.lat, p.long)) {
                        pTitle = p.title
                        pPath = p.path
                    }
                }
                val intent = Intent(this, ShowImageActivity::class.java)
                intent.putExtra("title", pTitle)
                intent.putExtra("path", pPath)
                startActivity(intent)
                false
            }

            val middleXY = LatLng(averageX / route.size, averageY / route.size)

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(middleXY, 16f))
        }

    }

    override fun onPolylineClick(googleMap: Polyline) {
        // nothing
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent(this@ShowStoryActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}