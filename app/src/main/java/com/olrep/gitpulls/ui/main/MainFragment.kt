package com.olrep.gitpulls.ui.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.snackbar.Snackbar
import com.olrep.gitpulls.R
import com.olrep.gitpulls.callback.ClickListener
import com.olrep.gitpulls.ui.web.WebActivity
import com.olrep.gitpulls.utils.Utils

class MainFragment : Fragment(), ClickListener {
    companion object {
        private const val TAG = Utils.TAG + "MF"
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

                if (newState == SCROLL_STATE_IDLE) {
                    Log.d(TAG, "Scrolling stopped, lastVisibleItemPosition is $lastVisibleItemPosition, totalItems are $totalItems")

                    if (totalItems == lastVisibleItemPosition + 1 && Utils.shouldLoadMore(totalItems, viewModel.totalCount)) {
                        Log.d(TAG, "reached end of the line - load more")
                        viewModel.username.value?.let { viewModel.getPulls(it) }
                    } else {
                        Log.i(TAG, "Can't or shouldn't load more")
                    }
                }
            }
        })

        val adapter = Adapter(this)
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

        val defaultUser = "defunkt"

        viewModel.getPulls(defaultUser) // this is the first load calls when there's no user entered

        Snackbar.make(view, "Loading user $defaultUser's pull requests on app launch", Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        val searchMenuItem: MenuItem? = menu.findItem(R.id.menu_item_search)
        val searchView: SearchView = searchMenuItem?.actionView as SearchView
        searchView.isIconified = true

        val searchManager: SearchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i(TAG, "onQueryTextSubmit: $query")
                query?.let { viewModel.getPulls(it) }
                hideSoftKeyboard()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d(TAG, "onQueryTextChange: $query")
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    fun hideSoftKeyboard() {
        if (activity?.currentFocus == null){
            return
        }

        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }

    override fun clicked(url: String, title: String) {
        val intent = Intent(activity, WebActivity::class.java)
        intent.putExtra(Utils.KEY_PR_URL, url)
        intent.putExtra(Utils.KEY_PR_TITLE, title)
        startActivity(intent)
    }
}