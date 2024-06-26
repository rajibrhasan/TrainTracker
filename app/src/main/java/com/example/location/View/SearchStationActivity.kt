package com.example.location.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Adapter.StationAdapter
import com.example.location.Adapter.TrainAdapter
import com.example.location.Model.Station
import com.example.location.Model.TrainSuggestion
import com.example.location.R
import com.example.location.TrainApplication
import com.example.location.ViewModel.StationViewModel
import com.example.location.ViewModel.StationViewModelFactory
import com.example.location.ViewModel.TrainViewModel
import com.example.location.ViewModel.TrainViewModelFactory
import com.example.location.databinding.ActivityMainBinding
import com.example.location.databinding.ActivitySearchStationBinding

class SearchStationActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchStationBinding
    private lateinit var stationViewModel: StationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchStationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchStationClearBtn.visibility = INVISIBLE
        binding.searchStationResultText.text = resources.getString(R.string.popular_stations)

        val recyclerView : RecyclerView = binding.searchStationRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val viewModelFactory = StationViewModelFactory((application as TrainApplication).stRepository)
        stationViewModel = ViewModelProvider(this, viewModelFactory).get(StationViewModel::class.java)

        val stAdapter = StationAdapter(this)
        recyclerView.adapter = stAdapter

        val src : String? = intent.getStringExtra("src")


        stationViewModel.myAllStations.observe(this){ stations->
            val newList = mutableListOf<Station>()
            for(st in stations){
                if(src != st.name) newList.add(st)
            }
            stAdapter.setMyStations(newList)
        }


        val searchText = binding.searchStationEditText

        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before:Int, count: Int) {
                val text = searchText.text.toString()
                if(text.length>1){
                    binding.searchStationResultText.setText(resources.getString(R.string.suggestions))
                    val newList : MutableList<Station> = arrayListOf()
                    stationViewModel.myAllStations.observe(this@SearchStationActivity){ stations->
                        for(st in stations) if(src != st.name && st.name.lowercase().contains(text.lowercase())) newList.add(st)
                    }
                    stAdapter.setMyStations(newList)

                    if(newList.size == 0)
                        binding.searchStationNoResult.setText(resources.getString(R.string.no_station))

                    else binding.searchStationNoResult.setText("")


                }
                else{
                    binding.searchStationResultText.setText(resources.getString(R.string.popular_stations))
                    binding.searchStationNoResult.visibility = View.VISIBLE
                    stationViewModel.myAllStations.observe(this@SearchStationActivity){ stations->
                        val newList = mutableListOf<Station>()
                        if(src != null){
                            for(st in stations){
                                if(src != st.name) newList.add(st)
                            }
                        }
                        stAdapter.setMyStations(newList)
                    }

                    if(text.length>=1) binding.searchStationClearBtn.visibility = VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

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


        binding.searchStationBackBtn.setOnClickListener{
            finish()
        }


        binding.searchStationClearBtn.setOnClickListener(){
            searchText.setText("")
        }
    }
}