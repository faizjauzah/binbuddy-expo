package com.example.expopab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expopab.databinding.ActivityMainBinding
import com.example.expopab.ui.home.AccountFragment
import com.example.expopab.ui.home.EducationFragment
import com.example.expopab.ui.home.HomeFragment
import com.example.expopab.ui.home.TrackingFragment
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_education -> EducationFragment()
                R.id.navigation_tracking -> TrackingFragment()
                R.id.navigation_account -> AccountFragment()
                else -> HomeFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment as Fragment)
                .commit()
            true
        }
    }
}