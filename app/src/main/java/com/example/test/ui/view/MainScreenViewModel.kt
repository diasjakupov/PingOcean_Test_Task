package com.example.test.ui.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.ui.data.models.InvalidData
import com.example.test.ui.data.models.PermissionNotGranted
import com.example.test.ui.data.models.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class MainScreenViewModel : ViewModel() {
    var uiState = mutableStateOf<UIState>(UIState.Default)


    var outcomeCount = mutableStateOf(0)
    var incomeCount = mutableStateOf(0)

    var untilGranted = mutableStateOf(0)


    fun checkForPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkForPhoneNumber(context: Context, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            outcomeCount.value = 0
            incomeCount.value = 0
            if (checkForPermission(context)) {
                if ((phone.length < 11)) {
                    uiState.value = UIState.Error("Invalid phone number", InvalidData())
                } else {
                    val columns = arrayOf(
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.TYPE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.DATE
                    )
                    val cursor =
                        context.contentResolver.query(
                            CallLog.Calls.CONTENT_URI,
                            columns,
                            null,
                            null
                        )
                    while (cursor?.moveToNext() == true) {
                        val number = cursor.getString(0)
                        val type = cursor.getString(1)
                        val duration = cursor.getString(2)
                        val date = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(cursor.getString(3).toLong()),
                            TimeZone.getDefault().toZoneId()
                        )
                        if (number == phone) {
                            if (type.toInt() == CallLog.Calls.OUTGOING_TYPE) {
                                outcomeCount.value += duration.toInt()
                            } else {
                                if (LocalDateTime.now().until(date, ChronoUnit.DAYS) <= 3) {
                                    incomeCount.value += duration.toInt()
                                }
                            }
                        }

                    }
                    uiState.value = UIState.Success
                    cursor?.close()
                }

            } else {
                uiState.value = UIState.Error("Permission is not granted", PermissionNotGranted())
                untilGranted.value++
            }
        }

    }

    fun clear() {
        outcomeCount.value = 0
        incomeCount.value = 0
        uiState.value = UIState.Default
    }
}