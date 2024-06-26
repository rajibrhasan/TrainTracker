package com.example.location.Adapter

import android.R.attr.button
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat.postOnAnimationDelayed
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Model.Route
import com.example.location.R
import kotlin.math.roundToInt


class RouteAdapter2(val activity : FragmentActivity) : RecyclerView.Adapter<RouteAdapter2.RouteViewHolder>() {
    var routeList:List<Route> = arrayListOf()
    val nohalt_map : MutableMap<Int, ArrayList<Route>> = mutableMapOf()
    val haltInd : ArrayList<Int> = arrayListOf()
    var currentPos : Int = -1
    var haltCurrent : Int = -1
    private lateinit var pressRunnable : Runnable
    private lateinit var unpressRunnable : Runnable

    class RouteViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        val arr_sch: TextView = itemView.findViewById(R.id.live_arr_sched)
        val arr_act: TextView = itemView.findViewById(R.id.live_arr_actual)
        val dep_sch : TextView = itemView.findViewById(R.id.live_dep_sched)
        val dep_act : TextView = itemView.findViewById(R.id.live_dep_actual)
        val st_name : TextView = itemView.findViewById(R.id.live_city)
        val cardView : CardView = itemView.findViewById(R.id.live_card)
        val halt : TextView = itemView.findViewById(R.id.live_halt)
        val dist : TextView = itemView.findViewById(R.id.live_dist)

        val st_img : ImageView = itemView.findViewById(R.id.live_station_img)
        val nohalt_btn : Button = itemView.findViewById(R.id.live_nohalt_btn)

        val line_before : ImageView = itemView.findViewById(R.id.live_line_before)
        val line_after : ImageView = itemView.findViewById(R.id.live_line_after)
        val line_with_btn : ImageView = itemView.findViewById(R.id.live_line_btn)

        val train_loc_before : LinearLayout = itemView.findViewById(R.id.live_loc_before)
        val train_loc_after : LinearLayout = itemView.findViewById(R.id.live_loc_after)
        val ll_with_btn : LinearLayout = itemView.findViewById(R.id.live_ll_btn)

        val rv_nohalt : RecyclerView = itemView.findViewById(R.id.live_rv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.route_item_live,parent,false)

        return RouteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return haltInd.size
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val currentRoute : Route = routeList[haltInd[position]]
        var nohalt_open = false
        val (nohalts, nohaltpos) = getNoHalts(position)
        var halt = currentRoute.halt
        if(position == 0 || position == itemCount - 1) halt = "0"
        val dist = currentRoute.distance.roundToInt().toString()


        holder.arr_sch.text = currentRoute.arr_time
        holder.st_name.text = currentRoute.city
        holder.dep_sch.text = currentRoute.dep_time
        holder.halt.text = "Halt: $halt min"
        holder.dist.text = "Distance: $dist km"
        holder.rv_nohalt.visibility = GONE

        holder.rv_nohalt.layoutManager = LinearLayoutManager(holder.line_before.context)
        val nohaltAdapter = NoHaltAdapter2(activity)
        holder.rv_nohalt.adapter = nohaltAdapter
        nohaltAdapter.setNoHaltRoutes2(nohalts, nohaltpos)
        holder.nohalt_btn.text = nohalts.size.toString() + " no-halt stations"

        var second_loc_visible = false


        if(position == 0) holder.line_before.visibility = GONE
        else holder.line_before.visibility = VISIBLE

        if(haltInd[position] < haltCurrent){
            holder.line_before.setImageResource(R.drawable.rail_line_black_overlay)
            holder.st_img.setImageResource(R.drawable.station_inactive)
            holder.line_after.setImageResource(R.drawable.rail_line_black_overlay)
            holder.train_loc_before.visibility = GONE
            holder.line_with_btn.setImageResource(R.drawable.rail_line_black_overlay)
            holder.train_loc_after.visibility = GONE


        }

        else if(haltInd[position] > haltCurrent){
            holder.line_before.setImageResource(R.drawable.rail_line_blue)
            holder.st_img.setImageResource(R.drawable.station)
            holder.line_after.setImageResource(R.drawable.rail_line_blue)
            holder.train_loc_before.visibility = GONE
            holder.line_with_btn.setImageResource(R.drawable.rail_line_blue)
            holder.train_loc_after.visibility = GONE

        }

        else{
            holder.line_before.setImageResource(R.drawable.rail_line_black_overlay)
            holder.st_img.setImageResource(R.drawable.station_inactive)
            holder.line_after.setImageResource(R.drawable.rail_line_black_overlay)
            if(currentPos == haltInd[position]) {
                holder.train_loc_before.visibility = VISIBLE
                holder.line_with_btn.setImageResource(R.drawable.rail_line_blue)
                holder.train_loc_after.visibility = GONE
            }

            else{
                holder.train_loc_before.visibility = GONE
                holder.line_with_btn.setImageResource(R.drawable.rail_line_black_overlay)
                holder.train_loc_after.visibility = VISIBLE
                second_loc_visible = true
            }

        }

        if(position == itemCount -1 ) holder.ll_with_btn.visibility = GONE
        else holder.ll_with_btn.visibility = VISIBLE



        holder.nohalt_btn.setOnClickListener{view->
            if(nohalt_open){
                holder.nohalt_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.keyboard_arrow_down,0,0,0)
                holder.rv_nohalt.visibility = GONE
                if(second_loc_visible) holder.train_loc_after.visibility = VISIBLE
                nohalt_open = false
            }
            else{
                holder.nohalt_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.keyboard_arrow_up,0,0,0)
                holder.rv_nohalt.visibility = VISIBLE
                if(nohaltpos >= 0 && nohaltpos < nohalts.size - 1){
                    holder.train_loc_after.visibility = GONE
                }

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

    fun getNoHalts(position : Int) : Pair<ArrayList<Route>, Int>{
        val nohalts : ArrayList<Route> = arrayListOf()
        var haltpos : Int = -1
        if(position != haltInd.size - 1 ){
            for(i in haltInd[position]+1..haltInd[position+1]-1){
                nohalts.add(routeList[i])
            }

            if(currentPos > haltInd[position] && currentPos < haltInd[position+1])
                haltpos = currentPos - haltInd[position]-1
        }

        if(currentPos < haltInd[position]) haltpos = -1
        else if(currentPos > haltInd[position+1] -1) haltpos = nohalts.size

        return Pair(nohalts,haltpos)

    }



    fun setMyPos(pos : Int){
        this.currentPos = pos
        for(item in haltInd){
            if(item <= pos) haltCurrent = item
        }
        notifyDataSetChanged()
    }

    fun getRoute(position: Int):Route{
        return routeList[position]
    }


}