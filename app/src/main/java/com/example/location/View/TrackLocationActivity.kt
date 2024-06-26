package com.example.location.View

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location.R
import com.example.location.Adapter.TrainAdapter
import com.example.location.TrainApplication
import com.example.location.ViewModel.TrainViewModel
import com.example.location.ViewModel.TrainViewModelFactory

class TrackLocationActivity : AppCompatActivity(){

    private lateinit var trainViewModel: TrainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_location)


        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val viewModelFactory = TrainViewModelFactory((application as TrainApplication).repository)
        trainViewModel = ViewModelProvider(this, viewModelFactory).get(TrainViewModel::class.java)
        val trainAdapter = TrainAdapter(this,trainViewModel,"Route Map")
        recyclerView.adapter = trainAdapter
        trainViewModel.myAllTrainNames.observe(this) { trains ->
            trainAdapter.setMyTrains(trains)
        }

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
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

    }

}




/*fun registerActivityResultLauncher(){
    addActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultAddNote ->
        val resultCode = resultAddNote.resultCode
        val data = resultAddNote.data

        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val title: String = data.getStringExtra("Title").toString()
            val description: String = data.getStringExtra("Description").toString()

            val note = Note(title, description)
            noteViewModel.insert(note)

        }
    }

    updateActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultUpdateNote ->
        val resultCode = resultUpdateNote.resultCode
        val data = resultUpdateNote.data

        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val title: String = data.getStringExtra("title").toString()
            val description: String = data.getStringExtra("description").toString()
            val id: Int = data.getIntExtra("id", -1)

            val note = Note(title, description)
            note.id = id
            noteViewModel.update(note)
        }
    }
}
*/


