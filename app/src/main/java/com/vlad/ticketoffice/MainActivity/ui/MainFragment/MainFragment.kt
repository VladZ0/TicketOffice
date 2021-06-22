package com.vlad.ticketoffice.MainActivity.ui.MainFragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.vlad.ticketoffice.Utils
import com.vlad.ticketoffice.MainActivity.adapters.RacesAdapter
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.RaceAddActivity.RaceAddActivity
import com.vlad.ticketoffice.TicketsActivity.TicketsActivity
import com.vlad.ticketoffice.databinding.FragmentMainBinding
import com.vlad.ticketoffice.model.Race
import com.vlad.ticketoffice.model.User
import java.text.SimpleDateFormat
import java.util.*

class MainFragment: Fragment() {
    private val mAuth = FirebaseAuth.getInstance()
    private var currentRace: Race? = null
    private lateinit var mBinding: FragmentMainBinding
    private lateinit var mRacesAdapter: RacesAdapter
    private lateinit var mViewModel: MainFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        mBinding = FragmentMainBinding.bind(root)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(mAuth.currentUser == null){
            mBinding.mainFragmentBtn.visibility = View.GONE
        }

        initViewModelWithRecyclerView()

        mBinding.imgSearchBtn.setOnClickListener {
            val races = mViewModel.races.value
            mRacesAdapter.races = races?.filter {
                it.route.toLowerCase(Locale.getDefault())
                    .contains(mBinding.etSearch.text.trim().toString().toLowerCase(Locale.getDefault()))
                        || it.departureDate.toLowerCase(Locale.getDefault())
                                .contains(mBinding.etSearch.text.trim().toString().toLowerCase(Locale.getDefault()))
            } as MutableList<Race>?
        }
    }

    override fun onStart() {
        super.onStart()
        mViewModel.initSnapshotListeners()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.removeSnapshotListeners()
    }

    private fun initRecyclerView(isAdmin: Boolean){

        if (mAuth.currentUser != null) {
            mRacesAdapter.raceItemClickListener = object : RacesAdapter.OnRaceItemClickListener {
                override fun onRaceItemClick(v: View?, position: Int) {
                    val intent = Intent(requireActivity(), TicketsActivity::class.java)
                    currentRace = mViewModel.races.value?.get(position)
                    if(Date().before(SimpleDateFormat("dd.MM.yyyy").parse(currentRace?.departureDate))
                        || isAdmin) {
                    intent.putExtra(Utils.CURRENT_RACE, currentRace)
                    startActivity(intent)
                    }
                    else{
                        Toast.makeText(requireContext(), "Бронювння білетів на цей рейс завершено.",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }

            if (isAdmin) {
                mBinding.mainFragmentBtn.setOnClickListener {
                    startActivity(Intent(requireActivity(), RaceAddActivity::class.java))
                }

                mRacesAdapter.raceItemLongClickListener =
                    object : RacesAdapter.OnRaceItemLongClickListener {
                        override fun onRaceItemLongClick(v: View?, position: Int) {
                            currentRace = mViewModel.races.value?.get(position)
                            v?.showContextMenu()
                        }
                    }

                registerForContextMenu(mBinding.racesRecyclerView)
            } else {
                unregisterForContextMenu(mBinding.racesRecyclerView)
                mBinding.mainFragmentBtn.visibility = View.GONE
            }
        }
    }

    private fun initViewModelWithRecyclerView(){
        mViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)

        mRacesAdapter = RacesAdapter()
        mBinding.racesRecyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.racesRecyclerView.adapter = mRacesAdapter

        mViewModel.races.observe(viewLifecycleOwner){
            mRacesAdapter.races = it
        }

        initRecyclerView(arguments?.getSerializable("ROLE") as User.Role == User.Role.ADMIN)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val inflater = activity?.menuInflater

        inflater?.inflate(R.menu.races_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.race_edit -> {
                val intent = Intent(requireActivity(), RaceAddActivity::class.java)
                intent.putExtra(Utils.CURRENT_RACE, currentRace)
                intent.putExtra(Utils.IS_EDITING, true)
                startActivity(intent)

                return true
            }

            R.id.race_delete -> {
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Видалення рейса")
                    .setMessage("Ви справді бажаєте видлаити даний рейс")
                    .setPositiveButton("Видалити"
                    ) { dialog, _ ->
                        currentRace?.let { mViewModel.deleteRace(it)
                            Toast.makeText(context, "Рейс видалений!", Toast.LENGTH_SHORT)
                                .show()
                            dialog?.cancel()
                            return@setPositiveButton
                        }

                        Toast.makeText(context, "Помилка при видаленні рейса!", Toast.LENGTH_SHORT)
                            .show()
                        dialog?.cancel()
                    }
                    .setNegativeButton("Скасувати"
                    ) { dialog, _ -> dialog?.cancel() }
                    .create()

                dialog.show()
                return true
            }
        }

        return super.onContextItemSelected(item)
    }
}