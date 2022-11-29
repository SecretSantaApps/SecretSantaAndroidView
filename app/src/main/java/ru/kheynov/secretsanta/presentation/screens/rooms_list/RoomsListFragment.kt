package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        viewModel.loadData()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.apply {
                        roomsListProgressBar.visibility =
                            if (state == RoomsListViewModel.State.Loading) View.VISIBLE
                            else View.GONE
                        roomsListText.visibility =
                            if (state is RoomsListViewModel.State.Loaded) View.VISIBLE
                            else View.GONE
                        roomsListText.text =
                            if (state is RoomsListViewModel.State.Loaded) "Username: ${state.username}"
                            else ""
                    }
                }
            }
        }
    }
}