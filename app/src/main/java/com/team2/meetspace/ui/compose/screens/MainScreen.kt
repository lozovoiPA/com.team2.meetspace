package com.team2.meetspace.ui.compose.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team2.meetspace.R
import com.team2.meetspace.ui.compose.components.ErrorBottomSheet
import com.team2.meetspace.ui.compose.components.MeetingEditBottomSheet
import com.team2.meetspace.ui.compose.components.MeetingCard
import com.team2.meetspace.ui.compose.components.MspElementWithDisabledClick
import com.team2.meetspace.ui.compose.components.MspFilledButton
import com.team2.meetspace.ui.viewModel.MainScreenViewModel
import com.team2.meetspace.ui.viewModel.MeetingEditBottomSheetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel(),
    bottomSheetViewModel: MeetingEditBottomSheetViewModel = viewModel(),
    onJoinMeeting: () -> Unit = {},
    onEnterMeeting: (String) -> Unit = {},
    onMeetingButtonClicked: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val isConnected by state.isConnected.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.retrieveMeetings()
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { onMeetingButtonClicked() },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Встречи") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { },
                    enabled = false
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = "Главная",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            MspElementWithDisabledClick(
                @Composable{ MspFilledButton(onClick = { viewModel.showCreateSheet() }, text = "Создать встречу", enabled = isConnected) },
                { viewModel.showError() },
                enabled = isConnected
            )
            Spacer(modifier = Modifier.height(16.dp))

            MspElementWithDisabledClick(
                @Composable{ MspFilledButton(onClick = onJoinMeeting, text = "Присоединиться", enabled = isConnected) },
                { viewModel.showError() },
                enabled = isConnected
            )

            Spacer(modifier = Modifier.height(40.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(20.dp))
            if (state.upcomingMeetings.isEmpty()) {
                Text(
                    text = "Ближайшие встречи",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Встреч пока нет")
                }
            } else {
                Text(
                    text = "Ближайшие встречи",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.upcomingMeetings) { meeting ->
                        MeetingCard(
                            meeting = meeting,
                            onEnterClick = {
                                onEnterMeeting(meeting.roomIdentifier)
                            },
                            enabled = isConnected,
                            onDisabledClick = {
                                viewModel.showError()
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.showCreateBottomSheet) {
            MeetingEditBottomSheet(
                viewModel = bottomSheetViewModel,
                sheetState = sheetState,
                onDismiss = {
                    scope.launch { sheetState.hide() }
                    viewModel.hideCreateSheet()
                },
                onMeetingCreated = { meeting ->
                    viewModel.addMeeting(meeting)
                    scope.launch { sheetState.hide() }
                    viewModel.hideCreateSheet()
                }
            )
        }

        if (state.displayConnectionError) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.hideError() },
                sheetState = sheetState
            ) {
                ErrorBottomSheet(
                    onExit = {
                        scope.launch { sheetState.hide(); viewModel.hideError() }
                    },
                    errorText = stringResource(R.string.no_internet_connection_label),
                    description = stringResource(R.string.no_internet_connection_desc)
                )
            }
        }
    }
}