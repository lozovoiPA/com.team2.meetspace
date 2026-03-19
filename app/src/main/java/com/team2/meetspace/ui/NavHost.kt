package com.team2.meetspace.ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team2.meetspace.MeetingEditBottomSheetViewModelFactory
import com.team2.meetspace.ui.compose.screens.CallScreen
import com.team2.meetspace.ui.compose.screens.JoinMeetingScreen
import com.team2.meetspace.ui.compose.screens.LandingScreen
import com.team2.meetspace.ui.compose.screens.MainScreen
import com.team2.meetspace.ui.compose.screens.MeetingScreen


enum class MeetspaceScreen {
    Landing,
    Main,
    JoinMeeting,
    Call,
    MeetingInfo,
    MeetingEditBottomSheet
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetspaceAppNavHost(
    innerPadding: PaddingValues,
    factory: MeetingEditBottomSheetViewModelFactory
) {
    val navController = rememberNavController()
    var joinCode by remember { mutableStateOf("") }

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
                bottomSheetViewModel = viewModel(factory = factory),
                onJoinMeeting = {
                    navController.navigate(MeetspaceScreen.JoinMeeting.name)
                },
                onEnterMeeting = { code ->
                    joinCode = code
                    navController.navigate(MeetspaceScreen.JoinMeeting.name)
                },
                onMeetingButtonClicked = {
                    navController.navigate(MeetspaceScreen.MeetingInfo.name)
                }
            )
        }

        composable(route = MeetspaceScreen.JoinMeeting.name) {
            JoinMeetingScreen(
                initialCode = joinCode,
                onNextButtonClicked = { roomCode, userName ->
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
                onJoinMeeting = { code ->
                    joinCode = code
                    navController.navigate(MeetspaceScreen.JoinMeeting.name)
                },
                onHomeButtonClicked = {
                    navController.navigate(MeetspaceScreen.Main.name)
                }
            )
        }
    }
}