package com.gituser.paging.view.users.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gituser.paging.R

class GitUserLoadAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<GitUserLoadAdapter.LoadStateViewHolder>() {

    class LoadStateViewHolder(view: View, retry: () -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val btnRetry: Button = view.findViewById(R.id.btnRetry)
        private val tvErrorMessage: TextView = view.findViewById(R.id.tvErrorMessage)
        private val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)

        init {
            btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bindState(loadState: LoadState) {

            if (loadState is LoadState.Error) {
                tvErrorMessage.error = loadState.error.localizedMessage
            }
            btnRetry.isVisible = loadState is LoadState.Error
            progressBar.isVisible = loadState is LoadState.Loading
            tvErrorMessage.isVisible = loadState is LoadState.Error
        }

    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bindState(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {

        return LoadStateViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_load_state_footer, parent, false),
            retry
        )
    }
}


