package com.jsbl.genix.views.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.model.DogBreed
import com.jsbl.genix.viewModel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*


/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private val dogListAdapter =
        OnViewListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //For menu
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

   /* override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.refresh()

        dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogListAdapter
        }

        refresh.setOnRefreshListener {
            dogsList.visibility = GONE
            error.visibility = GONE
            progressBar.visibility = VISIBLE
            viewModel.refreshBypassCache()
            refresh.isRefreshing = false
        }

        obserceViewModel()
    }

    fun obserceViewModel() {
        viewModel.dogs.observe(viewLifecycleOwner, Observer {
            it?.let {
                dogsList.visibility = VISIBLE
                dogListAdapter.updateDogList(it as ArrayList<DogBreed>)
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {
            it?.let {
//                error.visibility = VISIBLE
                error.visibility = if (it) VISIBLE else GONE

            }
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            it?.let {
                progressBar.visibility = if (it) VISIBLE else GONE

                if (it) {
                    error.visibility = GONE
                    dogsList.visibility = GONE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSettings -> {
                view?.let {
                    Navigation.findNavController(it)
                        .navigate(ListFragmentDirections.actioSettings())
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }*/


}
