package com.team2.meetspace.ui.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.meetspace.R

@Composable
fun Step1TypeAndDescription(
    isImmediate: Boolean,
    onImmediateChange: (Boolean) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column {
        Text(
            "Тип встречи",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = isImmediate,
                    onValueChange = { onImmediateChange(true) },
                    role = Role.RadioButton
                )
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isImmediate,
                onClick = null)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Создать прямо сейчас",
                    fontWeight = if (isImmediate) FontWeight.Bold else FontWeight.Normal)
                Text("Встреча начнется сразу после создания",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = !isImmediate,
                    onValueChange = { onImmediateChange(false) },
                    role = Role.RadioButton
                )
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = !isImmediate, onClick = null)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Запланировать на время",
                    fontWeight = if (!isImmediate) FontWeight.Bold else FontWeight.Normal)
                Text("Выберите дату и время встречи",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Тема встречи (необязательно)") },
            placeholder = { Text(stringResource(R.string.meeting_placeholder_desc)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text("Далее")
        }
    }
}