package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.secretsanta.databinding.FragmentRoomsListBinding

@AndroidEntryPoint
class RoomsListFragment : Fragment() {
    private lateinit var binding: FragmentRoomsListBinding

    private val viewModel by viewModels<RoomsListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRoomsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadButton.setOnClickListener {
            viewModel.loadData()
        }
    }
}