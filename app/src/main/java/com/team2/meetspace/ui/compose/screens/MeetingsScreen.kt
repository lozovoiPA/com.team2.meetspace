package com.team2.meetspace.ui.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team2.meetspace.ui.compose.components.MeetingEditBottomSheet
import com.team2.meetspace.ui.viewModel.MainScreenViewModel
import kotlinx.coroutines.launch
import com.team2.meetspace.ui.compose.components.MeetingCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingsScreen(
    viewModel: MainScreenViewModel = viewModel(),
    onJoinMeeting: (String) -> Unit = {},
    onHomeButtonClicked: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val isConnected by state.isConnected.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.retrieveMeetings()
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Встречи") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onHomeButtonClicked() },
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
                text = "Запланированные встречи",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(40.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else if (state.upcomingMeetings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Встреч пока нет")
                }
            }
            else{
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    /*
                    Text(
                        text = "Ближайшие встречи",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )*/

                    LazyColumn {
                        items(state.upcomingMeetings) { meeting ->
                            MeetingCard(
                                meeting = meeting,
                                onEnterClick = {
                                    onJoinMeeting(meeting.roomIdentifier)
                                },
                                enabled = isConnected
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        if (state.showCreateBottomSheet) {
            MeetingEditBottomSheet(
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
    }
}