package com.example.expopab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.expopab.databinding.ActivityMainBinding
import com.example.expopab.ui.home.AccountFragment
import com.example.expopab.ui.home.EducationFragment
import com.example.expopab.ui.home.HomeFragment
import com.example.expopab.ui.home.TrackingFragment
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize and setup toolbar
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Set initial toolbar title
        supportActionBar?.title = "Home"

        setupBottomNavigation()

        // Add HomeFragment if this is first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, HomeFragment())
                .commit()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_home -> {
                    supportActionBar?.title = "Home"
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    HomeFragment()
                }
                R.id.navigation_education -> {
                    supportActionBar?.title = "Education"
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    EducationFragment()
                }
                R.id.navigation_tracking -> {
                    supportActionBar?.title = "Tracking"
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    TrackingFragment()
                }
                R.id.navigation_account -> {
                    supportActionBar?.title = "Account"
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    AccountFragment()
                }
                else -> {
                    supportActionBar?.title = "Home"
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    HomeFragment()
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment as Fragment)
                .commit()
            true
        }
    }

    // Handle Up button clicks
    override fun onSupportNavigateUp(): Boolean {
        // Navigate to Home fragment
        binding.bottomNav.selectedItemId = R.id.navigation_home
        return true
    }
}