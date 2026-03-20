package com.team2.meetspace.ui.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.meetspace.ui.theme.ButtonColor

@Composable
public fun MspFilledButton(
    modifier: Modifier = Modifier.fillMaxWidth(0.8F).height(56.dp),
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
        enabled = enabled
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
public fun MspOutlineButton(
    modifier: Modifier = Modifier.fillMaxWidth(0.8F).height(56.dp),
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(1.dp, Color.Black),
        enabled = enabled
    ) {
        Text(text = text, color = Color.Black)
    }
}

@Composable
public fun MspElementWithDisabledClick(
    button: @Composable () -> Unit,
    onDisabledClick: () -> Unit,
    enabled: Boolean = false
) {
    Box(contentAlignment = Alignment.Center) {
        button()
        if (!enabled){
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onDisabledClick() }
            )
        }
    }
}