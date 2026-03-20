package com.team2.meetspace.ui.compose.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.meetspace.data.PreferencesManager
import com.team2.meetspace.ui.compose.components.MspFilledButton
import com.team2.meetspace.ui.compose.components.MspOutlineButton
import com.team2.meetspace.ui.compose.components.PermissionCard
import com.team2.meetspace.ui.theme.LogoGradient

enum class LandingScreenStep {
    Welcome,
    Permission
}

@Preview(showBackground = true)
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

    var currentPage by rememberSaveable { mutableStateOf(LandingScreenStep.Welcome) }

    val onFinishOnboarding = {
        currentPage = LandingScreenStep.Welcome
        preferencesManager.setOnboardingCompleted()
        onNextButtonClicked()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> onFinishOnboarding() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        MeetspaceLogo()
        Spacer(modifier = Modifier.height(24.dp))

        WelcomePageContent(
            onNext = { currentPage = LandingScreenStep.Permission }
        )
        Spacer(modifier = Modifier.weight(1f))

        if(currentPage == LandingScreenStep.Permission) {
            PermissionCard(
                icon = Icons.Outlined.AccountCircle,
                title = "Разрешить приложению Meetspace доступ к сообщениям и отправке уведомлений?",
                description = "Разрешите приложению отправлять уведомления вам и приглашенным на встречу, чтобы вы не забыли о ее начале",
                allowButtonText = "Разрешить",
                denyButtonText = "Пропустить",
                onAllowClick =
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.POST_NOTIFICATIONS,
                                    Manifest.permission.SEND_SMS
                                )
                            )
                        } else {
                            permissionLauncher.launch(arrayOf(Manifest.permission.SEND_SMS))
                        }
                    },
                onDenyClick = { onFinishOnboarding() }
            )
        }
    }
}

@Composable
private fun MeetspaceLogo(
    size: Dp = 200.dp,
    fontSize: TextUnit = 18.sp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = LogoGradient
                )
            )
            .border(4.dp, Color.Black, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "MEETSPACE",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}

@Composable
private fun WelcomePageContent(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Meetspace",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Добро пожаловать!\nВ MEETSPACE вы можете создавать видео-встречи и управлять их планированием.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(98.dp))
        MspFilledButton(onClick = { onNext() }, text = "Далее")
    }
}

