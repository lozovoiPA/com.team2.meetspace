package com.team2.meetspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team2.meetspace.data.PreferencesManager
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.ui.MeetspaceAppNavHost
import com.team2.meetspace.ui.compose.components.MeetingCreateBottomSheet
import com.team2.meetspace.ui.compose.screens.CallScreen
import com.team2.meetspace.ui.compose.screens.JoinMeetingScreen
import com.team2.meetspace.ui.compose.screens.LandingScreen
import com.team2.meetspace.ui.compose.screens.MainScreen
import com.team2.meetspace.ui.compose.screens.MeetingScreen
import com.team2.meetspace.ui.theme.MeetspaceTheme
import com.team2.meetspace.ui.viewModel.MeetingEditBottomSheetViewModel
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
        val factory = MeetingEditBottomSheetViewModelFactory(dependencies)

        val meetingsList: List<Meeting> = listOf(
            Meeting(
                roomIdentifier = UUID.randomUUID().toString(),
                timestamp = Dependencies.TimestampHelper().dateTimeToTimestamp(
                    LocalDate.now(PreferencesManager.systemTimeZone),
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
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                dependencies.meetingLocalDataSource.create(meetingsList[0])
                dependencies.meetingLocalDataSource.create(meetingsList[1])
            }
        }

        enableEdgeToEdge()
        setContent {
            MeetspaceTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(Color.White)) { innerPadding ->
                    MeetspaceAppNavHost(innerPadding, factory)
                }
            }
        }
    }
}