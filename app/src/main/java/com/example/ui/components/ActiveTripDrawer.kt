package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import com.example.data.local.TripEntity
import com.example.data.model.TripStatus

@Composable
fun ActiveTripDrawer(
    trip: TripEntity,
    cancelCountdownSeconds: Int,
    otpInput: String,
    otpError: String?,
    onOtpInputChanged: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onCancelTrip: () -> Unit,
    onCompleteTrip: () -> Unit,
    onOpenChat: () -> Unit,
    onTriggerSos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_trip_drawer"),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = Color(0xFF1E1C24),
        shadowElevation = 20.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Drag Handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(36.dp)
                    .height(4.dp)
                    .background(Color(0xFF49454F), shape = CircleShape)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Status Banner Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when (trip.status) {
                            TripStatus.ACCEPTED.name -> Icons.Default.DirectionsCar
                            TripStatus.IN_PROGRESS.name -> Icons.Default.Navigation
                            TripStatus.COMPLETED.name -> Icons.Default.CheckCircle
                            else -> Icons.Default.LocalTaxi
                        },
                        contentDescription = null,
                        tint = when (trip.status) {
                            TripStatus.ACCEPTED.name -> Color(0xFF38BDF8)
                            TripStatus.IN_PROGRESS.name -> Color(0xFF22C55E)
                            TripStatus.COMPLETED.name -> Color(0xFF22C55E)
                            else -> Color(0xFFF59E0B)
                        },
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = when (trip.status) {
                                TripStatus.SEARCHING.name -> "Searching Nearest Driver..."
                                TripStatus.ACCEPTED.name -> "Driver Arriving • Handshake OTP Required"
                                TripStatus.IN_PROGRESS.name -> "On the Way to Destination"
                                TripStatus.COMPLETED.name -> "Trip Arrived Successfully!"
                                TripStatus.CANCELLED.name -> "Trip Cancelled"
                                else -> "Active Trip"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${trip.pickupName} ➔ ${trip.dropName}",
                            fontSize = 11.sp,
                            color = Color(0xFFCAC4D0)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF381E72)
                ) {
                    Text(
                        text = "₹${trip.totalFare.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD0BCFF),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Simulated Trip Progress Bar for IN_PROGRESS
            if (trip.status == TripStatus.IN_PROGRESS.name) {
                LinearProgressIndicator(
                    progress = { 0.65f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .testTag("trip_progress_bar"),
                    color = Color(0xFF22C55E),
                    trackColor = Color(0xFF2B2930)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Driver/Host Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2930)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    .size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = trip.hostName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = trip.vehicleDetails,
                                fontSize = 11.sp,
                                color = Color(0xFFCAC4D0)
                            )
                        }
                    }

                    // Handshake Security OTP Display
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFF6D00).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6D00))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "START OTP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6D00)
                            )
                            Text(
                                text = trip.otp,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.testTag("display_otp_code")
                            )
                        }
                    }
                }
            }

            // Handshake OTP Verification Section (When driver arrives / status ACCEPTED)
            if (trip.status == TripStatus.ACCEPTED.name) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF36343B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Key,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Driver Handshake Security Check",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = onOtpInputChanged,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("otp_verification_input"),
                                placeholder = { Text("Enter 4-digit OTP", fontSize = 12.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF22C55E),
                                    unfocusedBorderColor = Color(0xFF49454F)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = onVerifyOtp,
                                modifier = Modifier.testTag("verify_otp_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF22C55E),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Verify & Start", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (otpError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = otpError,
                                fontSize = 11.sp,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row: Chat, SOS, Complete, Cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // In-App Chat
                OutlinedButton(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_trip_chat_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD0BCFF))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Emergency SOS Pink Shield Button
                Button(
                    onClick = onTriggerSos,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("emergency_sos_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEC4899),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SOS Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cancel / Finish Buttons
            if (trip.status == TripStatus.IN_PROGRESS.name) {
                Button(
                    onClick = onCompleteTrip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("complete_trip_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22C55E),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Arrived & Complete Trip", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            } else if (trip.status != TripStatus.COMPLETED.name && trip.status != TripStatus.CANCELLED.name) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isFreeCancel = cancelCountdownSeconds > 0
                    Column {
                        Text(
                            text = if (isFreeCancel) "Free Cancellation Timer" else "Cancellation Fee May Apply",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isFreeCancel) Color(0xFF22C55E) else Color(0xFFEF4444)
                        )
                        if (isFreeCancel) {
                            val mins = cancelCountdownSeconds / 60
                            val secs = cancelCountdownSeconds % 60
                            Text(
                                text = String.format("%02d:%02d remaining", mins, secs),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onCancelTrip,
                        modifier = Modifier.testTag("cancel_trip_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel Ride", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
