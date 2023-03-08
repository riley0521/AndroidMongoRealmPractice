package com.rpfcoding.androidmongorealmpractice.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rpfcoding.androidmongorealmpractice.presentation.auth.AuthenticationScreen
import com.rpfcoding.androidmongorealmpractice.presentation.auth.AuthenticationViewModel
import com.rpfcoding.androidmongorealmpractice.presentation.home.HomeScreen
import com.rpfcoding.androidmongorealmpractice.presentation.home.HomeViewModel
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

@ExperimentalMaterial3Api
@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = startDestination) {
        authRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            }
        )
        homeRoute()
    }
}

@ExperimentalMaterial3Api
fun NavGraphBuilder.authRoute(
    navigateToHome: () -> Unit
) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val authenticated by viewModel.authenticated.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        AuthenticationScreen(
            authenticated = authenticated,
            isLoading = isLoading,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onSuccessfulSignIn = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully Authenticated!")
                        viewModel.setLoading(false)
                    },
                    onError = {
                        messageBarState.addError(it as Exception)
                        viewModel.setLoading(false)
                    }
                )
            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = navigateToHome
        )
    }
}

fun NavGraphBuilder.homeRoute() {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = viewModel()
        val data by viewModel.data.collectAsState()
        val filtered by viewModel.filtered.collectAsState()
        val name by viewModel.name.collectAsState()
        val objectId by viewModel.objectId.collectAsState()

        HomeScreen(
            data = data,
            filtered = filtered,
            name = name,
            objectId = objectId,
            onNameChanged = viewModel::updateName,
            onObjectIdChanged = viewModel::updateObjectId,
            onInsertClicked = viewModel::insertPerson,
            onUpdateClicked = viewModel::updatePerson,
            onDeleteClicked = viewModel::deletePerson,
            onFilterClicked = viewModel::filterData
        )
    }
}