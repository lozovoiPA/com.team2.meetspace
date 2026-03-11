package com.team2.meetspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team2.meetspace.ui.screens.CallScreen
import com.team2.meetspace.ui.screens.JoinMeetingScreen
import com.team2.meetspace.ui.screens.LandingScreen
import com.team2.meetspace.ui.screens.MainScreen
import com.team2.meetspace.ui.screens.MeetingScreen
import com.team2.meetspace.ui.theme.MeetspaceTheme

enum class MeetspaceScreen {
    Landing,
    Main,
    JoinMeeting,
    Call,
    MeetingInfo
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeetspaceTheme {
                MeetspaceApp()
            }
        }
    }
}

@Composable
fun MeetspaceApp() {
    Scaffold(modifier = Modifier.fillMaxSize().background(Color.White)) { innerPadding ->
        MeetspaceAppNavHost(innerPadding)
    }
}

@Composable
fun MeetspaceAppNavHost(
    innerPadding: PaddingValues,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = MeetspaceScreen.Landing.name,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = MeetspaceScreen.Landing.name) {
            LandingScreen(
                onNextButtonClicked = {
                    navController.navigate(MeetspaceScreen.Main.name)
                }
            )
        }

        composable(route = MeetspaceScreen.Main.name) {
            MainScreen(
                onJoinMeeting = {
                    navController.navigate(MeetspaceScreen.JoinMeeting.name)
                },
                onMeetingButtonClicked = {
                    navController.navigate(MeetspaceScreen.MeetingInfo.name)
                }
            )
        }

        composable(route = MeetspaceScreen.JoinMeeting.name) {
            JoinMeetingScreen(
                onNextButtonClicked = {roomCode, userName ->
                    navController.navigate(MeetspaceScreen.Call.name)
                },
                onCancelButtonClicked = {
                    navController.navigate(MeetspaceScreen.Main.name)
                },
                onMeetingButtonClicked = {
                    navController.navigate(MeetspaceScreen.MeetingInfo.name)
                }
            )
        }

        composable(route = MeetspaceScreen.Call.name) {
            CallScreen(
                onHangupButtonClicked = {
                    navController.navigate(MeetspaceScreen.Main.name)
                }
            )
        }

        composable(route = MeetspaceScreen.MeetingInfo.name) {
            MeetingScreen(
                onHomeButtonClicked = {
                    navController.navigate(MeetspaceScreen.Main.name)
                },
            )
        }
    }
}