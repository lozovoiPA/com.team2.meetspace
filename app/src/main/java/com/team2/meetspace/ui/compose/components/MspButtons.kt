package com.team2.meetspace.ui.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.team2.meetspace.ui.theme.ButtonColor

@Composable
public fun MspFilledButton(
    modifier: Modifier = Modifier.fillMaxWidth(0.8F).height(56.dp),
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
public fun MspOutlineButton(
    modifier: Modifier = Modifier.fillMaxWidth(0.8F).height(56.dp),
    onClick: () -> Unit,
    text: String
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(text = text, color = Color.Black)
    }
}