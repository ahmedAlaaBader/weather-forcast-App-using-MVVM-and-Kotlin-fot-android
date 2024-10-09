package com.example.wetherforcastapp.ui.alarm.view

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherforcastapp.R
import com.example.wetherforcastapp.databinding.FragmentAlarmBinding
import com.example.wetherforcastapp.model.data.RepoImpl
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.LocalDataBaseImp
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import com.example.wetherforcastapp.model.data.network.IRemoteDataSourceImpl
import com.example.wetherforcastapp.ui.alarm.AlarmReceiver
import com.example.wetherforcastapp.ui.alarm.viewmodel.AlarmViewModel
import com.example.wetherforcastapp.ui.alarm.viewmodel.AlarmViewModelFactory
import com.example.wetherforcastapp.ui.favorite.view.FavWeatherListAdapter
import com.example.wetherforcastapp.ui.favorite.viewmodel.FavViewModel
import com.example.wetherforcastapp.ui.favorite.viewmodel.FavViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*

class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private var alarm = true
    private var alarmId = 0
    private lateinit var alarmAdapter: AlarmAdapter
    companion object {
        fun newInstance() = AlarmFragment()
    }

    private val viewModel: AlarmViewModel by viewModels {
        AlarmViewModelFactory(
            RepoImpl.getInstance(
                IRemoteDataSourceImpl.getInstance(),
                LocalDataBaseImp.getInstance(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmBinding.inflate(inflater,container,false)
        setupFloatingActionButton()
        observeUIState()
        setupRecyclerViews()
        return binding.root
    }
    private fun observeUIState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is UIState.Loading -> {
                        showLoadingState()
                    }
                    is UIState.Success<*> -> {
                        val entityAlarm = state.data as List<EntityAlarm>
                        Log.d("FavFragment", "Data received")
                        updateCurrentWeatherUI(entityAlarm)
                    }
                    is UIState.Failure -> {
                        Log.e("FavFragment", "Error: ${state.msg}")
                        showError(state.msg)
                    }
                    else -> {
                        Log.e("FavFragment", "Unexpected state")
                    }
                }
            }
        }
    }
    private fun updateCurrentWeatherUI(entityAlarm:  List<EntityAlarm>) {
        binding.progressBar3.visibility = View.GONE
        alarmAdapter.submitList(entityAlarm)
    }

    private fun showLoadingState() {
        binding.progressBar3.visibility = View.VISIBLE
    }

    private fun showError(exception: Throwable) {
        Toast.makeText(requireContext(), "Error: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
    }

    private fun setupRecyclerViews() {
        alarmAdapter = AlarmAdapter { id, time ->
            cancelAlarm(id, time)
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = alarmAdapter
    }
    private fun cancelAlarm(id:Int,time:String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete this alarm?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            viewModel.deleteAlarm(time)
            val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireContext(), AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)

            Toast.makeText(requireContext(), "Alarm Deleted Successfully!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun showExactAlarmPermissionDialog() {
        startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
    }
    @SuppressLint("DefaultLocale")
    private fun setAlarm(selectedDate: String, fromTime: String, alarmId: Int) {
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            showExactAlarmPermissionDialog()
            return
        }

        val calendar = Calendar.getInstance()
        val currentDate = Calendar.getInstance()
        val dateParts = selectedDate.split("/")
        val selectedDay = dateParts[0].toInt()
        val selectedMonth = dateParts[1].toInt() - 1
        val selectedYear = dateParts[2].toInt()

        calendar.set(selectedYear, selectedMonth, selectedDay)

        val fromTimeParts = fromTime.split(":")
        val fromHour = fromTimeParts[0].toInt()
        val fromMinute = fromTimeParts[1].substring(0, 2).toInt()
        val fromAmPm = fromTimeParts[1].substring(3)
        val hourAdjusted = if (fromAmPm == "PM" && fromHour != 12) fromHour + 12 else fromHour

        calendar.set(Calendar.HOUR_OF_DAY, hourAdjusted)
        calendar.set(Calendar.MINUTE, fromMinute)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis < currentDate.timeInMillis) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        val updatedDate = String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val entityAlarm = EntityAlarm(alarmId, updatedDate, fromTime, alarm)
        viewModel.insertalarm(entityAlarm)

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            Toast.makeText(requireContext(), "Alarm set for $fromTime on $updatedDate", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Permission to set exact alarms is not granted.", Toast.LENGTH_SHORT).show()
            showExactAlarmPermissionDialog()
        }
    }
    private fun showConfirmationDialog(selectedDate: String, selectedTime: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alarm, null)
        val dateTextView = dialogView.findViewById<TextView>(R.id.alarmDate)
        val timeTextView = dialogView.findViewById<TextView>(R.id.setTime)
        val confirmButton = dialogView.findViewById<Button>(R.id.saveButton)

        val radioAlarm = dialogView.findViewById<RadioButton>(R.id.radioAlarm)
        val radioNotification = dialogView.findViewById<RadioButton>(R.id.radioNotification)

        radioAlarm.setOnClickListener {
            alarm = false
            Toast.makeText(requireContext(), "Alarm selected", Toast.LENGTH_SHORT).show()
        }

        radioNotification.setOnClickListener {
            alarm = true
            Toast.makeText(requireContext(), "Notification selected", Toast.LENGTH_SHORT).show()
        }

        dateTextView.text = selectedDate
        timeTextView.text = selectedTime

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        confirmButton.setOnClickListener {
            alarmId++
            setAlarm(selectedDate, selectedTime, alarmId)
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun showTimePickerDialog(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                val amPm = if (hour >= 12) "PM" else "AM"
                val hourFormatted = if (hour % 12 == 0) 12 else hour % 12
                onTimeSelected(String.format("%02d:%02d $amPm", hourFormatted, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }
    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                onDateSelected(String.format("%02d/%02d/%04d", day, month + 1, year))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
    private fun setupFloatingActionButton() {
        binding.fab.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                showTimePickerDialog { selectedTime ->
                    showConfirmationDialog(selectedDate, selectedTime)
                }
            }
        }
    }
}