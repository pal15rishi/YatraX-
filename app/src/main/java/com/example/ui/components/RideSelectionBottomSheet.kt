package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TripEntity
import com.example.data.model.KanpurLocation
import com.example.data.model.RideMode
import com.example.data.model.VehicleType
import kotlin.math.roundToInt

@Composable
fun RideSelectionBottomSheet(
    rideMode: RideMode,
    pickupLocation: KanpurLocation,
    dropLocation: KanpurLocation,
    isPinkShieldActive: Boolean,
    isNightSurgeActive: Boolean,
    surgeMultiplier: Double,
    availableCarpools: List<TripEntity>,
    carpoolRatePerKm: Double,
    cabRatePerKm: Double,
    autoRatePerKm: Double,
    bikeRatePerKm: Double,
    onBookCarpool: (TripEntity) -> Unit,
    onBookCabOption: (VehicleType, Double, Double) -> Unit,
    onOpenCreateOffer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Distance estimate in Kanpur (mocked Euclidean based on lat/lng)
    val dx = dropLocation.longitude - pickupLocation.longitude
    val dy = dropLocation.latitude - pickupLocation.latitude
    val distanceKm = (kotlin.math.sqrt(dx * dx + dy * dy) * 110.0).coerceAtLeast(3.0)
    val roundedDist = (distanceKm * 10).roundToInt() / 10.0

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ride_selection_bottom_sheet"),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = Color(0xFF1E1C24), // Dark M3 Container
        shadowElevation = 16.dp,
        tonalElevation = 8.dp
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

            // Trip Route Summary & Distance Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (rideMode == RideMode.CARPOOL) "Available Carpools" else "Commercial Cabs & Autos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${pickupLocation.name} ➔ ${dropLocation.name} (~${roundedDist} km)",
                        fontSize = 12.sp,
                        color = Color(0xFFCAC4D0)
                    )
                }

                if (isNightSurgeActive || surgeMultiplier > 1.0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Nightlight,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Night Surge ${surgeMultiplier}x",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (rideMode == RideMode.CARPOOL) {
                // CARPOOL MODE: Filter carpools matching Pink Shield if active
                val filteredPools = availableCarpools.filter { trip ->
                    if (isPinkShieldActive) trip.hostIsFemale else true
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredPools.size} Verified Hosts Nearby",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD0BCFF)
                    )

                    OutlinedButton(
                        onClick = onOpenCreateOffer,
                        modifier = Modifier.testTag("host_carpool_offer_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6D00)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Offer Seat / Driver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredPools.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFF2B2930), shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No matching pools currently.",
                                color = Color(0xFFCAC4D0),
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap 'Offer Seat' to host a carpool for Kanpur daily commuters!",
                                color = Color(0xFFFF6D00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredPools) { trip ->
                            CarpoolItemCard(
                                trip = trip,
                                onBook = { onBookCarpool(trip) }
                            )
                        }
                    }
                }

            } else {
                // COMMERCIAL CAB / AUTO / BIKE MODE
                val options = listOf(
                    Triple(VehicleType.CAB_SEDAN, cabRatePerKm, Icons.Default.LocalTaxi),
                    Triple(VehicleType.CAB_HATCHBACK, cabRatePerKm * 0.85, Icons.Default.LocalTaxi),
                    Triple(VehicleType.AUTO_RICKSHAW, autoRatePerKm, Icons.Default.ElectricRickshaw),
                    Triple(VehicleType.EXPRESS_BIKE, bikeRatePerKm, Icons.Default.TwoWheeler)
                )

                var selectedVehicleType by remember { mutableStateOf(VehicleType.CAB_SEDAN) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(options) { (vType, baseRate, icon) ->
                        val fare = (roundedDist * baseRate * surgeMultiplier).roundToInt().toDouble()
                        val isSelected = selectedVehicleType == vType

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedVehicleType = vType }
                                .testTag("cab_option_${vType.name.lowercase()}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF4F378B) else Color(0xFF2B2930)
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD0BCFF)) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color(0xFFD0BCFF),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = vType.displayName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "ETA 3 mins • ${vType.capacity} Capacity",
                                            fontSize = 11.sp,
                                            color = Color(0xFFCAC4D0)
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${fare.toInt()}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFD0BCFF)
                                    )
                                    Text(
                                        text = "Fixed Tariff",
                                        fontSize = 10.sp,
                                        color = Color(0xFFCAC4D0)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Instant Booking Action Button
                val selectedOption = options.first { it.first == selectedVehicleType }
                val selectedFare = (roundedDist * selectedOption.second * surgeMultiplier).roundToInt().toDouble()

                Button(
                    onClick = { onBookCabOption(selectedVehicleType, roundedDist, selectedFare) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_book_cab_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD0BCFF),
                        contentColor = Color(0xFF381E72)
                    )
                ) {
                    Text(
                        text = "Book ${selectedVehicleType.displayName} • ₹${selectedFare.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CarpoolItemCard(
    trip: TripEntity,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("carpool_item_${trip.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2930)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Host Avatar Icon
                Surface(
                    shape = CircleShape,
                    color = if (trip.hostIsFemale) Color(0xFFEC4899).copy(alpha = 0.2f) else Color(0xFFFF6D00).copy(alpha = 0.2f)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (trip.hostIsFemale) Color(0xFFEC4899) else Color(0xFFFF6D00),
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = trip.hostName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "KYC Verified",
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(14.dp)
                        )
                        if (trip.hostIsFemale) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Female,
                                contentDescription = "Pink Shield Host",
                                tint = Color(0xFFEC4899),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = trip.vehicleDetails,
                        fontSize = 11.sp,
                        color = Color(0xFFCAC4D0)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${trip.hostRating} • ${trip.departureTime}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCAC4D0)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${trip.pricePerSeatOrKm.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFF6D00)
                )
                Text(
                    text = "${trip.availableSeats} seats left",
                    fontSize = 10.sp,
                    color = Color(0xFFCAC4D0)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onBook,
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("book_carpool_seat_button_${trip.id}"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6D00),
                        contentColor = Color.White
                    )
                ) {
                    Text("Book Seat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
