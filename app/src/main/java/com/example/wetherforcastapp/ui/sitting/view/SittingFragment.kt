package com.example.wetherforcastapp.ui.sitting.view

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.navigation.fragment.findNavController
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wetherforcastapp.R

import com.example.wetherforcastapp.databinding.FragmentSittingBinding

import java.util.Locale


class SittingFragment : Fragment() {
    private lateinit var binding: FragmentSittingBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    companion object {
        fun newInstance() = SittingFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSittingBinding.inflate(inflater, container, false)


        sharedPreferences = requireContext().getSharedPreferences("R3-pref", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        loadPreferences()
        setupLanguage()
        setUpTemperature()
        setUpWind()
        setUpMap()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun setUpMap() {
        binding.location.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.map -> {
                    savePreference("map", "m")
                    findNavController().navigate(R.id.mapsFragment)
                }
                R.id.gps -> {
                    savePreference("map", "g")
                }
            }
        }
    }
    private fun setUpTemperature() {
        binding.temperature.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.celsius -> {
                    savePreference("temperature", "c")
                }
                R.id.kelvin -> {
                    savePreference("temperature", "k")
                }
                R.id.fahrenheit -> {
                    savePreference("temperature", "f")
                }
            }
        }
    }
    private fun setUpWind() {
        binding.windSpeed.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.meter_Sec -> {
                    savePreference("wind", "s")
                }
                R.id.mile_Hour -> {
                    savePreference("wind", "h")
                }
            }
        }
    }
    private fun setupLanguage() {
        // Set the currently selected language in the UI
//        val currentLang = sharedPreferences.getString("LANG", "en")
//        when (currentLang) {
//            "en" -> binding.radioEnglish.isChecked = true
//            "ar" -> binding.radioArabic.isChecked = true
//        }

        // Update SharedPreferences when language is changed
        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioEnglish -> {

//                    editor.putString("LANG", "en").apply()
                    savePreference("LANG", "en")
                    setLocale(requireContext(),"en")
                }
                R.id.radioArabic -> {
//                    editor.putString("LANG", "ar").apply()  // Save and apply changes
                    savePreference("LANG", "ar")
                    setLocale(requireContext(),"ar")
                }
            }
        }
    }
    private fun savePreference(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val sharedPreferences = context.getSharedPreferences("R3-pref", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("LANG", languageCode).apply()

        if (context is Activity) {
            context.recreate()
        }
    }
    private fun loadPreferences(){
        when (sharedPreferences.getString("LANG", "en")) {
            "en" -> binding.radioEnglish.isChecked = true
            "ar" -> binding.radioArabic.isChecked = true
        }
        when (sharedPreferences.getString("temperature", "c")) {
            "c" -> binding.celsius.isChecked = true
            "k" -> binding.kelvin.isChecked = true
            "f" -> binding.fahrenheit.isChecked = true
        }
        when (sharedPreferences.getString("wind", "s")) {
            "s" -> binding.meterSec.isChecked = true
            "h" -> binding.mileHour.isChecked = true
        }
        when (sharedPreferences.getString("map", "m")) {
            "m" -> binding.map.isChecked = true
            "g" -> binding.gps.isChecked = true
        }
        when (sharedPreferences.getString("alarm", "e")) {
            "m" -> binding.enable.isChecked = true
            "g" -> binding.disable.isChecked = true
        }
    }
}
