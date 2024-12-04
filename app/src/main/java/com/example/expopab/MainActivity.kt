package com.example.expopab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expopab.databinding.ActivityMainBinding
import com.example.expopab.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add HomeFragment if this is first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, HomeFragment())
                .commit()
        }
    }
}