package com.example.newsline.presentation

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsline.R
import com.example.newsline.domain.RecyclerAdapter
import com.example.newsline.api.enums.Category
import com.example.newsline.api.enums.Country
import com.example.newsline.data.repository.RemoteArticleRepositoryImpl
import com.example.newsline.databinding.ActivityMainBinding
import com.example.newsline.domain.Location
import com.example.newsline.domain.usecase.GetFilteredHeadlinesUseCase
import com.example.newsline.domain.usecase.GetHeadlinesUseCase
import com.example.newsline.domain.models.Article
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {

    private val listData = mutableListOf<Article>()
    private lateinit var mainViewModel: MainViewModel

    private val remoteArticleRepository = RemoteArticleRepositoryImpl()
    private val getHeadlinesUseCase = GetHeadlinesUseCase(remoteArticleRepository)
    private var getFilteredHeadlinesUseCase = GetFilteredHeadlinesUseCase(remoteArticleRepository)
    //todo add clearFilters button
    //     configure ViewModel
    //     add local storage with favorite articles
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityMainBinding =    DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = RecyclerAdapter(listData, this@MainActivity)

        binding.countrySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Country.values())
        val countrySpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Country.values())
        binding.countrySpinner.setSelection(countrySpinnerAdapter.getPosition(Location.Base().getCurrentCountryCode()))

        binding.categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Category.values())

        binding.filterRelativeLayout.visibility = GONE
        binding.filterImageButton.setOnClickListener {
            if (binding.filterRelativeLayout.visibility == GONE) {
                binding.filterRelativeLayout.visibility = VISIBLE
                binding.filterRelativeLayout.gravity = Gravity.TOP
            } else {
                binding.filterRelativeLayout.visibility = GONE
                binding.countrySpinner.setSelection(countrySpinnerAdapter.getPosition(Location.Base().getCurrentCountryCode()))
            }
        }

        binding.findImageButton.setOnClickListener {
            //binding.findMoreButton.visibility = VISIBLE
            binding.recyclerView.adapter?.notifyItemRangeRemoved(0, listData.size)
            listData.clear()
            PagesLoaded.value = 0
            getFilteredHeadlinesUseCase = getFilteredHeadlinesUseCase.updateFilters(
                binding.keywordsEditText.text.toString(),
                binding.countrySpinner.selectedItem.toString().lowercase(Locale.getDefault()),
                binding.categorySpinner.selectedItem.toString().lowercase(Locale.getDefault())
            )

            MainScope().launch {
                listData.addAll(getFilteredHeadlinesUseCase.execute())
                binding.recyclerView.adapter?.notifyItemInserted(listData.size - 1)
            }
            // make findMoreButton showing filtered articles
            binding.findMoreButton.setOnClickListener {
                if (!PagesLoaded.resultsEnded) {
                    MainScope().launch {
                        listData.addAll(getFilteredHeadlinesUseCase.execute())
                        binding.recyclerView.adapter?.notifyItemInserted(listData.size - 1)
                    }
                }
            }
        }
        binding.findMoreButton.setOnClickListener {
            if (!PagesLoaded.resultsEnded) {
                MainScope().launch {
                    listData.addAll(getHeadlinesUseCase.execute())
                    binding.recyclerView.adapter?.notifyItemInserted(listData.size - 1)
                }
            }
        }

        MainScope().launch {
            listData.addAll(getHeadlinesUseCase.execute())
            binding.recyclerView.adapter?.notifyItemInserted(listData.size - 1)
        }

        Log.d("DATA1", listData.size.toString())
    }

}