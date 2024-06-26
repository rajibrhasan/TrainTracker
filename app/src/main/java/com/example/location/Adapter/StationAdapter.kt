package com.example.location.Adapter
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Model.Station
import com.example.location.Model.Train
import com.example.location.Model.TrainSuggestion
import com.example.location.R
import com.example.location.TrainApplication
import com.example.location.View.TrackLocationActivity
import com.example.location.View.TrainDetailActivity
import com.example.location.ViewModel.TrainViewModel
import com.example.location.ViewModel.TrainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StationAdapter(private val activity: Activity) :  RecyclerView.Adapter<StationAdapter.StationViewHolder>() {
    var stations:List<Station> = ArrayList()
    class StationViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        val stationTitle : TextView = itemView.findViewById(R.id.stationTitle)
        val cardView : CardView = itemView.findViewById(R.id.stationCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.station_item,parent,false)

        return StationViewHolder(view)

    }

    override fun getItemCount(): Int {
        return stations.size
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {

        val currentStation : Station = stations[position]
        holder.stationTitle.text = currentStation.name.uppercase()

        holder.cardView.setOnClickListener{
            val intent = Intent()
            intent.putExtra("station",currentStation.name)
            activity.setResult(RESULT_OK, intent);
            activity.finish()
        }

    }

    fun setMyStations(mystations:List<Station>){
        this.stations = mystations
        notifyDataSetChanged()
    }

    fun getStation(position: Int):Station{
        return stations[position]
    }


}





