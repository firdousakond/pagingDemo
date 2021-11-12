package com.gituser.paging.view.users.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gituser.paging.R
import com.gituser.paging.data.model.UserData
import com.gituser.paging.utils.EventObserver
import com.gituser.paging.utils.hide
import com.gituser.paging.utils.show
import com.gituser.paging.view.users.adapter.FavUserAdapter
import com.gituser.paging.view.users.listeners.UserItemClickListener
import com.gituser.paging.view.users.viewmodel.GitUserViewModel
import kotlinx.android.synthetic.main.fragment_favourite_user.*
import org.koin.android.viewmodel.ext.android.viewModel


class FavouriteUsersFragment : Fragment(), UserItemClickListener {

    private val viewModel: GitUserViewModel by viewModel()
    private var favUserAdapter: FavUserAdapter? = null

    private fun initializeList() {
        favUserAdapter = FavUserAdapter(this)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        rvFavUsers.apply {
            layoutManager = linearLayoutManager
            adapter = favUserAdapter
        }
        viewModel.getFavouriteUsers()
    }

    private fun initObserver() {
        viewModel.favUsers.observe(viewLifecycleOwner, EventObserver { favUsers ->
            Log.i("$TAG: ", "Fav users: $favUsers")
            if (!favUsers.isNullOrEmpty()) {
                favUserAdapter?.updateUsers(favUsers)
            } else {
                tvNoUsers.show()
            }
            progress_bar.hide()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initObserver()
        return inflater.inflate(R.layout.fragment_favourite_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main_toolbar.isTitleCentered = true
    }

    override fun onResume() {
        super.onResume()
        initializeList()
    }

    override fun onGitUserItemClick(userData: UserData) {
        val bundle = Bundle()
        bundle.putParcelable(UserDetailsFragment.USER_DATA, userData)
        findNavController().navigate(R.id.userDetailsFragment, bundle)
    }

    override fun onFavouriteClick(userData: UserData) {
        if (userData.isFavourite == 0) {
            userData.isFavourite = 1
        } else {
            userData.isFavourite = 0
        }
        viewModel.updateUserFavourite(userData)
        favUserAdapter?.updateFavourite(userData)
    }

    companion object {
        private val TAG = FavouriteUsersFragment::class.java.simpleName
    }

}