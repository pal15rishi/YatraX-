package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AuthModalScreen(
    onSendOtp: (String) -> String, // returns generated OTP
    onVerifyOtp: (String, String, String) -> Boolean, // (phone, enteredOtp, role) -> success
    onDismiss: () -> Unit
) {
    var phoneInput by remember { mutableStateOf("9876543210") }
    var otpInput by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var generatedOtpCode by remember { mutableStateOf("1234") }
    var selectedRole by remember { mutableStateOf("USER") } // "USER" or "ADMIN"
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = { /* Require login before dismissing */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("auth_modal_screen"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C24)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Brand Badge Header
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFF6D00).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6D00).copy(alpha = 0.3f))
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = "YatraX Security",
                        tint = Color(0xFFFF6D00),
                        modifier = Modifier
                            .padding(12.dp)
                            .size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF6D00)
                    ) {
                        Text(
                            text = "YATRA",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "X",
                        color = Color(0xFFD0BCFF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Text(
                        text = " • Login",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = if (isOtpSent) "Enter 4-digit verification code" else "Enter phone number to continue",
                    fontSize = 12.sp,
                    color = Color(0xFFCAC4D0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                if (!isOtpSent) {
                    // STEP 1: Phone Number Input Form
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = {
                            if (it.length <= 10) {
                                phoneInput = it.filter { char -> char.isDigit() }
                                errorMessage = null
                                // Auto detect admin phone number
                                if (phoneInput == "9999999999") {
                                    selectedRole = "ADMIN"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_phone_input"),
                        label = { Text("Mobile Number") },
                        prefix = { Text("+91 ", color = Color.White, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFFF6D00)) },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF6D00),
                            unfocusedBorderColor = Color(0xFF49454F),
                            focusedLabelColor = Color(0xFFFF6D00),
                            unfocusedLabelColor = Color(0xFFCAC4D0),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset Quick Login Helper Chips
                    Text(
                        text = "Quick Demo Accounts:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCAC4D0),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    phoneInput = "9876543210"
                                    selectedRole = "USER"
                                    errorMessage = null
                                }
                                .testTag("quick_user_login_chip"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedRole == "USER") Color(0xFF381E72) else Color(0xFF2B2930),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedRole == "USER") Color(0xFFD0BCFF) else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFD0BCFF), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("User Ride", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("9876543210", fontSize = 10.sp, color = Color(0xFFCAC4D0))
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    phoneInput = "9999999999"
                                    selectedRole = "ADMIN"
                                    errorMessage = null
                                }
                                .testTag("quick_admin_login_chip"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedRole == "ADMIN") Color(0xFF701200) else Color(0xFF2B2930),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedRole == "ADMIN") Color(0xFFFF6D00) else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFFFF6D00), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Admin Control", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("9999999999", fontSize = 10.sp, color = Color(0xFFFFB4A9))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (phoneInput.length < 10) {
                                errorMessage = "Please enter a valid 10-digit mobile number."
                            } else {
                                generatedOtpCode = onSendOtp(phoneInput)
                                isOtpSent = true
                                errorMessage = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("send_otp_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6D00),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Send OTP Verification", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // STEP 2: OTP Verification Form
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF22C55E).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "OTP sent to +91 $phoneInput",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "🔑 Demo Security OTP Code: $generatedOtpCode",
                                    fontSize = 12.sp,
                                    color = Color(0xFF22C55E),
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = {
                            if (it.length <= 4) {
                                otpInput = it.filter { char -> char.isDigit() }
                                errorMessage = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_otp_input"),
                        label = { Text("4-Digit Verification OTP") },
                        placeholder = { Text(generatedOtpCode) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF22C55E)) },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF22C55E),
                            unfocusedBorderColor = Color(0xFF49454F),
                            focusedLabelColor = Color(0xFF22C55E),
                            unfocusedLabelColor = Color(0xFFCAC4D0),
                            cursorColor = Color.White,
                            focusedPlaceholderColor = Color(0xFFCAC4D0),
                            unfocusedPlaceholderColor = Color(0xFFCAC4D0)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val roleToAssign = if (phoneInput == "9999999999" || selectedRole == "ADMIN") "ADMIN" else "USER"
                            val success = onVerifyOtp(phoneInput, otpInput, roleToAssign)
                            if (!success) {
                                errorMessage = "Invalid OTP code. Please enter $generatedOtpCode or 1234."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("verify_otp_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22C55E),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Verify OTP & Login", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    TextButton(
                        onClick = {
                            isOtpSent = false
                            otpInput = ""
                            errorMessage = null
                        }
                    ) {
                        Text("Change Phone Number", color = Color(0xFFD0BCFF), fontSize = 12.sp)
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFF2B8B5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
