package com.team2.meetspace

import android.media.MediaPlayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team2.meetspace.ui.screens.CallScreen
import com.team2.meetspace.ui.screens.JoinMeetingScreen
import com.team2.meetspace.ui.screens.LandingScreen
import com.team2.meetspace.ui.screens.MainScreen
import com.team2.meetspace.ui.theme.MeetspaceTheme

// Перечисление экранов приложения.
enum class MeetspaceScreen(){
    Main,
    JoinMeeting,
    Call,
    Landing
}

// Основная активность, у нас только 1.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeetspaceTheme {
                MeetspaceApp();
            }
        }
    }
}

// Основное приложение. Здесь отрисовываются все экраны.
// Навигация происходит с помощью MeetspaceAppNavHost и не определяется внутри других экранов.
// Вместо этого в качестве параметра экрану задаются функции перехода по кнопкам.
@Composable
fun MeetspaceApp(){
    Scaffold(modifier = Modifier.fillMaxSize().background(Color.White)) { innerPadding ->
        MeetspaceAppNavHost(innerPadding);
    }
}

// С помощью данного компонента происходит навигация между экранами.
// Подробнее почитать: https://developer.android.com/codelabs/basic-android-kotlin-compose-navigation
// Пример приложения с кексами на гите: https://github.com/MechaArms/Cupcake-App/blob/master/app/src/main/java/com/example/cupcake/CupcakeScreen.kt
@Composable
fun MeetspaceAppNavHost(
    innerPadding: PaddingValues,
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = MeetspaceScreen.Landing.name,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = MeetspaceScreen.Landing.name){
            LandingScreen(
                onNextButtonClicked = {
                    navController.navigate(MeetspaceScreen.Main.name);
                }
            )
        }
        composable(route = MeetspaceScreen.Main.name){
            MainScreen(
                onJoinMeetingButtonClicked = {
                    navController.navigate(MeetspaceScreen.JoinMeeting.name);
                }
            )
        }
        composable(route = MeetspaceScreen.JoinMeeting.name){
            JoinMeetingScreen(
                onNextButtonClicked = {
                    navController.navigate(MeetspaceScreen.Call.name);
                },
                onCancelButtonClicked = {
                    navController.navigate(MeetspaceScreen.Main.name);
                }
            )
        }
        composable(route = MeetspaceScreen.Call.name){
            CallScreen(
                onHangupButtonClicked = {
                    navController.navigate(MeetspaceScreen.Main.name);
                }
            );
        }
    }
}