package com.example.location.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Adapter.TrainAdapter
import com.example.location.Model.Train
import com.example.location.Model.TrainSuggestion
import com.example.location.R
import com.example.location.TrainApplication
import com.example.location.ViewModel.TrainViewModel
import com.example.location.ViewModel.TrainViewModelFactory
import com.example.location.databinding.ActivityTrainResultBinding

class SearchTrainResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTrainResultBinding
    private lateinit var viewModel: TrainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrainResultBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_progress_bar)

        val pb_src : TextView = findViewById(R.id.pb_src)
        val pb_dest : TextView = findViewById(R.id.pb_dest)

        val src = intent.getStringExtra("src")?:""
        val dest = intent.getStringExtra("dest")?:""

        pb_src.setText(src)
        pb_dest.setText(dest)

        val tabName = intent.getStringExtra("tabName")?:""

        val recyclerView : RecyclerView = binding.trainResultRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val viewModelFactory = TrainViewModelFactory((application as TrainApplication).repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TrainViewModel::class.java)

        val trainAdapter = TrainAdapter(this,viewModel, tabName)
        recyclerView.adapter = trainAdapter

        viewModel.myAllTrains.observe(this){ trains->
            val newList = mutableListOf<TrainSuggestion>()
            for(tr in trains){
                if(checkRoute(tr, src, dest)) newList.add(TrainSuggestion(tr.id,tr.name,tr.src,tr.dest,tr.off_day, tr.leave_time,
                tr.arr_time,tr.total_duration, tr.total_distance))
            }

            trainAdapter.setMyTrains(newList)
            setContentView(binding.root)

            binding.trainResultSrc.text  = src
            binding.trainResultDest.text = dest

            if(newList.size == 0) binding.trainResultNoResult.setText(resources.getString(R.string.no_result_train))

        }

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,0){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //trainViewModel.delete(trainAdapter.getTrain(viewHolder.adapterPosition))
                TODO("Not yet implemented")
            }
        }).attachToRecyclerView(recyclerView)

        binding.trainResultBackBtn.setOnClickListener(){
            finish()
        }
    }

    private fun checkRoute(tr : Train, src : String, dest : String) : Boolean{
        var srcInd = -1
        var destInd = -1

        for(i in 0..tr.route.size-1){
            val current_route = tr.route[i]
            if(current_route.city == src && current_route.is_stop) srcInd = i
            if(current_route.city == dest && current_route.is_stop) destInd = i
        }
        if(srcInd !=-1 && destInd!=-1 && srcInd < destInd) return true
        return false
    }
}