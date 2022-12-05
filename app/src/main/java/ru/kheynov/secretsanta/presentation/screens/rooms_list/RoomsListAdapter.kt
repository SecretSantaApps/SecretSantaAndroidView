package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.kheynov.secretsanta.databinding.RoomItemLayoutBinding
import ru.kheynov.secretsanta.domain.entities.RoomItem

class RoomsListAdapter(
    private val onRoomClick: (RoomItem) -> Unit,
) : RecyclerView.Adapter<RoomsListAdapter.RoomsListViewHolder>() {
    private val rooms: MutableList<RoomItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomsListViewHolder {
        val binding =
            RoomItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomsListViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    fun updateRoomsList(newRooms: List<RoomItem>) {
        rooms.clear()
        rooms.addAll(newRooms)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = rooms.size

    inner class RoomsListViewHolder(
        private val binding: RoomItemLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(room: RoomItem) {
            binding.apply {
                roomName.text = room.roomName
                membersCount.text = room.membersCount
                gameStartedText.text = room.gameState
                room.gameStateColor?.also { gameStartedText.setTextColor(it) }
                with(room.date) {
                    if (!contains("null")) {
                        dateText.text = room.date
                        dateText.visibility = View.VISIBLE
                    } else
                        dateText.visibility = View.INVISIBLE
                }
                root.setOnClickListener { onRoomClick(room) }
            }
        }
    }
}