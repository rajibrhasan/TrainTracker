package com.example.location.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Model.TrainSuggestion
import com.example.location.R
import com.example.location.View.TrainDetailActivity
import com.example.location.View.TrainLocationActivity
import com.example.location.ViewModel.TrainViewModel
import com.example.location.databinding.InfoDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class TrainAdapter(private val activity: Activity, private val viewModel : TrainViewModel, val tabName : String) : RecyclerView.Adapter<TrainAdapter.TrainViewHolder>() {
    var trains:List<TrainSuggestion> = ArrayList()
    private var onTrain : Boolean = false
    class TrainViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        val tv_trainName : TextView = itemView.findViewById(R.id.trainItem_name)
        val tv_src : TextView = itemView.findViewById(R.id.trainItem_src)
        val tv_dest : TextView = itemView.findViewById(R.id.trainItem_dest)
        val ti_cardView : CardView = itemView.findViewById(R.id.cardView)
        val tv_distance : TextView = itemView.findViewById(R.id.trainItem_distance)
        val tv_time : TextView = itemView.findViewById(R.id.trainItem_totalTime)
        val tv_arrTime : TextView = itemView.findViewById(R.id.trainItem_arrTime)
        val tv_depTime : TextView = itemView.findViewById(R.id.trainItem_depTime)
        val btn : Button = itemView.findViewById(R.id.trainItem_clickBtn)
        val sun : TextView = itemView.findViewById(R.id.sun)
        val mon : TextView = itemView.findViewById(R.id.mon)
        val tue : TextView = itemView.findViewById(R.id.tue)
        val wed : TextView = itemView.findViewById(R.id.wed)
        val thu : TextView = itemView.findViewById(R.id.thu)
        val fri : TextView = itemView.findViewById(R.id.fri)
        val sat : TextView = itemView.findViewById(R.id.sat)

        val map : Map<String, TextView> = mapOf("Sun" to sun,"Mon" to mon,
            "Tue" to tue, "Wed" to wed, "Thu" to thu, "Fri" to fri, "Sat" to sat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.train_item,parent,false)
        return TrainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trains.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TrainViewHolder, position: Int) {
        val currentTrain : TrainSuggestion = trains[position]
        val total_duration = currentTrain.total_duration.replace(":","h ")+"m"
        val total_distance = currentTrain.total_distance.roundToInt().toString()+" km"
        holder.tv_trainName.text = currentTrain.name
        holder.tv_src.text = currentTrain.src.uppercase()
        holder.tv_dest.text = currentTrain.dest.uppercase()
        holder.tv_depTime.text = currentTrain.leave_time.replace("BST","")
        holder.tv_arrTime.text = currentTrain.arr_time.replace("BST","")
        holder.tv_time.text = total_duration
        holder.tv_distance.text = total_distance
        holder.btn.text = tabName

        if(currentTrain.off_day != ""){
            val day_name = currentTrain.off_day
           holder.map.forEach{(day,tv)->
               if(day_name !=" " && day == day_name) {
                   tv.setTextColor(ActivityCompat.getColor(activity,R.color.red))
                   //tv.setPaintFlags(tv.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
               }
               else {
                   tv.setTextColor(ActivityCompat.getColor(activity,R.color.rail_sheba_green))
                   //if(tv.getPaint().isStrikeThruText()) tv.setPaintFlags(tv.getPaintFlags() and ( Paint.STRIKE_THRU_TEXT_FLAG.inv()))
               }

           }
        }

        holder.btn.setOnClickListener{
            val intent : Intent
            if(!start_location(tabName)) {
                intent = Intent(activity, TrainDetailActivity::class.java)
            }
            else intent = Intent(activity, TrainLocationActivity::class.java)

            val sharedPref = activity.getSharedPreferences("first_page", AppCompatActivity.MODE_PRIVATE)
            sharedPref.edit().putString("train_name",currentTrain.name).apply()

            //intent.putExtra("train", currentTrain)
            CoroutineScope(Dispatchers.Main).launch{
                val train = viewModel.getMyTrain(currentTrain.id)
                intent.putExtra("train", train)
                val tabNum = getTabNum(tabName)
                intent.putExtra("tabNum",tabNum)

                if(start_location(tabName)) showDialog(intent)
                else activity.startActivity(intent)
            }
        }
    }

    fun setMyTrains(mytrains:List<TrainSuggestion>){
        this.trains = mytrains
        notifyDataSetChanged()
    }

    fun getTrain(position: Int):TrainSuggestion{
        return trains[position]
    }

    private fun start_location(tabName : String) : Boolean{
        if(tabName.lowercase() == "time table" || tabName.lowercase() == "route map") return false
        return true
    }

    fun handle_off_day(){

    }

    fun getTabNum(tabName : String) : Int{
        val newTab = tabName.lowercase()
        when(newTab){
            "train track" -> return 0
            "live status" -> return 1
            "time table" -> return 0
            "route map" -> return 1
        }
        return 0
    }

    private fun cleanTrainName(name: String) : String{
        val words : ArrayList<String> = name.split(' ') as ArrayList<String>
        for( i in 0 .. words.size-1){
            words[i] = words[i].lowercase()
            words[i] = words[i].replaceFirstChar {it.uppercase()  }
        }
        return words.joinToString(separator = " ")
    }

    private fun showDialog(intent : Intent){
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.info_dialog, null)
        val dialogBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val dialogBinding = InfoDialogBinding.bind(dialogView)
        val customAlertDialog = dialogBuilder.show()

        dialogBinding.yesBtn.setOnClickListener {
            onTrain = true
            intent.putExtra("onTrain",true)
            customAlertDialog.dismiss()
            activity.startActivity(intent)
        }

        dialogBinding.noBtn.setOnClickListener(){
            onTrain = false
            intent.putExtra("onTrain",false)
            customAlertDialog.dismiss()
            activity.startActivity(intent)
        }

        dialogBinding.cancelDialog.setOnClickListener(){
            customAlertDialog.dismiss()
        }
    }

}