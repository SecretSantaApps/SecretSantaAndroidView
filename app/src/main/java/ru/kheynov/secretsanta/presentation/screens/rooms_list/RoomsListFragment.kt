package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.kheynov.secretsanta.databinding.FragmentRoomsListBinding

class RoomsListFragment : Fragment() {
    private lateinit var binding: FragmentRoomsListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRoomsListBinding.inflate(inflater, container, false)
        return binding.root
    }

}