package com.example.location.View

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.location.Model.FirebaseTrain
import com.example.location.Model.Train
import com.example.location.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

class TrainTrackFragment : Fragment() {
    private lateinit var train : Train
    private lateinit var googleMap: GoogleMap
    private val POLYLINE_STROKE_WIDTH_PX = 14
    private lateinit var  fb_train : FirebaseTrain

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var ref: DatabaseReference

    private val callback = OnMapReadyCallback { gMap ->
        googleMap = gMap
        retrieveDataFromDatabase()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        train = getArguments()?.getSerializable("train") as Train
        ref = database.reference.child("trains").child(train.id.toString())
        return inflater.inflate(R.layout.fragment_train_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.train_track_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }



    private fun getMarker(currentInd : Int, total : Int, lastCoordInd : Int): BitmapDescriptor? {
        if(currentInd == lastCoordInd) return getActivity()?.let { BitmapFromVector(it, R.drawable.map_train_active) }
        else if(currentInd == 0) {
            if(currentInd < lastCoordInd) return getActivity()?.let { BitmapFromVector(it, R.drawable.map_start_inactive) }
            else return getActivity()?.let { BitmapFromVector(it, R.drawable.map_start_active) }
        }
        else if(currentInd == total - 1) return getActivity()?.let { BitmapFromVector(it, R.drawable.map_end_active) }
        if(currentInd<lastCoordInd) return getActivity()?.let { BitmapFromVector(it, R.drawable.station_inactive) }
        return getActivity()?.let { BitmapFromVector(it, R.drawable.station) }
    }

    private fun addPolyline(coords : List<LatLng>, color: String, googleMap : GoogleMap){
        val poly_options1 = PolylineOptions()
        poly_options1.addAll(coords)
        poly_options1.color(Color.parseColor(color))
        val polyline1 = googleMap.addPolyline(poly_options1)
        polyline1.endCap = RoundCap()
        polyline1.startCap = RoundCap()
        polyline1.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
        polyline1.isClickable = true
    }


    fun retrieveDataFromDatabase() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fb_train = snapshot.getValue(FirebaseTrain::class.java)!!
                showTrainOnMap()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(),error.toString(),Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun showTrainOnMap(){
        googleMap.clear()

        val coords : ArrayList<LatLng> = arrayListOf()
        for(c in train.coords) coords.add(LatLng(c.latitude, c.longitude))

        val currentPos = fb_train.lastCoordInd
        addPolyline(coords.take(currentPos + 1), "#8094A0", googleMap)
        addPolyline(coords.takeLast(coords.size - currentPos),  "#4285f5", googleMap)

        val update = CameraUpdateFactory.newLatLngZoom(coords[currentPos], 10.0f)
        googleMap.animateCamera(update)

        for (i in 1 until train.route.size - 1) {
            val r = train.route[i]

            if (r.is_stop && currentPos != r.coordInd) {
                val marker = MarkerOptions().position(coords[r.coordInd]).title(r.city)
                marker.icon(getMarker(r.coordInd, coords.size, currentPos))
                marker.snippet("Halt: "+r.halt+" min")
                googleMap.addMarker(marker)
            }
        }

        if(currentPos != 0){
            val src_marker = MarkerOptions().position(coords[0]).title(train.route[0].city)
            src_marker.icon(getMarker(0, coords.size, currentPos))
            src_marker.snippet("Source")
            googleMap.addMarker(src_marker)
        }

        if(currentPos != coords.size-1){
            val dest_marker = MarkerOptions().position(coords.last()).title(train.route.last().city)
            dest_marker.icon(getMarker(coords.size - 1, coords.size, currentPos))
            dest_marker.snippet("Destination")
            googleMap.addMarker(dest_marker)
        }

        val current_marker = MarkerOptions().position(coords[currentPos]).title(train.name)
        current_marker.icon(getMarker(currentPos, coords.size, currentPos))
        current_marker.snippet("Train is here.")
        googleMap.addMarker(current_marker)

        googleMap.setOnPolylineClickListener { it ->
            Toast.makeText(
                activity?.applicationContext,
                "Duration: " + train.total_duration,
                Toast.LENGTH_SHORT
            ).show()
        }
    }



    private fun BitmapFromVector(context: Context, vectorResId:Int): BitmapDescriptor? {
        val vectorDrawable: Drawable = ContextCompat.getDrawable(context,vectorResId)!!
        vectorDrawable.setBounds(0,0,vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight)

        val bitmap: Bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @Suppress("DEPRECATION")
    inline fun <reified T : Serializable> Bundle.customGetSerializable(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, T::class.java)
        } else {
            getSerializable(key) as? T
        }
    }

}