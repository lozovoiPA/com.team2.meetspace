package com.team2.meetspace.ui.compose.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.meetspace.data.PreferencesManager
import com.team2.meetspace.ui.compose.components.PermissionCard

@Composable
fun LandingScreen(
    onNextButtonClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val isOnboardingCompleted = remember { preferencesManager.isOnboardingCompleted() }

    if (isOnboardingCompleted) {
        LaunchedEffect(Unit) {
            onNextButtonClicked()
        }
        return
    }

    var currentPage by rememberSaveable { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        preferencesManager.setOnboardingCompleted()
        onNextButtonClicked()
    }

    val onFinishOnboarding = {
        preferencesManager.setOnboardingCompleted()
        onNextButtonClicked()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper Spacer
        Spacer(modifier = Modifier.weight(1f))

        // Logo
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1A1A), Color(0xFF6B1B3C), Color(0xFFE94E77))
                    )
                )
                .border(4.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "MEETSPACE",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Meetspace",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (currentPage == 0) {
            WelcomePageContent(
                onNext = { currentPage = 1 }
            )
            // Push Welcome content slightly up from the very bottom
            Spacer(modifier = Modifier.weight(1f))
        } else {
            // Description text stays at the top (below title)
            Text(
                text = "Разрешите приложению отправлять уведомления, чтобы вы могли знать, если скоро начнется встреча.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // THIS Spacer pushes the PermissionCard to the bottom while keeping text above
            Spacer(modifier = Modifier.weight(2f))

            PermissionCard(
                icon = Icons.Outlined.Notifications,
                title = "Разрешить приложению Meetspace отправлять уведомления?",
                allowButtonText = "Разрешить",
                denyButtonText = "Запретить",
                onAllowClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onFinishOnboarding()
                    }
                },
                onDenyClick = onFinishOnboarding
            )
        }
    }
}

@Composable
private fun WelcomePageContent(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Добро пожаловать!\nВ MEETSPACE вы можете создавать видео-встречи и управлять их планированием.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1F2D))
        ) {
            Text("Далее", color = Color.White)
        }
    }
}
