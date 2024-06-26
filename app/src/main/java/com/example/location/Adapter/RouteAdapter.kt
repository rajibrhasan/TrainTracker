package com.example.location.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Model.Route
import com.example.location.R
import kotlin.math.roundToInt

class RouteAdapter(private val activity: FragmentActivity) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {
    var routeList:List<Route> = arrayListOf()
    val haltInd : MutableList<Int> = arrayListOf()
    class RouteViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        val textViewArr:TextView = itemView.findViewById(R.id.time_arrival)
        val textViewStation : TextView = itemView.findViewById(R.id.time_city)
        val textViewDep : TextView = itemView.findViewById(R.id.time_dep)
        val cardView : CardView = itemView.findViewById(R.id.cardView)
        val hide_before : View = itemView.findViewById(R.id.ri_llhide_before)
        val hide_after1 : View = itemView.findViewById(R.id.ri_llhide_after1)
        val hide_after2 : View = itemView.findViewById(R.id.ri_llhide_after2)
        val tv_dist : TextView = itemView.findViewById(R.id.ri_dist)
        val tv_duration : TextView = itemView.findViewById(R.id.ri_duration)
        val rv : RecyclerView = itemView.findViewById(R.id.ri_rv)
        val btn_nohalt : Button = itemView.findViewById(R.id.ri_btn_nohalt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.route_item,parent,false)

        return RouteViewHolder(view)

    }

    override fun getItemCount(): Int {
        return haltInd.size
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val currentRoute : Route = routeList[haltInd[position]]
        val nohalts = getNoHalts(position)
        var nohalt_open = false
        var duration = currentRoute.duration.replace("null","---").replace("00","0")
        if(position == 0) duration = "0:0"
        holder.textViewArr.text = currentRoute.arr_time
        holder.textViewStation.text = currentRoute.city
        holder.textViewDep.text = currentRoute.dep_time
        holder.btn_nohalt.text = nohalts.size.toString() + " no-halt stations"
        holder.tv_dist.text = currentRoute.distance.roundToInt().toString() + " km"
        holder.tv_duration.text = duration.replace(":","h ")+"m"
        holder.rv.visibility = GONE


        if(position  == 0) holder.hide_before.visibility = GONE
        else holder.hide_before.visibility = VISIBLE

        if(position  == haltInd.size-1) {
            holder.hide_after1.visibility = GONE
            holder.hide_after2.visibility = GONE
        }
        else {
            holder.hide_after1.visibility = VISIBLE
            holder.hide_after2.visibility = VISIBLE
        }

        holder.rv.layoutManager = LinearLayoutManager(holder.hide_before.context)
        val nohaltAdapter = NoHaltAdapter(activity)

        holder.rv.adapter = nohaltAdapter
        nohaltAdapter.setNoHaltRoutes(nohalts)

        holder.btn_nohalt.setOnClickListener{view->
            if(nohalt_open){
                holder.btn_nohalt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.keyboard_arrow_down,0,0,0)
                holder.rv.visibility = GONE
                nohalt_open = false
            }
            else{
                holder.btn_nohalt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.keyboard_arrow_up,0,0,0)
                holder.rv.visibility = VISIBLE
                nohalt_open = true
            }


        }
    }

    fun setMyRoutes(myRoutes:List<Route>){
        this.routeList = myRoutes
        haltInd.clear()
        for(i in 0..myRoutes.size-1){
            val r = myRoutes[i]
            if(r.is_stop) haltInd.add(i)
        }
        notifyDataSetChanged()
    }

    private fun getNoHalts(position : Int) : ArrayList<Route>{
        val nohalts : ArrayList<Route> = arrayListOf()
        if(position != haltInd.size - 1 ){
            for(i in haltInd[position]+1..haltInd[position+1]-1){
                nohalts.add(routeList[i])
            }
        }

        return nohalts
    }


    fun getRoute(position: Int):Route{
        return routeList[haltInd[position]]
    }



}