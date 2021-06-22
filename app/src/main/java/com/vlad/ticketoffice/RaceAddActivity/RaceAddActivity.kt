package com.vlad.ticketoffice.RaceAddActivity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.Utils
import com.vlad.ticketoffice.databinding.ActivityRaceAddBinding
import com.vlad.ticketoffice.model.Race
import java.text.SimpleDateFormat
import java.util.*

class RaceAddActivity : AppCompatActivity() {
    private var menuItemClickCount = 0
    private var currentRace: Race? = null
    private lateinit var mBinding: ActivityRaceAddBinding
    private lateinit var mViewModel:RaceAddActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRaceAddBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setTextWatchers()

        mViewModel = ViewModelProvider(this, RaceAddActivityVMFactory(application))
            .get(RaceAddActivityViewModel::class.java)

        if(intent != null){
            currentRace = intent.getSerializableExtra(Utils.CURRENT_RACE) as Race?
            mViewModel.isEditing = intent.getBooleanExtra(Utils.IS_EDITING, false)
        }

        if(mViewModel.isEditing){
            fillFieldsForEditing()
            initRefreshButton()
        }
        else{
            mBinding.raceRefreshBtn.visibility = View.GONE
        }
    }

    private fun initRefreshButton(){
        if(currentRace != null) {
            if (Date().before(
                    SimpleDateFormat("dd.MM.yyyy")
                        .parse(currentRace?.departureDate)
                )
            ) {
                mBinding.raceRefreshBtn.visibility = View.GONE
            }

            mBinding.raceRefreshBtn.setOnClickListener {
                if (validateFields()) {
                    currentRace?.let { it1 -> Log.e("TAG", currentRace?.id!!)
                        mViewModel.deleteTickets(it1) }
                    Toast.makeText(this, "Дані про квитки очищені!", Toast.LENGTH_SHORT)
                        .show()
                    mBinding.raceRefreshBtn.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_record_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    // Watchers to autocomplete date and time edittext fields with "." and ":"

    private fun setTextWatchers(){
        mBinding.etDepartureDate.doOnTextChanged { text, _, before, count ->
            if(before < count){
                if(text.toString().trim().length == 2){
                    mBinding.etDepartureDate.append(".")
                }
                else if(text.toString().trim().length == 5){
                    mBinding.etDepartureDate.append(".")
                }
            }
        }

        mBinding.etDepartureTime.doOnTextChanged { text, _, before, count ->
            if(before < count){
                if(text.toString().trim().length == 2){
                    mBinding.etDepartureTime.append(":")
                }
            }
        }

        mBinding.etArrivalTime.doOnTextChanged { text, _, before, count ->
            if(before < count){
                if(text.toString().trim().length == 2){
                    mBinding.etArrivalTime.append(":")
                }
            }
        }
    }

    private fun fillFieldsForEditing(){
        Log.e("TAG", "in Filling")
        mBinding.etRoute.setText(currentRace?.route)
        mBinding.etRace.setText(currentRace?.name)
        mBinding.etDepartureDate.setText(currentRace?.departureDate)
        mBinding.etDepartureTime.setText(currentRace?.departureTime)
        mBinding.etArrivalTime.setText(currentRace?.arrivalTime)
        currentRace?.ordinaryTicketPrice?.let {
            mBinding.etOrdinaryTicketPrice.setText(it.toString())
        }
        currentRace?.expensiveTicketPrice?.let {
            mBinding.etExpensiveTicketPrice.setText(it.toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_record -> {
                //menuItemClick prevents double click on save record menu item
                //else we'll be able to save race a few times
                menuItemClickCount++
                if (menuItemClickCount < 2) {
                    if (validateFields() && !mViewModel.isEditing) {
                        mViewModel.isEditing = true

                        val race = Race("", mBinding.etRace.text.trim().toString(), mBinding.etRoute.text.trim().toString(),
                            mBinding.etDepartureDate.text.trim().toString(), mBinding.etDepartureTime.text.trim().toString(),
                            mBinding.etArrivalTime.text.trim().toString(),
                            mBinding.etOrdinaryTicketPrice.text.trim().toString().toInt(),
                            mBinding.etExpensiveTicketPrice.text.trim().toString().toInt())

                        mViewModel.addRace(race)

                        return true
                    }
                    if (validateFields() && mViewModel.isEditing) {
                        val race = Race(currentRace?.id!!, mBinding.etRace.text.trim().toString(), mBinding.etRoute.text.trim().toString(),
                            mBinding.etDepartureDate.text.trim().toString(), mBinding.etDepartureTime.text.trim().toString(),
                            mBinding.etArrivalTime.text.trim().toString(),
                            mBinding.etOrdinaryTicketPrice.text.trim().toString().toInt(),
                            mBinding.etExpensiveTicketPrice.text.trim().toString().toInt())

                        mViewModel.editRace(race)

                        return true
                    }
                }
            }
        }

        return false
    }

    private fun validateFields(): Boolean{
        val t = menuItemClickCount
        menuItemClickCount = 0

        when {
            mBinding.etRoute.text.trim().toString().isEmpty() -> {
                Toast.makeText(this, "Введіть маршрут!", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            !mBinding.etRoute.text.trim().toString()
                .matches("([а-яА-Яa-zA-Z]*)(( - )|( -)|(- )|(-))([а-яА-Яa-zA-Z]*)".toRegex()) -> {
                Toast.makeText(this,
                    "Ви невірно ввели маршрут, правильний формат: пункт відправлення - пункт прибуття!",
                    Toast.LENGTH_LONG)
                    .show()
                return false
            }
            mBinding.etRace.text.trim().toString().isEmpty() -> {
                Toast.makeText(this, "Введіть назву рейса!", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            mBinding.etDepartureDate.text.trim().toString().isEmpty() -> {
                Toast.makeText(this, "Введіть дату даного рейса!", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            !Utils.validateDate(mBinding.etDepartureDate.text.trim().toString()) -> {
                Toast.makeText(this, "Дата введена невірно, правильний формат: dd.MM.yyyy!",
                    Toast.LENGTH_LONG)
                    .show()
                return false
            }
            mBinding.etDepartureTime.text.trim().toString().isEmpty() -> {
                Toast.makeText(this, "Введіть час вильоту!", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            !Utils.validateTime(mBinding.etDepartureTime.text.trim().toString()) -> {
                Toast.makeText(this, "Ви невірно ввели час відправлення, правильний формат: hh:mm!",
                    Toast.LENGTH_LONG)
                    .show()
                return false
            }
            mBinding.etArrivalTime.text.trim().toString().isEmpty() -> {
                Toast.makeText(this, "Введіть час прибуття!", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            !Utils.validateTime(mBinding.etArrivalTime.text.trim().toString()) -> {
                Toast.makeText(this, "Ви невірно ввели час прибуття, правильний формат: hh:mm!",
                    Toast.LENGTH_LONG)
                    .show()
                return false
            }
            mBinding.etOrdinaryTicketPrice.text.trim().toString().isEmpty() -> {
                Toast.makeText(this, "Введіть ціну звичайного білета!", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            mBinding.etExpensiveTicketPrice.text.trim().toString().isEmpty() -> {
                Toast.makeText(this, "Введіть ціну дорогого білета!", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            mBinding.etExpensiveTicketPrice.text.trim().toString().toDouble() <=
                    mBinding.etOrdinaryTicketPrice.text.trim().toString().toDouble() -> {
                Toast.makeText(this,
                    "Ціна економ класа не може бути більшою, ніж ціна бізнес класа," +
                            " ми тут не благодійністю займаємось!!!",
                    Toast.LENGTH_LONG)
                    .show()
                return false
            }
            else -> {
                menuItemClickCount = t
                return true
            }
        }

    }

    override fun onStart() {
        super.onStart()
        mViewModel.setOnlineStatus()
    }

    override fun onPause() {
        super.onPause()
        mViewModel.setOfflineStatus()
    }
}