package com.vlad.ticketoffice.MainActivity.ui.PrivateOfficeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vlad.ticketoffice.MainActivity.adapters.TicketsInfoAdapter
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.databinding.FragmentPrivateOfficeBinding
import com.vlad.ticketoffice.model.Race

class FragmentPrivateOffice: Fragment() {
    private lateinit var mBinding: FragmentPrivateOfficeBinding
    private lateinit var mViewModel: FragmentPrivateOfficeViewModel
    private lateinit var mTicketsInfoAdapter: TicketsInfoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPrivateOfficeBinding
            .bind(inflater.inflate(R.layout.fragment_private_office, container, false))
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(FragmentPrivateOfficeViewModel::class.java)

        mTicketsInfoAdapter = TicketsInfoAdapter()

        mViewModel.bookedRaces.observe(viewLifecycleOwner){
            mTicketsInfoAdapter.ticketsInfo = it as MutableList<Race>
        }

        mBinding.ticketsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mBinding.ticketsRecyclerView.adapter = mTicketsInfoAdapter
    }

    override fun onStart() {
        super.onStart()
        mViewModel.initSnapshotListeners()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.removeSnapshotListeners()
    }
}