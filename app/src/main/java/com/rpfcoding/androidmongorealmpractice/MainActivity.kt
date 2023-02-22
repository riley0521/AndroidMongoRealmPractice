package com.rpfcoding.androidmongorealmpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.rpfcoding.androidmongorealmpractice.presentation.HomeScreen
import com.rpfcoding.androidmongorealmpractice.presentation.HomeViewModel
import com.rpfcoding.androidmongorealmpractice.ui.theme.AndroidMongoRealmPracticeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidMongoRealmPracticeTheme {
                val viewModel: HomeViewModel = hiltViewModel()
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
    }
}