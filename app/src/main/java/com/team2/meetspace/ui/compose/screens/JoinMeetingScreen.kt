package com.team2.meetspace.ui.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

data class JoinState(
    val roomCode: String = "",
    val userName: String = "",
    val isCodeValid: Boolean = false,
    val currentStep: JoinRoom = JoinRoom.Code,
    val validStatus: ValidStatus = ValidStatus.None
)

enum class JoinRoom { Code, Name }
enum class ValidStatus { None, NotFound, NotStarted, Valid }

class JoinViewModel : ViewModel() {
    private val _state = mutableStateOf(JoinState())
    val state: State<JoinState> = _state

    fun setInitialCode(code: String) {
        _state.value = _state.value.copy(roomCode = code)
    }

    fun onRoomCodeChanged(newCode: String) {
        _state.value = _state.value.copy(
            roomCode = newCode,
            validStatus = ValidStatus.None
        )
    }

    fun onUserNameChanged(newName: String) {
        _state.value = _state.value.copy(userName = newName)
    }

    fun roomCheck(roomId: String) {
        val status = if (roomId.isNotEmpty()) ValidStatus.Valid else ValidStatus.NotFound
        _state.value = _state.value.copy(
            validStatus = status,
            isCodeValid = status == ValidStatus.Valid
        )
    }

    fun goToNextStep() {
        if (_state.value.currentStep == JoinRoom.Code) {
            if (_state.value.isCodeValid) {
                _state.value = _state.value.copy(currentStep = JoinRoom.Name)
            }
        }
    }

    fun resetValidStatus() {
        _state.value = _state.value.copy(validStatus = ValidStatus.None)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinMeetingScreen(
    initialCode: String = "",
    viewModel: JoinViewModel = viewModel(),
    onCancelButtonClicked: () -> Unit = {},
    onNextButtonClicked: (String, String) -> Unit,
    onMeetingButtonClicked: () -> Unit
) {
    val state by viewModel.state

    LaunchedEffect(initialCode) {
        if (initialCode.isNotEmpty()) {
            viewModel.setInitialCode(initialCode)
            viewModel.roomCheck(initialCode)
        }
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
                    selected = false,
                    onClick = { onCancelButtonClicked() },
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
        when (state.currentStep) {
            JoinRoom.Code -> JoinRoomScreen(
                roomCode = state.roomCode,
                validStatus = state.validStatus,
                onRoomCodeChange = viewModel::onRoomCodeChanged,
                onCheck = viewModel::roomCheck,
                onNext = viewModel::goToNextStep,
                onResetValid = viewModel::resetValidStatus,
                onHomeButtonClicked = onCancelButtonClicked,
                modifier = Modifier.padding(padding)
            )

            JoinRoom.Name -> UserNameScreen(
                userName = state.userName,
                onUserNameChange = viewModel::onUserNameChanged,
                onNext = { onNextButtonClicked(state.roomCode, state.userName) },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun JoinRoomScreen(
    roomCode: String,
    onRoomCodeChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    onHomeButtonClicked: () -> Unit = {},
    onResetValid: () -> Unit,
    onCheck: (String) -> Unit,
    validStatus: ValidStatus
) {
    LaunchedEffect(validStatus) {
        if (validStatus == ValidStatus.Valid) {
            onNext()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Присоединиться к встрече",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            when (validStatus){
                ValidStatus.None ->
                {
                    Text(
                        text = "Номер комнаты",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Left
                    )

                    OutlinedTextField(
                        value = roomCode,
                        onValueChange = onRoomCodeChange,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    )

                    Button(
                        onClick = {
                            onCheck(roomCode)
                        },
                        enabled = roomCode.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Далее", fontSize = 16.sp)
                    }
                }

                ValidStatus.NotFound -> {
                    Text(
                        text = "Сообщение\n ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "Комната с введённым \n номером не найдена")
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onHomeButtonClicked() },
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("На Главную")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { onResetValid() },
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Назад")
                    }
                }

                ValidStatus.NotStarted -> {
                    Text(
                        text = "Сообщение\n ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "Встреча ещё не началась")

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onHomeButtonClicked() },
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("На Главную")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { onResetValid() },
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Назад")
                    }
                }

                else -> { }
            }
        }
    }
}

@Composable
fun UserNameScreen(
    userName: String,
    onUserNameChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Имя пользователя",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ФИО",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Left
        )

        OutlinedTextField(
            value = userName,
            onValueChange = onUserNameChange,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        Button(
            onClick = onNext,
            enabled = userName.trim().isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Далее", fontSize = 16.sp)
        }
    }
}