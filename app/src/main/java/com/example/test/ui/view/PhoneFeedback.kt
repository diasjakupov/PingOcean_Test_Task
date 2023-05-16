package com.example.test.ui.view

import android.Manifest
import android.app.Activity
import android.provider.Contacts.Intents.UI
import android.widget.Space
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.test.BuildConfig
import com.example.test.ui.data.models.PermissionNotGranted
import com.example.test.ui.data.models.UIState

@Composable
fun PhoneFeedback(
    viewModel: MainScreenViewModel,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val context = LocalContext.current

    val phoneNumber = remember {
        mutableStateOf("")
    }
    val incomeCount = remember {
        viewModel.incomeCount
    }
    val outcomeCount = remember {
        viewModel.outcomeCount
    }
    Column(modifier = Modifier.fillMaxWidth(0.7f)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = phoneNumber.value,
                onValueChange = { phone ->
                    if ("[0-9+]*".toRegex().containsMatchIn(phone)) {
                        phoneNumber.value = phone
                    }
                },
                placeholder = {
                    Text(text = "Enter phone number: ")
                },
                modifier = Modifier.weight(0.7f),
                enabled = !((viewModel.uiState.value is UIState.Error) && ((viewModel.uiState.value as UIState.Error).type is PermissionNotGranted))

            )
            Button(
                onClick = { viewModel.checkForPhoneNumber(context, phoneNumber.value) },
                enabled = !((viewModel.uiState.value is UIState.Error) && ((viewModel.uiState.value as UIState.Error).type is PermissionNotGranted))            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search for phone",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))


        when (viewModel.uiState.value) {
            is UIState.Default -> {
                Text("Please enter phone number")
            }
            is UIState.Success -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Income count: ${incomeCount.value}s")
                    Text(text = "Outcome count: ${outcomeCount.value}s")
                }
            }
            is UIState.Error -> {
                Column() {
                    Text((viewModel.uiState.value as UIState.Error).error, color = Color.Red)
                    if ((viewModel.uiState.value as UIState.Error).type is PermissionNotGranted) {
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
                            launcher.launch(Manifest.permission.READ_CALL_LOG)
                        } else {
                            Text(
                                "Please allow Call Log permission in the settings",
                                modifier = Modifier.padding(top = 12.dp),
                                color = Color.Red
                            )
                        }
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row() {
            Button(
                onClick = {
                    phoneNumber.value = ""
                    viewModel.clear()
                },
                enabled = !((viewModel.uiState.value is UIState.Error) && ((viewModel.uiState.value as UIState.Error).type is PermissionNotGranted))
            ) {
                Text("Clear results")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(onClick = { (context as Activity).finish() }) {
                Text("Exit")
            }
        }


    }
}