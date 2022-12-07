package ru.kheynov.secretsanta.presentation.screens.create_room.room_created

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentCreatedSuccessfullyBinding

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
            roomName.text = getString(R.string.room_name_placeholder, arguments?.getInt("roomName"))
            roomPassword.text =
                getString(R.string.room_password_placeholder, arguments?.getInt("password"))
            roomId.text = getString(R.string.room_id_placeholder, arguments?.getInt("roomId"))
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
        }
    }

}