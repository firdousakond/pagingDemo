package com.gituser.paging

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gituser.paging.view.users.fragments.FavouriteUsersFragment
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouriteUserFragmentTest {

    private lateinit var navController: TestNavHostController
    private lateinit var scenario: FragmentScenario<FavouriteUsersFragment>

    @Before
    fun setup() {
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
        scenario = launchFragmentInContainer(
            themeResId = R.style.Theme_PagingDemo,
        )
    }

    @Test
    fun testFavouriteUserLaunch() {
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test
    fun loadsTheUserResultsFromDB() {
        onView(withId(R.id.rvFavUsers)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
            val recyclerView = view as RecyclerView
            Assert.assertTrue(recyclerView.adapter?.itemCount!! > 0)
        }
    }

    @Test
    fun verifyRecyclerItemClick() {
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_main)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.favUserFragment)
        }

        onView(withId(R.id.rvFavUsers)).check { _, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
        }
        onView(withId(R.id.rvFavUsers)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )
    }

    @Test
    fun testNavigationFromFavUserToUserDetailsScreen() {

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_main)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.favUserFragment)
        }
        onView(withId(R.id.rvFavUsers)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        Assert.assertEquals(navController.currentDestination?.id, R.id.userDetailsFragment)
    }

}