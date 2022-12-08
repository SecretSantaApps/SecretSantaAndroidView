package ru.kheynov.secretsanta.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.data.KeyValueStorage
import ru.kheynov.secretsanta.databinding.ActivityMainBinding
import ru.kheynov.secretsanta.presentation.screens.create_room.CreateRoomFragment
import ru.kheynov.secretsanta.presentation.screens.join_room.JoinRoomFragment
import ru.kheynov.secretsanta.presentation.screens.profile.ProfileFragment
import ru.kheynov.secretsanta.presentation.screens.rooms_list.RoomsListFragment
import ru.kheynov.secretsanta.utils.navigateToLoginScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var keyValueStorage: KeyValueStorage

    private val viewModel by viewModels<MainActivityViewModel>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntent(intent)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.room.collect(::navigateToRoom)
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.action.collect(::handleAction)
            }
        }

        binding.apply {
            listButton.setOnClickListener { viewModel.navigateToRoomsList() }
            profileButton.setOnClickListener { viewModel.navigateToProfile() }
            createRoomFab.setOnClickListener { viewModel.navigateToCreateRoom() }
        }

        onBackPressedDispatcher.addCallback(this /* lifecycle owner */,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (supportFragmentManager.backStackEntryCount == 1) finish()
                    else supportFragmentManager.popBackStack()
                }
            })
    }

    private fun handleAction(action: MainActivityViewModel.Action) {
        when (action) {
            MainActivityViewModel.Action.NavigateToCreateRoom -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CreateRoomFragment()).addToBackStack(null)
                    .commit()
            }
            MainActivityViewModel.Action.NavigateToProfile -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).addToBackStack(null)
                    .commit()
            }
            MainActivityViewModel.Action.NavigateToRoomsList -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RoomsListFragment()).addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun navigateToRoom(room: MainActivityViewModel.Room) {
        binding.apply {
            listButton.setColorFilter(
                getColor(
                    if (room is MainActivityViewModel.Room.RoomsList) R.color.colorAccent
                    else R.color.colorPrimary
                )
            )
            profileButton.setColorFilter(
                getColor(
                    if (room is MainActivityViewModel.Room.Profile) R.color.colorAccent
                    else R.color.colorPrimary
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (firebaseAuth.currentUser == null || !keyValueStorage.isAuthorized) navigateToLoginScreen(
            this
        ) //navigate to login screen if user not logged in
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkAction: String? = intent?.action
        val appLinkData: Uri? = intent?.data
        handleRoomJoin(appLinkAction, appLinkData)
    }

    private fun handleRoomJoin(appLinkAction: String?, appLinkData: Uri?) {
        if (appLinkAction == Intent.ACTION_VIEW && appLinkData != null) {
            val roomId = appLinkData.getQueryParameter("roomId")
            val roomPassword = appLinkData.getQueryParameter("pass")
            Log.d("MainActivity", "roomId: $roomId, pass: $roomPassword")
            if (roomId != null && roomPassword != null) {
                val fragment = JoinRoomFragment()
                val args = Bundle().apply {
                    putString("roomId", roomId)
                    putString("pass", roomPassword)
                }
                fragment.arguments = args
                lifecycleScope.launch{
                    delay(10) //delay to wait main screen to initialize
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }
            }
        }
    }
}


