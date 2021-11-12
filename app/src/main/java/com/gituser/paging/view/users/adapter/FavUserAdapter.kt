package com.gituser.paging.view.users.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gituser.paging.R
import com.gituser.paging.data.model.UserData
import com.gituser.paging.view.users.listeners.UserItemClickListener

class FavUserAdapter(private val userItemClickListener: UserItemClickListener) :
    ListAdapter<UserData, FavUserAdapter.FavViewHolder>(UserDiffCallback) {

    private var favUsers: MutableList<UserData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        return FavViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        holder.bindPost(favUsers[position])
    }

    inner class FavViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        private val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
        private val tvName: TextView = view.findViewById(R.id.tvName)
        private val ivFavourite: ImageView = view.findViewById(R.id.ivFavourite)

        fun bindPost(userData: UserData) {
            if (userData.isFavourite == 1) {
                ivFavourite.setImageResource(R.drawable.ic_favorite_actived)
            } else {
                ivFavourite.setImageResource(R.drawable.ic_favorite)
            }
            tvName.text = userData.login
            Glide.with(view.context)
                .load(userData.avatar_url)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_demo_avatar)
                .into(ivAvatar)
            ivFavourite.setOnClickListener {
                userItemClickListener.onFavouriteClick(userData)
            }
            view.setOnClickListener {
                userItemClickListener.onGitUserItemClick(userData)
            }

        }
    }

    override fun getItemCount(): Int {
        return favUsers.size
    }

    fun updateUsers(users: List<UserData>) {
        favUsers.addAll(users)
        notifyItemInserted(favUsers.size)
    }

    fun updateFavourite(userData: UserData) {

        for (i in 0..favUsers.size) {
            if (favUsers[i].id == userData.id) {
                favUsers[i].isFavourite = userData.isFavourite
                notifyItemChanged(i)
                break
            }
        }
    }
}