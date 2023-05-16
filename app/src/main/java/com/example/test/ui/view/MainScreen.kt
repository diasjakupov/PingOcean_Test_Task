package com.example.test.ui.view

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.ui.data.models.PermissionNotGranted
import com.example.test.ui.data.models.UIState

@Composable
fun MainScreen() {
    val viewModel = viewModel<MainScreenViewModel>()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted && viewModel.untilGranted.value != -1) {
                Log.e("TAG", "not granted")
                viewModel.untilGranted.value++
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && viewModel.untilGranted.value > 2) {
                    viewModel.uiState.value =
                        UIState.Error("Permission is not granted", PermissionNotGranted())
                    viewModel.untilGranted.value = -1
                }
            }
        }
    )

    LaunchedEffect(key1 = viewModel.untilGranted.value, key2 = Unit) {
        if (!viewModel.checkForPermission(context)) {
            launcher.launch(Manifest.permission.READ_CALL_LOG)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PhoneFeedback(viewModel = viewModel, launcher = launcher)

    }
}