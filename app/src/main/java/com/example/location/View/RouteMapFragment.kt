package com.example.location.View

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.location.Model.Train
import com.example.location.R
import com.example.location.TrainApplication
import com.example.location.ViewModel.StationViewModel
import com.example.location.ViewModel.StationViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import java.io.Serializable

class RouteMapFragment : Fragment() {
    private lateinit var train : Train
    private val POLYLINE_STROKE_WIDTH_PX = 14
    private lateinit var stationViewModel: StationViewModel

    private val callback = OnMapReadyCallback { googleMap ->
        //val (coords, stationNames, stationCoords) = getCoordinates()

        val coords : ArrayList<LatLng> = arrayListOf()
        for(c in train.coords) coords.add(LatLng(c.latitude, c.longitude))

        val polylineOptions = PolylineOptions()
        polylineOptions.addAll(coords)
        polylineOptions.color(ActivityCompat.getColor(requireActivity(),R.color.light_blue_A200))

        val polyline = googleMap.addPolyline(polylineOptions)
        polyline.endCap = RoundCap()
        polyline.startCap = RoundCap()
        polyline.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
        polyline.isClickable = true

        val update = CameraUpdateFactory.newLatLngZoom(coords[coords.size/2], 10.0f)
        googleMap.animateCamera(update)

        for(i in 0 until train.route.size){
            val r = train.route[i]
            if(r.is_stop){
                val marker = MarkerOptions().position(coords[r.coordInd]).title(r.city)
                marker.icon(getActivity()?.let { BitmapFromVector(it, R.drawable.route_train) })
                marker.snippet("Halt: "+r.halt+" min")

                if(i == 0){
                    marker.icon(getActivity()?.let { BitmapFromVector(it, R.drawable.route_start) })
                    marker.snippet("Source")
                }

                else if(i == train.route.size-1){
                    marker.icon(getActivity()?.let { BitmapFromVector(it, R.drawable.route_end) })
                    marker.snippet("Destination")
                }

                googleMap.addMarker(marker)
            }
        }


        googleMap.setOnPolylineClickListener {it->
            Toast.makeText(activity?.applicationContext,"Duration: "+train.total_duration, Toast.LENGTH_SHORT).show()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        train = getArguments()?.getSerializable("train") as Train
        val viewModelFactory = StationViewModelFactory((requireActivity().application as TrainApplication) .stRepository)
        stationViewModel = ViewModelProvider(this, viewModelFactory).get(StationViewModel::class.java)

        return inflater.inflate(R.layout.fragment_route_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun BitmapFromVector(context: Context, vectorResId:Int): BitmapDescriptor? {
        val vectorDrawable: Drawable = ContextCompat.getDrawable(context,vectorResId)!!
        vectorDrawable.setBounds(0,0,vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight)

        val bitmap: Bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                            vectorDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getCoordinates():Triple<ArrayList<LatLng> ,ArrayList<String>, ArrayList<LatLng>>{
        val coords : ArrayList<LatLng> = arrayListOf()
        val stationNames : ArrayList<String> = arrayListOf()
        val stationCoords : ArrayList<LatLng> = arrayListOf()

        for(coord in train.coords) coords.add(LatLng(coord.latitude, coord.longitude))
        for(r in train.route){
            if(r.is_stop) {
                stationNames.add(r.city)
                stationCoords.add(coords[r.coordInd])
            }
        }

        return Triple(coords, stationNames, stationCoords)
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