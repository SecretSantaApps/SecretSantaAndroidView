package ru.kheynov.secretsanta.presentation.screens.create_room.room_created

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.kheynov.secretsanta.databinding.FragmentCreatedSuccessfullyBinding


class CreatedSuccessfully : Fragment() {
    private lateinit var binding: FragmentCreatedSuccessfullyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatedSuccessfullyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createAnotherRoomButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

}