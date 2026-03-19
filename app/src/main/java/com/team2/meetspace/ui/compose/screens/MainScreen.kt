package com.team2.meetspace.ui.compose.screens

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team2.meetspace.Dependencies
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.ui.compose.components.MeetingCreateBottomSheet
import com.team2.meetspace.ui.viewModel.MainViewModel
import com.team2.meetspace.ui.viewModel.MeetingEditBottomSheetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    bottomSheetViewModel: MeetingEditBottomSheetViewModel = viewModel(),
    onJoinMeeting: () -> Unit = {},
    onEnterMeeting: (String) -> Unit = {},
    onMeetingButtonClicked: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

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

            Button(
                onClick = { viewModel.showCreateSheet() },
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.5f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            )
            {
                Text("Создать встречу")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onJoinMeeting,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.5f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            )
            {
                Text("Присоединиться")
            }

            Spacer(modifier = Modifier.height(40.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(20.dp))
            if (
                !Dependencies.NetworkHelper().checkConnection(LocalContext.current.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет подключения к интернету")
                }
            }
            else if (state.upcomingMeetings.isEmpty()) {
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
                            onEnterClick = { onEnterMeeting(meeting.roomIdentifier) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

        }

        if (state.showCreateBottomSheet) {
            MeetingCreateBottomSheet(
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
    }
}

@Composable
fun MeetingCard(meeting: Meeting, onEnterClick: (String) -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Код: ${meeting.roomIdentifier}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Код встречи", meeting.roomIdentifier)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Код скопирован", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Код встречи", meeting.roomIdentifier)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Код скопирован", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Копировать",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Button(
                onClick = { onEnterClick(meeting.roomIdentifier) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
            ) {
                Text("Войти", fontSize = 14.sp)
            }
        }
    }
}