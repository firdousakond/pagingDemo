package com.gituser.paging.view.users.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gituser.paging.R
import com.gituser.paging.data.model.UserData
import com.gituser.paging.utils.DiffUtilCallBack
import com.gituser.paging.view.users.listeners.UserItemClickListener

class GitUserPagerAdapter(private val userItemClickListener: UserItemClickListener) :
    PagingDataAdapter<UserData, GitUserPagerAdapter.GiphyViewHolder>(DiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiphyViewHolder {

        return GiphyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GiphyViewHolder, position: Int) {
        getItem(position)?.let { holder.bindPost(it) }
    }

    inner class GiphyViewHolder(private val view: View) :
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

            view.setOnClickListener {
                userItemClickListener.onGitUserItemClick(userData)
            }
            ivFavourite.setOnClickListener {
                userItemClickListener.onFavouriteClick(userData)
            }

        }
    }

    fun updateFavouriteUsers(favUsers: List<UserData>) {

        for (i in snapshot().indices) {
            var isFavourite = false
            favUsers.forEach { fav ->
                if(snapshot()[i]?.id == fav.id) {
                   isFavourite = true
                }
            }
            if(isFavourite){
                snapshot()[i]?.isFavourite = 1
            }else{
                snapshot()[i]?.isFavourite = 0
            }
        }
        notifyDataSetChanged()
    }
}