package com.olrep.gitpulls.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.olrep.gitpulls.R
import com.olrep.gitpulls.utils.Utils

class MainFragment : Fragment() {
    companion object {
        private const val TAG = Utils.TAG + "MF"
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val rv: RecyclerView = view.findViewById(R.id.rv)
        val llm = LinearLayoutManager(activity)
        rv.layoutManager = llm
        rv.setHasFixedSize(true)
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItems = rv.layoutManager!!.itemCount
                val lastVisibleItemPosition = llm.findLastVisibleItemPosition()
                if (totalItems == lastVisibleItemPosition + 1) {

                }

            }
        })

        val adapter = Adapter()
        rv.adapter = adapter

        val progressBar: ProgressBar = view.findViewById(R.id.progress_circular)
        viewModel.progress.observe(this, {
            Log.d(TAG, "observed on progress: $it")

            progressBar.visibility = if (it.first) View.VISIBLE else View.GONE

            if (it.second) {
                Toast.makeText(activity, "Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.pulls.observe(this, {
            Log.d(TAG, "observed on pulls: $it")

            adapter.setData(it)
        })

        viewModel.username.observe(this, {
            Log.d(TAG, "username changed, clearing adapter data. new user is $it")
            adapter.clear()
        })

        viewModel.getPulls("defunkt", 15, 1)
    }
}