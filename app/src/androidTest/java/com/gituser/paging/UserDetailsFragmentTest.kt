package com.gituser.paging

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gituser.paging.data.model.UserData
import com.gituser.paging.view.users.fragments.UserDetailsFragment
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDetailsFragmentTest {

    private lateinit var fragmentArgs: Bundle
    private lateinit var navController: TestNavHostController
    private lateinit var scenario: FragmentScenario<UserDetailsFragment>

    @Before
    fun setup() {
        fragmentArgs = bundleOf(UserDetailsFragment.USER_DATA to testUser)
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
        scenario = launchFragmentInContainer(
            themeResId = R.style.Theme_PagingDemo,
            fragmentArgs = fragmentArgs
        )
    }

    @Test
    fun testUserDetailsLaunch() {
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test
    fun testFavouriteUserIconClick() {
        onView(withId(R.id.ivFavourite)).perform(click())
        assertTrue(testUser.isFavourite == 1)
    }

    @Test
    fun testBackIconClick() {
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_main)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.userDetailsFragment)
        }
        onView(withId(R.id.ivBackArrow)).perform(click())
        Assert.assertEquals(navController.currentDestination?.id, R.id.userFragment)

    }

    companion object {

        val testUser =
            UserData(
                id = 1,
                login = "Captain America",
                url = "https://www.marvel.com",
                avatar_url = "ttps://www.marvel.com/CA",
                type = "User",
                isFavourite = 0
            )
    }
}