package com.team2.meetspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team2.meetspace.MainViewModel
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import com.team2.meetspace.ui.components.MeetingCreateBottomSheet
import kotlinx.coroutines.launch
import com.team2.meetspace.data.entities.Meeting
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.graphics.Color



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingScreen(
    viewModel: MainViewModel  = viewModel(),
    onHomeButtonClicked: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {  },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Встречи") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onHomeButtonClicked(); },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {  },
                    icon = {  },
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


            if (!state.isConnected) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет подключения к интренету")
                }
            }
            else if (state.isLoading) {
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
                    Text(
                        text = "Ближайшие встречи",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn {
                        items(state.upcomingMeetings) { meeting ->
                            MeetingCardInfo(
                                meeting = meeting
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        if (state.showCreateBottomSheet) {
            MeetingCreateBottomSheet(
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

@Composable
private fun MeetingCardInfo(meeting: Meeting) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = meeting.formattedDateTime,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = meeting.description.ifBlank { "Проверка проделанной работы" },
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Код: ${meeting.id}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { /* TODO: копировать */ }) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Копировать",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}