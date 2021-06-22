package com.vlad.ticketoffice.MainActivity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.databinding.ActivityMainBinding
import com.vlad.ticketoffice.model.User

class MainActivity : AppCompatActivity() {
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var mBinding: ActivityMainBinding
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var mViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Головна"
        initModelAndDrawer()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.setOnlineStatus()
        mViewModel.initUserSnapshotListener()
    }

    override fun onPause() {
        super.onPause()
        mViewModel.removeSnapshotListeners()
        mViewModel.setOfflineStatus()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(mAppBarConfiguration)
                || super.onSupportNavigateUp()
    }

    // When role is changed drawer header should be changed too

    private fun initModelAndDrawer(){
        val headerView = mBinding.navView.getHeaderView(0)

        val tvAdmin = headerView.findViewById<TextView>(R.id.tv_admin)
        val tvNotAuth = headerView.findViewById<TextView>(R.id.tv_not_auth)
        val tvName = headerView.findViewById<TextView>(R.id.tv_name)
        val tvEmail = headerView.findViewById<TextView>(R.id.tv_email)

        if(mAuth.currentUser == null){
            tvNotAuth.visibility = View.VISIBLE
            initDrawer(false)
        }
        else{
            tvNotAuth.visibility = View.GONE

            mViewModel.user.observe(this, {
                tvName.text = it.name
                tvEmail.text = it.email

                if(it.role == User.Role.ADMIN){
                    tvAdmin.visibility = View.VISIBLE
                    initDrawer(true)
                }
                else{
                    tvAdmin.visibility = View.GONE
                    initDrawer(false)
                }
            })
        }
    }

    // I configure Navigation according to user role (admin, user, or not auth user)

    private fun initDrawer(isAdmin: Boolean){
        val drawerLayout: DrawerLayout = mBinding.drawerLayout
        val navView: NavigationView = mBinding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bundle = Bundle()
        if(mViewModel.user.value != null) {
            bundle.putSerializable("ROLE", mViewModel.user.value?.role)
        }
        else{
            bundle.putSerializable("ROLE", User.Role.USER)
        }

        val onLoginMenuItemClickListener = MenuItem.OnMenuItemClickListener{
            mViewModel.setOfflineStatus()
            mAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            return@OnMenuItemClickListener true
        }

        if(isAdmin){
            navController.setGraph(R.navigation.admin_nav_graph, bundle)
            navView.menu.clear()
            menuInflater.inflate(R.menu.admin_drawer_menu, navView.menu)
            navView.menu.findItem(R.id.sign_out).setOnMenuItemClickListener(onLoginMenuItemClickListener)
        }
        else{
            navController.setGraph(R.navigation.user_nav_graph, bundle)
            navView.menu.clear()
            menuInflater.inflate(R.menu.user_drawer_menu, navView.menu)
            if(mAuth.currentUser != null){
                navView.menu.findItem(R.id.nav_login).setTitle(R.string.sign_out)
                navView.menu.findItem(R.id.nav_login).setOnMenuItemClickListener(onLoginMenuItemClickListener)
            }
        }

        if(mAuth.currentUser == null){
            navView.menu.findItem(R.id.nav_private_office).isVisible = false
        }

        mAppBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, mAppBarConfiguration)
        navView.setupWithNavController(navController)

        // When we returned from UserDataActivity we should navigate to nav_users

        if (intent.getBooleanExtra("toUsers", false)){
            navController.navigate(R.id.nav_users)
        }
    }
}