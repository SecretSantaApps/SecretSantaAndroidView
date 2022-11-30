package ru.kheynov.secretsanta.presentation.screens.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.secretsanta.databinding.FragmentProfileBinding
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*binding.signOutButton.setOnClickListener {
            activity?.applicationContext?.let { context ->
                AuthUI.getInstance().signOut(context).addOnCompleteListener {
                    navigateToLoginScreen(context)
                }
            }
        }
        binding.getTokenButton.setOnClickListener {
            firebaseAuth.currentUser?.getIdToken(false)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken: String? = task.result?.token
                    Log.i("TOKEN", idToken.toString())
                    val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as
                            ClipboardManager

                    val clip: ClipData = ClipData.newPlainText(null, "```${idToken.toString()}```")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(activity, "Token copied to clipboard!", Toast.LENGTH_SHORT)
                        .show()
                    // Send token to your backend via HTTPS
                    // ...
                } else {
                    // Handle error -> task.getException();
                }
            }
        }*/
    }

}