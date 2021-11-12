package com.gituser.paging.view.users.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gituser.paging.R
import com.gituser.paging.data.model.UserData
import com.gituser.paging.utils.EventObserver
import com.gituser.paging.utils.NetworkUtil
import com.gituser.paging.utils.toastMessage
import com.gituser.paging.view.users.adapter.GitUserLoadAdapter
import com.gituser.paging.view.users.adapter.GitUserPagerAdapter
import com.gituser.paging.view.users.listeners.UserItemClickListener
import com.gituser.paging.view.users.viewmodel.GitUserViewModel
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.progress_bar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class UserFragment : Fragment(), UserItemClickListener {

    private val viewModel: GitUserViewModel by viewModel()
    private lateinit var gitUserAdapter: GitUserPagerAdapter
    private var lastKey = ""
    private var isFirstLoaded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gitUserAdapter = GitUserPagerAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        setListeners()
        initializeList()
        observePageLoadState()
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstLoaded)
            viewModel.getFavouriteUsers()
    }

    private fun observePageLoadState() {
        lifecycleScope.launch {
            gitUserAdapter.loadStateFlow.collectLatest { loadState ->
                progress_bar.isVisible = loadState.refresh is LoadState.Loading
            }
        }
    }

    private fun initializeList() {
        etSearch.requestFocus()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        rvUsers.apply {
            layoutManager = linearLayoutManager
            adapter = gitUserAdapter.withLoadStateFooter(
                footer = GitUserLoadAdapter(gitUserAdapter::retry)
            )
        }
    }

    private fun initObserver() {
        viewModel.favUsers.observe(viewLifecycleOwner, EventObserver { users ->
            gitUserAdapter.updateFavouriteUsers(users)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, EventObserver { errorMessage ->
            activity?.toastMessage(errorMessage)
        })

    }

    private fun setListeners() {
        swipeRefresh.setOnRefreshListener(refreshListener)
        etSearch.addTextChangedListener { text ->
            if (text.toString() != lastKey) {
                lastKey = text.toString()
                performSearch(text?.toString() ?: "")
            }
        }
        ivFavourite.setOnClickListener {
            findNavController().navigate(R.id.favUserFragment)
            isFirstLoaded = false
        }
    }

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        if (!NetworkUtil.isNetworkConnected(requireContext())) {
            swipeRefresh.isRefreshing = false
            return@OnRefreshListener
        }
        isFirstLoaded = true
        swipeRefresh.isRefreshing = true
        performSearch(etSearch.text?.toString() ?: "")
    }

    private fun performSearch(key: String) {
        if (NetworkUtil.isNetworkConnected(requireContext())) {
            if (key.isNotEmpty() && key.length >= 3)
                lifecycleScope.launch {
                    Log.i("$TAG: ", "get user API call for $key")
                    viewModel.getGitUsers(searchKey = key).debounce(500)
                        .collectLatest { userData ->
                            Log.i("$TAG: ", "get user API Success for $key")
                            updateUserList(userData)
                        }
                }
            else {
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private suspend fun updateUserList(userData: PagingData<UserData>) {
        Log.i("$TAG: ", "$userData")
        gitUserAdapter.submitData(userData)
        swipeRefresh.isRefreshing = false
    }

    override fun onGitUserItemClick(userData: UserData) {
        val bundle = Bundle()
        bundle.putParcelable(UserDetailsFragment.USER_DATA, userData)
        findNavController().navigate(R.id.userDetailsFragment, bundle)
        isFirstLoaded = false
    }

    override fun onFavouriteClick(userData: UserData) {
        if (userData.isFavourite == 0) {
            userData.isFavourite = 1
        } else {
            userData.isFavourite = 0
        }
        viewModel.updateUserFavourite(userData)
        gitUserAdapter.snapshot().forEach { user ->
            if (user == userData) {
                user.isFavourite = userData.isFavourite
                gitUserAdapter.notifyDataSetChanged()
                return
            }
        }
    }

    companion object {
        private val TAG = UserFragment::class.java.simpleName
    }

}