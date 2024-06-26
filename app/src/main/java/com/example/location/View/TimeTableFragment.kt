package com.example.location.View

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Model.Train
import com.example.location.R
import com.example.location.Adapter.RouteAdapter
import com.example.location.Model.Route
import java.io.Serializable


class TimeTableFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val train = getArguments()?.getSerializable("train") as Train
        val recyclerView: RecyclerView = view.findViewById(R.id.live_rv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val routeAdapter = RouteAdapter(requireActivity())

        recyclerView.adapter = routeAdapter
        routeAdapter.setMyRoutes(train.route)
    }

    private fun getHalts(route : List<Route>) : List<Route>{
        val newRoute : MutableList<Route> = arrayListOf()

        for (r in route){
            if(r.is_stop) newRoute.add(r)
        }

        return newRoute
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



