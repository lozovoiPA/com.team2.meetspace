package com.team2.meetspace.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun MainScreen(
    onJoinMeetingButtonClicked: () -> Unit = {}
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Главный экран\nВстречи, параметры встреч",
            color = Color.Gray,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = { onJoinMeetingButtonClicked(); },
            modifier = Modifier
                .align(Alignment.TopCenter) // Aligns the button to the bottom center
                .width(300.dp)
                .padding(
                    horizontal = 32.dp,
                    vertical = 16.dp)
        ) {
            Text(
                text = "На экран подключения\nк встрече",
                textAlign = TextAlign.Center
            )
        }
    }
}