package ru.kheynov.secretsanta.presentation.screens.room_details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.kheynov.secretsanta.databinding.UsersItemLayoutBinding
import ru.kheynov.secretsanta.domain.entities.UserInfo

@SuppressLint("NotifyDataSetChanged")

class RoomDetailsUsersListAdapter(
    private val onKickUserClick: (UserInfo) -> Unit,
    private val onUserLeaveClick: () -> Unit,
) : RecyclerView.Adapter<RoomDetailsUsersListAdapter.ViewHolder>() {
    private var ownerId: String = ""
    private var isAdmin: Boolean = false
    
    private var usersList: List<UserInfo> = emptyList()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            UsersItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(usersList[position])
    }
    
    override fun getItemCount(): Int = usersList.size
    
    fun updateList(list: List<UserInfo>) {
        usersList = list
        notifyDataSetChanged()
    }
    
    fun setOwnerId(id: String) {
        ownerId = id
        notifyDataSetChanged()
    }
    
    fun enableBlocking() {
        isAdmin = true
        notifyDataSetChanged()
    }
    
    inner class ViewHolder(private val binding: UsersItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserInfo) {
            binding.apply {
                userName.text = user.username
                leaveUserButton.visibility = View.GONE
                kickUserButton.visibility = if (isAdmin) View.VISIBLE else View.GONE
                kickUserButton.setOnClickListener { onKickUserClick(user) }
                if (user.userId == ownerId) {
                    kickUserButton.visibility = View.GONE
                    leaveUserButton.visibility = View.VISIBLE
                    leaveUserButton.setOnClickListener { onUserLeaveClick() }
                }
            }
        }
    }
}