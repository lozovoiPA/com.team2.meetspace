package com.team2.meetspace.ui.screens


import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.meetspace.ui.theme.CallBackgroundColor
import androidx.compose.ui.platform.LocalContext

import com.team2.meetspace.R;
@Preview
@Composable
fun CallScreen(
    onHangupButtonClicked: () -> Unit = {}
){
    // Текущая активность
    val context = LocalContext.current;
    val callTestAudioPlayer = CallTestAudioPlayer(context);
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(CallBackgroundColor)
    ) {
        Text(
            text = "Экран видео-встречи\nс основными кнопками",
            color = Color.LightGray,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    callTestAudioPlayer.stop();
                    onHangupButtonClicked();
                          },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(text = "На главную")
            }
            Button(
                onClick = { callTestAudioPlayer.toggle(); },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Музыка")
            }
        }
    }
}

// Класс-заглушка для проигрывания аудио-треков
class CallTestAudioPlayer(val context: Context) {
    private var mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.music);

    fun toggle() {
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start();
            return;
        }
        mediaPlayer.pause();
    }

    fun stop(){
        mediaPlayer.stop();
    }
}

