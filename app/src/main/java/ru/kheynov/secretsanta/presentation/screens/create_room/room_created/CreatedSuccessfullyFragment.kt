package ru.kheynov.secretsanta.presentation.screens.create_room.room_created

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentCreatedSuccessfullyBinding
import ru.kheynov.secretsanta.utils.generateInviteLink

@AndroidEntryPoint
class CreatedSuccessfullyFragment : Fragment() {
    private lateinit var binding: FragmentCreatedSuccessfullyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatedSuccessfullyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createAnotherRoomButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.apply {
            roomName.text =
                getString(R.string.room_name_placeholder, arguments?.getString("roomName"))
            roomPassword.text =
                getString(R.string.room_password_placeholder, arguments?.getString("password"))
            roomId.text = getString(R.string.room_id_placeholder, arguments?.getString("roomId"))
            arguments?.getString("maxPrice")?.run {
                if (contains("0") || contains("null")) {
                    roomMaxPrice.visibility = View.GONE
                } else {
                    roomMaxPrice.text = getString(R.string.room_max_price_placeholder, this)
                }
            }
            arguments?.getString("date")?.run {
                if (contains("null")) {
                    roomDate.visibility = View.GONE
                } else {
                    roomDate.text = getString(R.string.room_deadline_placeholder, this)
                }
            }
            copyLinkButton.setOnClickListener {
                val clipboard =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val roomId = arguments?.getString("roomId") ?: return@setOnClickListener
                val password = arguments?.getString("password") ?: return@setOnClickListener
                val clip: ClipData = ClipData.newPlainText(
                    "link", generateInviteLink(
                        roomId = roomId,
                        password = password
                    )
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(activity, "Copied!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}