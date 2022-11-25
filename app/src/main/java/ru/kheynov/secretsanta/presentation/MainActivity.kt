package ru.kheynov.secretsanta.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.ActivityMainBinding
import ru.kheynov.secretsanta.utils.navigateToLoginScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navController = this.findNavController(R.id.nav_host_fragment)

        val navView = binding.bottomNavView
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        if (firebaseAuth.currentUser == null) navigateToLoginScreen(this) //navigate to login
    // screen if user not logged in
    }


}