package com.gituser.paging.view.users.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gituser.paging.R
import com.gituser.paging.data.model.UserData
import com.gituser.paging.view.users.viewmodel.GitUserViewModel
import kotlinx.android.synthetic.main.fragment_user_details.*
import org.koin.android.viewmodel.ext.android.viewModel


class UserDetailsFragment : Fragment() {

    private val viewModel: GitUserViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user_details, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userData: UserData? = null
        arguments?.let {
            userData = it.getParcelable(USER_DATA)
            setUIData(userData)
        }
        ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        ivFavourite.setOnClickListener {
            if (userData != null) {
                if (userData?.isFavourite == 1) {
                    userData?.isFavourite = 0
                } else {
                    userData?.isFavourite = 1
                }
                viewModel.updateUserFavourite(userData!!)
                updateFavourite(userData)
            }
        }
    }

    private fun setUIData(userData: UserData?) {
        Log.i("$TAG:", "UserData: ${userData.toString()}")
        Glide.with(requireContext())
            .load(userData?.avatar_url)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_demo_avatar)
            .into(ivAvatar)
        tvName.text = userData?.login
        tvUserType.text = userData?.type
        tvUrl.text = userData?.url
        updateFavourite(userData)
    }

    private fun updateFavourite(userData: UserData?) {
        if (userData?.isFavourite == 1) {
            ivFavourite.setImageResource(R.drawable.ic_favorite_actived)
        } else {
            ivFavourite.setImageResource(R.drawable.ic_favorite)
        }
    }

    companion object {
        private val TAG = UserDetailsFragment::class.java.simpleName
        const val USER_DATA = "userData"
    }
}