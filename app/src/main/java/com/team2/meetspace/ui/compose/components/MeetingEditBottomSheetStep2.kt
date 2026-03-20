package com.team2.meetspace.ui.compose.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.team2.meetspace.Dependencies
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun Step2DateTime(
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val incorrectTime = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val checkTimestamp = { x: LocalDate, y:LocalTime ->
        incorrectTime.value = (Dependencies.TimestampHelper().dateTimeToTimestamp(x, y) < currentTime)
        Log.i("Timepicker", "Timestamp checked: incorrect? ${incorrectTime.value}")
    }


    Column {
        Text(
            "Выберите дату и время",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DateTimePicker(
            initialDate = selectedDate,
            initialTime = selectedTime,
            onDateSelected = { x, y -> checkTimestamp(x, y); onDateChange(x) },
            onTimeSelected = { x, y -> checkTimestamp(x, y); onTimeChange(y) }
        )

        Spacer(Modifier.height(32.dp))

        MspElementWithDisabledClick(
            { Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), enabled = !incorrectTime.value) {
                Text("Выбрать участников")
            } },
            { Toast.makeText(context, "Выбранное время должно быть позже текущего", Toast.LENGTH_SHORT).show() },
            !incorrectTime.value
        )
    }
}