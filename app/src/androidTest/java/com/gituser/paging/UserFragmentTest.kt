package com.gituser.paging

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gituser.paging.view.MainActivity
import com.gituser.paging.view.users.fragments.UserDetailsFragment
import com.gituser.paging.view.users.fragments.UserFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserFragmentTest {

    @Test
    fun recyclerviewZeroItemCheck() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.rvUsers)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val recyclerView = view as RecyclerView
            assertEquals(0, recyclerView.adapter?.itemCount)
        }
    }

    @Test
    fun loadsTheTestResultsWhenSearchForUsers() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.rvUsers)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val recyclerView = view as RecyclerView
            assertEquals(0, recyclerView.adapter?.itemCount)
        }

        onView(withId(R.id.etSearch)).perform(
            typeText("moj"),
            pressImeActionButton()
        )
        Thread.sleep(5000)
        onView(withId(R.id.rvUsers)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
            val recyclerView = view as RecyclerView
            assertTrue(recyclerView.adapter?.itemCount!! > 0)
        }
    }

    @Test
    fun verifyRecyclerItemClick() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.etSearch)).perform(
            typeText("lol"),
            pressImeActionButton()
        )
        Thread.sleep(3000)
        onView(withId(R.id.rvUsers)).check { _, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
        }
        onView(withId(R.id.rvUsers)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
        )
    }

    @Test
    fun testNavigationFromUserToUserDetailsScreen() {

        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.etSearch)).perform(
            typeText("lol"),
            pressImeActionButton()
        )
        Thread.sleep(3000)
        onView(withId(R.id.rvUsers)).check { _, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
        }
        onView(withId(R.id.rvUsers)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
        )

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        val fragmentArgs =
            bundleOf(UserDetailsFragment.USER_DATA to UserDetailsFragmentTest.testUser)
        val titleScenario = launchFragmentInContainer<UserFragment>(
            themeResId = R.style.Theme_PagingDemo,
        )

        titleScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_main)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.navigate(R.id.userDetailsFragment, fragmentArgs)
        }

        assertEquals(navController.currentDestination?.id, R.id.userDetailsFragment)
    }
}