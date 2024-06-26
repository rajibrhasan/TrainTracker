package com.example.location.View

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Adapter.RouteAdapter
import com.example.location.Adapter.RouteAdapter2
import com.example.location.Model.FirebaseTrain
import com.example.location.Model.Route
import com.example.location.Model.Train
import com.example.location.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable


class LiveStatusFragment : Fragment() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var ref: DatabaseReference
    private lateinit var  fb_train : FirebaseTrain
    private lateinit var routeAdapter : RouteAdapter2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_live_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val train = getArguments()?.getSerializable("train") as Train
        val recyclerView: RecyclerView = view.findViewById(R.id.live_rv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        routeAdapter = RouteAdapter2(requireActivity())

        ref = database.reference.child("trains").child(train.id.toString())

        recyclerView.adapter = routeAdapter
        routeAdapter.setMyRoutes(train.route)
        retrieveDataFromDatabase(train)
    }

    private fun getHalts(route : List<Route>) : List<Route>{
        val newRoute : MutableList<Route> = arrayListOf()
        for(r in route){
            if(r.is_stop) newRoute.add(r)
        }
        return newRoute
    }

    fun retrieveDataFromDatabase(train : Train) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fb_train = snapshot.getValue(FirebaseTrain::class.java)!!
                routeAdapter.setMyPos(fb_train.lastStationInd)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(),error.toString(), Toast.LENGTH_SHORT).show()
            }

        })

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
   