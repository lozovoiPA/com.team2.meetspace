package com.team2.meetspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.team2.meetspace.data.PreferencesManager
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.MeetingsRetrieved
import com.team2.meetspace.ui.MeetspaceAppNavHost
import com.team2.meetspace.ui.theme.MeetspaceTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dependencies = Dependencies(this)

        // Моки встреч
        lifecycleScope.launch {
            lateinit var meetings: List<Meeting>
            withContext(Dispatchers.IO) {
                meetings = when (val result = dependencies.meetingLocalDataSource.retrieve()){
                    is MeetingsRetrieved -> result.meetings;
                    else -> emptyList()
                }
            }

            /*
            if(meetings.isEmpty()){
                val meetingsList: List<Meeting> = listOf(
                    Meeting(
                        roomIdentifier = UUID.randomUUID().toString(),
                        timestamp = Dependencies.TimestampHelper().dateTimeToTimestamp(
                            LocalDate.now(PreferencesManager.systemTimeZone).plusDays(3),
                            LocalTime.of(10, 0)),
                        description = "Обсуждение проделанной работы"
                    ),
                    Meeting(
                        roomIdentifier = UUID.randomUUID().toString(),
                        timestamp = Dependencies.TimestampHelper().dateTimeToTimestamp(
                            LocalDate.now(PreferencesManager.systemTimeZone).plusDays(1),
                            LocalTime.of(14, 30)),
                        description = "Подготовка отчетности"
                    )
                )
                withContext(Dispatchers.IO) {
                    dependencies.meetingLocalDataSource.create(meetingsList[0])
                    dependencies.meetingLocalDataSource.create(meetingsList[1])
                }
            }*/

        }

        enableEdgeToEdge()
        setContent {
            MeetspaceTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(Color.White)) { innerPadding ->
                    MeetspaceAppNavHost(innerPadding, dependencies)
                }
            }
        }
    }
}