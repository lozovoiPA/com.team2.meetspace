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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team2.meetspace.data.repositories.MeetingRepository
import com.team2.meetspace.ui.compose.screens.CallScreen
import com.team2.meetspace.ui.compose.screens.JoinMeetingScreen
import com.team2.meetspace.ui.compose.screens.LandingScreen
import com.team2.meetspace.ui.compose.screens.MainScreen
import com.team2.meetspace.ui.theme.MeetspaceTheme
import com.team2.meetspace.ui.viewModel.MeetingEditBottomSheetViewModel
import dagger.hilt.android.HiltAndroidApp

enum class MeetspaceScreen {
    Landing,
    Main,
    JoinMeeting,
    Call
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dependencies = Dependencies(this);
        val factory = MeetingEditBottomSheetViewModelFactory(dependencies);

        enableEdgeToEdge()
        setContent {
            MeetspaceTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(Color.White)) { innerPadding ->
                    MeetspaceAppNavHost(innerPadding, factory);
                }
            }
        }
    }
}


@Composable
fun MeetspaceAppNavHost(
    innerPadding: PaddingValues,
    factory: MeetingEditBottomSheetViewModelFactory,
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
                bottomSheetViewModel = viewModel(factory = factory),
                onJoinMeeting = {
                    navController.navigate(MeetspaceScreen.JoinMeeting.name)
                }
            )
        }

        composable(route = MeetspaceScreen.JoinMeeting.name) {
            JoinMeetingScreen(
                onNextButtonClicked = {
                    navController.navigate(MeetspaceScreen.Call.name)
                },
                onCancelButtonClicked = {
                    navController.navigate(MeetspaceScreen.Main.name)
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
    }
}