package ru.kheynov.secretsanta.presentation.screens.create_room

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentCreateRoomBinding
import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.presentation.screens.create_room.room_created.CreatedSuccessfullyFragment
import ru.kheynov.secretsanta.utils.dateFormatter
import java.time.LocalDate

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::updateUI)
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.date.collect {
                    binding.pickDeadlineDate.text = it?.run {
                        getString(
                            R.string.room_deadline_placeholder, it.format(dateFormatter).toString()
                        )
                    } ?: getString(
                        R.string.date_picker_hint
                    )
                }
            }
        }
        binding.apply {
            createRoomButton.setOnClickListener {
                val room = RoomDTO.Create(roomName = roomNameInput.text.toString(),
                    password = roomPasswordInput.text.toString(),
                    date = viewModel.date.value,
                    maxPrice = with(roomMaxPriceInput.text.toString()) {
                        if (isNullOrBlank()) null
                        else toInt()
                    })
                viewModel.createRoom(room)
            }

            pickDeadlineDate.setOnClickListener {
                showDatePicker()
            }
        }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(
            context!!, { _, year_picker, month_picker, day_picker ->
                Log.i(tag, "$day_picker $month_picker $year_picker")
                if (year_picker == year && month_picker == month && day_picker < day) {
                    viewModel.setDate(null)
                } else {
                    viewModel.setDate(LocalDate.of(year_picker, month_picker + 1, day_picker))
                }
            }, year, month, day
        )
        datePicker.show()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.actions.collect(::handleAction)
        }
        viewModel.clearDate()
    }

    private fun updateUI(state: CreateRoomFragmentViewModel.State) {
        binding.apply {
            createRoomButton.visibility =
                if (state is CreateRoomFragmentViewModel.State.Loading) View.GONE else View.VISIBLE
            roomNameInput.visibility = createRoomButton.visibility
            roomPasswordInput.visibility = createRoomButton.visibility
            roomMaxPriceInput.visibility = createRoomButton.visibility
            pickDeadlineDate.visibility = createRoomButton.visibility
            createRoomProgressBar.visibility =
                if (state is CreateRoomFragmentViewModel.State.Loading) View.VISIBLE else View.GONE
        }
    }

    private fun handleAction(action: CreateRoomFragmentViewModel.Action) {
        when (action) {
            is CreateRoomFragmentViewModel.Action.ShowError -> {
                Toast.makeText(
                    context, "Error: ${action.error}", Toast.LENGTH_SHORT
                ).show()
            }
            CreateRoomFragmentViewModel.Action.ShowSuccess -> {
                if (viewModel.state.value is CreateRoomFragmentViewModel.State.Loaded) {
                    val room = viewModel.state.value as CreateRoomFragmentViewModel.State.Loaded
                    val fragment = CreatedSuccessfullyFragment()
                    val args = Bundle().apply {
                        putString("roomName", room.room.name)
                        putString("password", room.room.password)
                        putString("maxPrice", room.room.maxPrice.toString())
                        putString("date", room.room.date?.format(dateFormatter).toString())
                        putString("roomId", room.room.id)
                    }
                    fragment.arguments = args
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, fragment)?.commit()
                        ?: Log.i(
                            "CreateRoomFragment",
                            "Unable to navigate"
                        )
                }
            }
        }
    }
}