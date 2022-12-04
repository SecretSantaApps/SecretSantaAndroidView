package ru.kheynov.secretsanta.presentation.screens.create_room.room_created

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentCreatedSuccessfullyBinding

@AndroidEntryPoint
class CreatedSuccessfullyFragment : Fragment() {
    private lateinit var binding: FragmentCreatedSuccessfullyBinding

    private val args: CreatedSuccessfullyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatedSuccessfullyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createAnotherRoomButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.apply {
            roomName.text = getString(R.string.room_name_placeholder, args.roomName)
            roomPassword.text = getString(R.string.room_password_placeholder, args.roomPassword)
            args.maxPrice.run {
                if (this.contains("0") || this.contains("null")) {
                    roomMaxPrice.visibility = View.GONE
                } else {
                    roomMaxPrice.text = getString(R.string.room_max_price_placeholder, this)
                }
            }
            args.date.run {
                if (this.contains("null")) {
                    roomDate.visibility = View.GONE
                } else {
                    roomDate.text = getString(R.string.room_deadline_placeholder, this)
                }
            }
        }
    }

}