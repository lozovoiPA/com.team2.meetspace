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
        preferencesManager.setOnboardingCompleted()
        onNextButtonClicked()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentPage) {
            LandingScreenStep.Welcome -> {
                Spacer(modifier = Modifier.weight(1f))
                MeetspaceLogo()
                Spacer(modifier = Modifier.height(24.dp))

                WelcomePageContent(
                    onNext = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            currentPage = LandingScreenStep.Permission
                        } else {
                            onFinishOnboarding()
                        }
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            LandingScreenStep.Permission -> {
                Spacer(modifier = Modifier.height(160.dp))
                Spacer(modifier = Modifier.weight(1f))
                
                MeetspaceLogo(150.dp, 14.sp)
                Spacer(modifier = Modifier.height(32.dp))

                PermissionStepContent(
                    onNext = onFinishOnboarding,
                    preferencesManager = preferencesManager
                )
            }
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

@Composable
private fun PermissionStepContent(
    onNext: () -> Unit,
    preferencesManager: PreferencesManager
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        preferencesManager.setOnboardingCompleted()
        onNext()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Meetspace",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Разрешите приложению отправлять уведомления, чтобы вы могли знать, если скоро начнется встреча.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.DarkGray
                )

                Text(
                    text = "Разрешить приложению Meetspace отправлять уведомления?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MspFilledButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
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
                        text = "Разрешить"
                    )
                    MspOutlineButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            preferencesManager.setOnboardingCompleted()
                            onNext()
                        },
                        text = "Запретить"
                    )
                }
            }
        }
    }
}
