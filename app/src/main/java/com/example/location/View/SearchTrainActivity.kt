package com.example.location.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location.Adapter.TrainAdapter
import com.example.location.Model.TrainSuggestion
import com.example.location.R
import com.example.location.TrainApplication
import com.example.location.ViewModel.TrainViewModel
import com.example.location.ViewModel.TrainViewModelFactory

class SearchTrainActivity : AppCompatActivity() {
    private lateinit var trainViewModel: TrainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_train)
        setSupportActionBar(findViewById(R.id.td_toolbar))



        val title = findViewById<TextView>(R.id.textView4)
        val noresultText = findViewById<TextView>(R.id.searchTrain_noResult)
        val clearBtn = findViewById<ImageView>(R.id.clearBtn)
        val searchText = findViewById<EditText>(R.id.searchEditText)

        clearBtn.visibility = INVISIBLE
        title.text = resources.getString(R.string.popular_trains)

        val tabName = intent.getStringExtra("tabName")?:""

        val recyclerView : RecyclerView = findViewById(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val viewModelFactory = TrainViewModelFactory((application as TrainApplication).repository)
        trainViewModel = ViewModelProvider(this, viewModelFactory).get(TrainViewModel::class.java)

        val trainAdapter = TrainAdapter(this,trainViewModel, tabName)
        recyclerView.adapter = trainAdapter

        trainViewModel.myAllTrainNames.observe(this) { trains ->
            trainAdapter.setMyTrains(trains)
        }

        searchText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before:Int, count: Int) {
                val text = searchText.text.toString()
                if(text.length > 2){
                    title.text = resources.getString(R.string.suggestions)
                    val newTrainList : MutableList<TrainSuggestion> = arrayListOf()

                    trainViewModel.myAllTrainNames.observe(this@SearchTrainActivity){ trains->
                        for(train in trains) if(train.name.lowercase().contains(text.lowercase())) newTrainList.add(train)
                    }

                    trainAdapter.setMyTrains(newTrainList)
                    if(newTrainList.size == 0) noresultText.text = resources.getString(R.string.no_result_train)
                    else noresultText.text = ""
                }
                else{
                    title.text = resources.getString(R.string.popular_trains)
                    title.visibility = VISIBLE
                    trainViewModel.myAllTrainNames.observe(this@SearchTrainActivity){ trains->
                        trainAdapter.setMyTrains(trains)
                    }
                    if(text.length>=1) clearBtn.visibility = VISIBLE
                }
            }
            override fun afterTextChanged(p0: Editable?) {}

        })
        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,0){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }).attachToRecyclerView(recyclerView)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener{
            finish()
        }

        clearBtn.setOnClickListener(){
            searchText.setText("")
        }
    }
}




