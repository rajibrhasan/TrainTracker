package com.example.location.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Model.Route
import com.example.location.R
import kotlin.math.roundToInt

class NoHaltAdapter2(private val activity : FragmentActivity) : RecyclerView.Adapter<NoHaltAdapter2.NoHaltViewHolder2>()  {
    var routeList:ArrayList<Route> = arrayListOf()
    var currentPos : Int = -1

    class NoHaltViewHolder2(itemView : View): RecyclerView.ViewHolder(itemView){
        val textViewArr: TextView = itemView.findViewById(R.id.nohalt_live_arrival)
        val textViewStation : TextView = itemView.findViewById(R.id.nohalt_live_city)
        val textViewDep : TextView = itemView.findViewById(R.id.no_halt_live_dep)
        val tv_dist : TextView = itemView.findViewById(R.id.nohalt_live_distance)
        val cardView : CardView = itemView.findViewById(R.id.nohalt_live_cardView)

        val line_before : ImageView = itemView.findViewById(R.id.nohalt_live_line_before)
        val line_after : ImageView = itemView.findViewById(R.id.nohalt_live_line_after)
        val st_img : ImageView = itemView.findViewById(R.id.no_halt_live_station)
        val ll_train_loc : LinearLayout = itemView.findViewById(R.id.nohalt_live_loc)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoHaltViewHolder2 {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.no_halt_live,parent,false)

        return NoHaltViewHolder2(view)

    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    override fun onBindViewHolder(holder: NoHaltAdapter2.NoHaltViewHolder2, position: Int) {
        val currentRoute : Route = routeList[position]


        holder.textViewArr.text = currentRoute.arr_time
        holder.textViewStation.text = currentRoute.city
        holder.textViewDep.text = currentRoute.dep_time
        holder.tv_dist.text = currentRoute.distance.roundToInt().toString()+" km"

        if(position <= currentPos){
            holder.line_before.setImageResource(R.drawable.rail_line_black_overlay)
            holder.st_img.setImageResource(R.drawable.station_inactive)
            holder.line_after.setImageResource(R.drawable.rail_line_black_overlay)

            if(position == currentPos) holder.ll_train_loc.visibility = VISIBLE
            else holder.ll_train_loc.visibility = GONE
        }

        else{
            holder.line_before.setImageResource(R.drawable.rail_line_blue)
            holder.st_img.setImageResource(R.drawable.station)
            holder.line_after.setImageResource(R.drawable.rail_line_blue)
            holder.ll_train_loc.visibility = GONE
        }

        if(position == itemCount-1) holder.ll_train_loc.visibility = GONE


    }


    fun setNoHaltRoutes2(myRoutes:ArrayList<Route>, pos : Int){
        this.routeList = myRoutes
        this.currentPos = pos
        notifyDataSetChanged()
    }


    fun getRoute2(position: Int): Route {
        return routeList[position]
    }
}