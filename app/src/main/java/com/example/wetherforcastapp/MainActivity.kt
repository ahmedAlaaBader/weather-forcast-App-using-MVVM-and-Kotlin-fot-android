package com.example.wetherforcastapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this.getSharedPreferences("R3-pref", MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("LANG", "en")
        if (languageCode != null) {
            setLocale(languageCode)
        }

        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawer_Layout)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.menu)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navigationView, navController)
    }

    override fun onBackPressed() {
        // Get the current destination from the NavController
        val currentDestination = navController.currentDestination
        // Check if the current destination is HomeFragment
        if (currentDestination?.id == R.id.homeFragment) { // Replace R.id.homeFragment with your actual fragment ID
            finish() // Close the app
        } else {
            super.onBackPressed() // Navigate back in the navigation stack
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        sharedPreferences.edit().putString("LANG", languageCode).apply()

        // Recreate activity only if the language has changed
        if (resources.configuration.locales.get(0).language != languageCode) {
            recreate()
        }
    }
}
