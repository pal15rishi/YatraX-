package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.window.Dialog

@Composable
fun CreateCarpoolOfferModal(
    pickupName: String,
    dropName: String,
    onCreateOffer: (String, String, Int, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var hostName by remember { mutableStateOf("Rohit Sharma") }
    var vehicleDetails by remember { mutableStateOf("Maruti Swift • UP 78 EV 9900") }
    var availableSeats by remember { mutableStateOf("3") }
    var pricePerSeat by remember { mutableStateOf("60") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_carpool_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C24))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Offer Carpool Seat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Share route from $pickupName to $dropName",
                    fontSize = 12.sp,
                    color = Color(0xFFCAC4D0),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = hostName,
                    onValueChange = { hostName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("carpool_host_name_input"),
                    label = { Text("Your Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6D00))
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = vehicleDetails,
                    onValueChange = { vehicleDetails = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("carpool_vehicle_input"),
                    label = { Text("Vehicle & Reg No.") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6D00))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = availableSeats,
                        onValueChange = { availableSeats = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("carpool_seats_input"),
                        label = { Text("Seats") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6D00))
                    )

                    OutlinedTextField(
                        value = pricePerSeat,
                        onValueChange = { pricePerSeat = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("carpool_price_input"),
                        label = { Text("Price (₹/Seat)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6D00))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val seats = availableSeats.toIntOrNull() ?: 3
                        val price = pricePerSeat.toDoubleOrNull() ?: 60.0
                        onCreateOffer(hostName, vehicleDetails, seats, price)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_carpool_offer_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6D00),
                        contentColor = Color.White
                    )
                ) {
                    Text("Publish Carpool Route", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
