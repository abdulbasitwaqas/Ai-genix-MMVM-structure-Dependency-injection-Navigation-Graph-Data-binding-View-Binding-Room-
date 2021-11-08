package com.jsbl.genix.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jsbl.genix.R
import com.jsbl.genix.databinding.FragmentGamesDetailsBinding
import com.jsbl.genix.utils.callBacks.OnViewClickListener

class GamesDetailsFragment : Fragment(),OnViewClickListener{

    private lateinit var binding: FragmentGamesDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGamesDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onClickListener = this
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {

            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
        }
    }
}