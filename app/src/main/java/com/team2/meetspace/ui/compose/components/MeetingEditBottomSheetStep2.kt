package com.team2.meetspace.ui.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    Column {
        Text(
            "Выберите дату и время",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DateTimePicker(
            initialDate = selectedDate,
            initialTime = selectedTime,
            onDateSelected = onDateChange,
            onTimeSelected = onTimeChange
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text("Выбрать участников")
        }
    }
}