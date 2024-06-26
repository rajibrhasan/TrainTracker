package com.example.location

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.location.View.SearchStationActivity
import com.example.location.View.SearchTrainActivity
import com.example.location.View.SearchTrainResultActivity
import com.example.location.databinding.FragmentTrackTrainOldBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrainTrackold.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrainTrackold : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentTrackTrainOldBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTrackTrainOldBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var src : String = ""
        var dest : String = ""

        val tabName = arguments?.getString("tabName")


        binding.searchTrainEditText.setOnClickListener(){
            val intent = Intent(activity, SearchTrainActivity::class.java)
            intent.putExtra("tabName",tabName)
            startActivity(intent)
        }

        val srcLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    binding.srcStationEditText.setText(data.getStringExtra("station"))
                    src = binding.srcStationEditText.text.toString()
                }

            }
        }

        val destLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    binding.destStationEditText.setText(data.getStringExtra("station"))

                }

            }
        }

        binding.srcStationEditText.setOnClickListener(){
            val intent = Intent(activity, SearchStationActivity::class.java)
            intent.putExtra("src",src)
            srcLauncher.launch(intent)

        }

        binding.destStationEditText.setOnClickListener(){
            if(src == ""){
                showAlertDialog("Please select source first.")
            }

            else{
                val intent = Intent(activity,SearchStationActivity::class.java)
                intent.putExtra("src",src)
                destLauncher.launch(intent)
            }

        }

        binding.searchTrainBtn.setOnClickListener(){
            val intent = Intent(activity,SearchTrainResultActivity::class.java)
            intent.putExtra("tabName",tabName)
            src = binding.srcStationEditText.text.toString()
            dest = binding.destStationEditText.text.toString()
            if( src != "" && dest != ""){
                intent.putExtra("src",src)
                intent.putExtra("dest",dest)
                startActivity(intent)
            }
            else{
                showAlertDialog("Please select both source and destination staions.")
            }

        }

    }

    fun showAlertDialog(message : String){
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.apply {
            //setIcon(R.drawable.ic_hello)
            setTitle("Error!!")
            setMessage(message)

            setNegativeButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        }.create().show()
    }


}