package com.rpfcoding.androidmongorealmpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.rememberNavController
import com.rpfcoding.androidmongorealmpractice.presentation.navigation.Screen
import com.rpfcoding.androidmongorealmpractice.presentation.navigation.SetupNavGraph
import com.rpfcoding.androidmongorealmpractice.ui.theme.AndroidMongoRealmPracticeTheme
import com.rpfcoding.androidmongorealmpractice.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidMongoRealmPracticeTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController
                )
            }
        }
    }

    private fun getStartDestination(): String {
        val user = App.create(Constants.APP_ID).currentUser
        return if(user != null && user.loggedIn) Screen.Home.route
        else Screen.Authentication.route
    }
}