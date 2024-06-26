package com.example.location.View

import com.example.location.Adapter.ViewPagerAdapter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.location.Model.Train
import com.example.location.R
import com.example.location.TrainApplication
import com.example.location.Util.BackgroundLocation.LocationService
import com.example.location.ViewModel.TrainViewModel
import com.example.location.ViewModel.TrainViewModelFactory
import com.google.android.material.tabs.TabLayout
import java.io.Serializable

class TrainDetailActivity : AppCompatActivity() {

    private lateinit var trainViewModel: TrainViewModel
    private lateinit var train_name: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train_detail)

        train_name = findViewById(R.id.td_trainName)

        val viewModelFactory = TrainViewModelFactory((application as TrainApplication).repository)
        trainViewModel = ViewModelProvider(this, viewModelFactory).get(TrainViewModel::class.java)

        val tabLayout: TabLayout = findViewById(R.id.td_tabs)
        val viewPager: ViewPager = findViewById(R.id.td_viewPager)

        val train = intent.extras?.customGetSerializable<Train>("train")
        val onTrain = intent.getBooleanExtra("onTrain", false)

        if(train!=null) train_name.text = train.name



        val tabNum = intent.getIntExtra("tabNum", 0)
        val tabAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = tabAdapter


        tabLayout.setupWithViewPager(viewPager)
        val bundle = Bundle()
        bundle.putSerializable("train", train)

        val timeTableFragment: Fragment = TimeTableFragment()
        timeTableFragment.setArguments(bundle)
        tabAdapter.addFragment(timeTableFragment, "Time Table")

        val routeFragment: Fragment = RouteMapFragment()
        routeFragment.setArguments(bundle)
        tabAdapter.addFragment(routeFragment, "Route Map")

        viewPager.setCurrentItem(tabNum)

        val backbtn2: ImageView = findViewById(R.id.td_backBtn)

        backbtn2.setOnClickListener {
            finish()
        }

    }



    @Suppress("DEPRECATION")
    inline fun <reified T : Serializable> Bundle.customGetSerializable(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, T::class.java)
        } else {
            getSerializable(key) as? T

        }
    }

}
      