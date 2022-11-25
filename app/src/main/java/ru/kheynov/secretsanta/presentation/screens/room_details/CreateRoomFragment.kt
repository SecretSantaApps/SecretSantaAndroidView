package ru.kheynov.secretsanta.presentation.screens.room_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.kheynov.secretsanta.databinding.FragmentCreateRoomBinding

class CreateRoomFragment : Fragment() {
    private lateinit var binding: FragmentCreateRoomBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCreateRoomBinding.inflate(inflater, container, false)

        return binding.root
    }

}