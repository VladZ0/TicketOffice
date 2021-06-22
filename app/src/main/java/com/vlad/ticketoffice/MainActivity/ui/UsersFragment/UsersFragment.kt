package com.vlad.ticketoffice.MainActivity.ui.UsersFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vlad.ticketoffice.MainActivity.adapters.UsersAdapter
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.UserDataActivity.UserDataActivity
import com.vlad.ticketoffice.databinding.FragmentUsersBinding
import com.vlad.ticketoffice.model.User

class UsersFragment: Fragment() {
    private lateinit var mBinding: FragmentUsersBinding
    private lateinit var mViewModel: UsersFragmentViewModel
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUsersBinding.bind(inflater.inflate(R.layout.fragment_users, container, false))
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerViewWithViewModel()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.initUsersSnapshotListener()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.removeSnapshotListeners()
    }

    private fun initRecyclerView(isAdmin: Boolean){
        usersAdapter = UsersAdapter()

        mBinding.usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mBinding.usersRecyclerView.adapter = usersAdapter
    }

    private fun initRecyclerViewWithViewModel(){
        mViewModel = ViewModelProvider(this).get(UsersFragmentViewModel::class.java)

        mViewModel.users.observe(viewLifecycleOwner){

            usersAdapter.users = it
            mBinding.usersRecyclerView.adapter = usersAdapter
            usersAdapter.userItemClickListener = object : UsersAdapter.OnUserItemClickListener{
                override fun onUserItemClick(view: View, position: Int) {
                    if(it[position].role == User.Role.USER) {
                        val userIntent = Intent(requireContext(), UserDataActivity::class.java)
                        userIntent.putExtra("user", it[position])
                        startActivity(userIntent)
                    }
                }
            }
        }

        mViewModel.currentUserRole.observe(viewLifecycleOwner){
            initRecyclerView(it == User.Role.ADMIN)
        }
    }
}