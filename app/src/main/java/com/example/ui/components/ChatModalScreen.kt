package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity

@Composable
fun ChatModalScreen(
    hostName: String,
    messages: List<ChatMessageEntity>,
    onSendMessage: (String) -> Unit,
    onCloseChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_modal_screen"),
        color = Color(0xFF121212)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1E1C24),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onCloseChat,
                        modifier = Modifier.testTag("back_from_chat_button")
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF4F378B)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFFEADDFF),
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = hostName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Active Trip Messaging",
                            fontSize = 11.sp,
                            color = Color(0xFF22C55E)
                        )
                    }

                    IconButton(
                        onClick = { /* Simulated Call */ },
                        modifier = Modifier.testTag("call_host_button")
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Call Host",
                            tint = Color(0xFFD0BCFF)
                        )
                    }
                }
            }

            // Message List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    val isMe = msg.senderId == "CURRENT_RIDER"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMe) 16.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 16.dp
                            ),
                            color = if (isMe) Color(0xFF4F378B) else Color(0xFF2B2930)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.content,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 9.sp,
                                    color = Color(0xFFCAC4D0)
                                )
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1E1C24)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input"),
                        placeholder = { Text("Type message to driver...", fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD0BCFF),
                            unfocusedBorderColor = Color(0xFF49454F),
                            focusedContainerColor = Color(0xFF2B2930),
                            unfocusedContainerColor = Color(0xFF2B2930)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                onSendMessage(textInput)
                                textInput = ""
                            }
                        },
                        modifier = Modifier
                            .background(Color(0xFFD0BCFF), shape = CircleShape)
                            .testTag("send_chat_button")
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color(0xFF381E72)
                        )
                    }
                }
            }
        }
    }
}
