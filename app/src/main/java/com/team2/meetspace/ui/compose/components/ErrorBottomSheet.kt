package com.team2.meetspace.ui.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorBottomSheet(
    onExit: () -> Unit,
    errorText: String,
    description: String)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            errorText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            description,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        MspFilledButton(onClick = onExit, text = "Закрыть")
    }
}