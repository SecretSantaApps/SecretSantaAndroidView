package ru.kheynov.secretsanta.presentation.screens.room_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.secretsanta.databinding.FragmentCreateRoomBinding

@AndroidEntryPoint
class CreateRoomFragment : Fragment() {

    private lateinit var binding: FragmentCreateRoomBinding

    private val viewModel by viewModels<CreateRoomFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCreateRoomBinding.inflate(inflater, container, false)

        return binding.root
    }

}