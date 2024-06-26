package com.example.location.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Model.Route
import com.example.location.R
import kotlin.math.roundToInt

class NoHaltAdapter(private val activity: FragmentActivity) : RecyclerView.Adapter<NoHaltAdapter.NoHaltViewHolder>() {

    var routeList:List<Route> = arrayListOf()

    class NoHaltViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val textViewArr: TextView = itemView.findViewById(R.id.nohalt_arrival)
        val textViewStation : TextView = itemView.findViewById(R.id.nohalt_city)
        val textViewDep : TextView = itemView.findViewById(R.id.nohalt_dep)
        val tv_dist : TextView = itemView.findViewById(R.id.nohalt_distance)
        val cardView : CardView = itemView.findViewById(R.id.nohalt_cardView)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoHaltViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.no_halt_item,parent,false)

        return NoHaltViewHolder(view)

    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    override fun onBindViewHolder(holder: NoHaltViewHolder, position: Int) {
        val currentRoute : Route = routeList[position]
        holder.textViewArr.text = currentRoute.arr_time
        holder.textViewStation.text = currentRoute.city
        holder.textViewDep.text = currentRoute.dep_time
        holder.tv_dist.text = currentRoute.distance.roundToInt().toString()+" km"
    }



    fun setNoHaltRoutes(myRoutes:List<Route>){
        this.routeList = myRoutes
        notifyDataSetChanged()
    }

    fun getRoute(position: Int): Route {
        return routeList[position]
    }

}