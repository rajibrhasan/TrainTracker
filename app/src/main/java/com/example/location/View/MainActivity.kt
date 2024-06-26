package com.example.location.View

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.location.R
import com.example.location.Util.BackgroundLocation.LocationService
import com.example.location.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class MainActivity : AppCompatActivity(){
    private lateinit var binding : ActivityMainBinding
    private lateinit var tabName : String
    private lateinit var sharedPref : SharedPreferences
    private lateinit var src : String
    private lateinit var dest : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("first_page", MODE_PRIVATE)

        tabName = binding.mainTabs.getTabAt(binding.mainTabs.selectedTabPosition)?.text.toString()

        binding.mainTabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabName = tab.text.toString()
                sharedPref.edit().putInt("tab",binding.mainTabs.selectedTabPosition).apply()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        handle_search()
        handle_railsheba()
    }

    override fun onResume() {
        super.onResume()
        val sh = getSharedPreferences("first_page", MODE_PRIVATE)
        val tab = binding.mainTabs.getTabAt(sh.getInt("tab",0))
        tab?.select()
    }

    private fun handle_search(){

        src = ""
        dest = ""
        binding.trainEdit.setOnClickListener(){
            val intent = Intent(this, SearchTrainActivity::class.java)
            intent.putExtra("tabName",tabName)
            startActivity(intent)
        }

        val srcLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    binding.srcText.setText(data.getStringExtra("station"))
                    binding.srcText.setTextColor(ContextCompat.getColor(this, R.color.rail_sheba_green))
                    src = binding.srcText.text.toString()
                    sharedPref.edit().putString("src",src).apply()
                }

            }
        }

        val destLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    binding.destText.setText(data.getStringExtra("station"))
                    binding.destText.setTextColor(ContextCompat.getColor(this, R.color.rail_sheba_green))
                    dest = binding.destText.text.toString()
                    sharedPref.edit().putString("dest",dest).apply()

                }

            }
        }

        binding.srcEdit.setOnClickListener(){
            val intent = Intent(this, SearchStationActivity::class.java)
            intent.putExtra("src",src)
            srcLauncher.launch(intent)

        }

        binding.destEdit.setOnClickListener(){
            if(src == ""){
                showAlertDialog("Please select source first.")
            }

            else{
                val intent = Intent(this,SearchStationActivity::class.java)
                intent.putExtra("src",src)
                destLauncher.launch(intent)
            }

        }

        binding.searchTrainBtn.setOnClickListener(){
            val intent = Intent(this,SearchTrainResultActivity::class.java)
            intent.putExtra("tabName",tabName)
            if( src != "" && dest != ""){
                intent.putExtra("src",src)
                intent.putExtra("dest",dest)
                intent.putExtra("tabName",tabName)
                startActivity(intent)
            }
            else{
                showAlertDialog("Please select both source and destination staions.")
            }

        }

    }

    fun showAlertDialog(message : String){
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.apply {
            //setIcon(R.drawable.ic_hello)
            setTitle("Error!!")
            setMessage(message)

            setNegativeButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        }.create().show()
    }

    private fun handle_railsheba(){
        val intent = Intent(this, WebviewActivity::class.java)
        binding.buyticketsBtn.setOnClickListener(){
            val intent2 = Intent(this,RecognitionActivity::class.java)
            intent.putExtra("url","http://railapp.railway.gov.bd/")
            startActivity(intent2)
        }
        binding.verifyBtn.setOnClickListener(){
            //binding.verifyBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.sheba_tab_background))
            intent.putExtra("url","http://railapp.railway.gov.bd/verify-ticket")
            startActivity(intent)
        }
        binding.myticketsBtn.setOnClickListener(){
            //binding.myticketsBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.sheba_tab_background))
            intent.putExtra("url","http://railapp.railway.gov.bd/history/upcomming")
            startActivity(intent)
        }
        binding.myaccountBtn.setOnClickListener(){
            //binding.myaccountBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.sheba_tab_background))
            intent.putExtra("url","http://railapp.railway.gov.bd/profile")
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        })

    }
}


