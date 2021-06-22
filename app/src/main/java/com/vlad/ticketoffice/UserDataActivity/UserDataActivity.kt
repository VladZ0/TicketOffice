package com.vlad.ticketoffice.UserDataActivity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vlad.ticketoffice.MainActivity.MainActivity
import com.vlad.ticketoffice.MainActivity.adapters.CardsAdapter
import com.vlad.ticketoffice.databinding.ActivityUserDataBinding
import com.vlad.ticketoffice.model.User

class UserDataActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityUserDataBinding
    private lateinit var mViewModel: UserDataActivityViewModel
    private lateinit var mCardsAdapter: CardsAdapter

    private var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mViewModel = ViewModelProvider(this).get(UserDataActivityViewModel::class.java)

        user = intent.getSerializableExtra("user") as User

        initRecyclerView()
    }

    private fun initRecyclerView(){
        mCardsAdapter = CardsAdapter()

        mBinding.userCardsRecyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.userCardsRecyclerView.adapter = mCardsAdapter

        mViewModel.cards.observe(this){
            mCardsAdapter.cards = it
            mBinding.userCardsRecyclerView.adapter = mCardsAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        mViewModel.setOnlineStatus()
        mViewModel.initSnapshotListener(user)
    }

    override fun onPause() {
        super.onPause()
        mViewModel.removeSnapshotListeners()
        mViewModel.setOfflineStatus()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            val backIntent = Intent(this, MainActivity::class.java)
            backIntent.putExtra("toUsers", true)
            backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(backIntent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}