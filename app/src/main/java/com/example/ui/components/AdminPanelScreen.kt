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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.DriverProfileEntity
import com.example.data.model.KycStatus

@Composable
fun AdminPanelScreen(
    driversList: List<DriverProfileEntity>,
    carpoolRate: Double,
    cabRate: Double,
    autoRate: Double,
    bikeRate: Double,
    isNightSurge: Boolean,
    onApproveDriver: (String) -> Unit,
    onRejectDriver: (String) -> Unit,
    onUpdateRates: (Double, Double, Double, Double) -> Unit,
    onToggleNightSurge: (Boolean) -> Unit,
    onResetDemoData: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editCarpoolRate by remember { mutableStateOf(carpoolRate.toString()) }
    var editCabRate by remember { mutableStateOf(cabRate.toString()) }
    var editAutoRate by remember { mutableStateOf(autoRate.toString()) }
    var editBikeRate by remember { mutableStateOf(bikeRate.toString()) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_panel_screen"),
        color = Color(0xFF121212)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1E1C24),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("back_from_admin_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Platform Admin Control Hub",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onResetDemoData,
                        modifier = Modifier.testTag("reset_demo_data_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color(0xFFEF4444))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Rate Setting Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2930)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFFD0BCFF))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dynamic Tariff & Rate Configuration", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editCarpoolRate,
                                onValueChange = { editCarpoolRate = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Carpool ₹/km") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6D00))
                            )
                            OutlinedTextField(
                                value = editCabRate,
                                onValueChange = { editCabRate = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Cab ₹/km") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD0BCFF))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editAutoRate,
                                onValueChange = { editAutoRate = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Auto ₹/km") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF59E0B))
                            )
                            OutlinedTextField(
                                value = editBikeRate,
                                onValueChange = { editBikeRate = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Bike ₹/km") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF22C55E))
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Night Surge Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Nightlight, contentDescription = null, tint = Color(0xFFF59E0B))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Night Surge Pricing (1.25x)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Applies automated tariff boost", fontSize = 11.sp, color = Color(0xFFCAC4D0))
                                }
                            }

                            Switch(
                                checked = isNightSurge,
                                onCheckedChange = onToggleNightSurge,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF59E0B))
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val c = editCarpoolRate.toDoubleOrNull() ?: carpoolRate
                                val cb = editCabRate.toDoubleOrNull() ?: cabRate
                                val a = editAutoRate.toDoubleOrNull() ?: autoRate
                                val b = editBikeRate.toDoubleOrNull() ?: bikeRate
                                onUpdateRates(c, cb, a, b)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_admin_rates_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF), contentColor = Color(0xFF381E72))
                        ) {
                            Text("Save Tariff Rates", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Driver Verification Queue
                Text("Driver & Host Verification Queue (${driversList.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF))
                Spacer(modifier = Modifier.height(10.dp))

                if (driversList.isEmpty()) {
                    Text("No registered drivers found.", color = Color(0xFFCAC4D0), fontSize = 13.sp)
                } else {
                    driversList.forEach { driver ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .testTag("admin_driver_card_${driver.id}"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2930)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = driver.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(text = "${driver.phone} • ${driver.vehicleModel}", fontSize = 11.sp, color = Color(0xFFCAC4D0))
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = when (driver.kycStatus) {
                                            KycStatus.APPROVED -> Color(0xFF22C55E).copy(alpha = 0.2f)
                                            KycStatus.PENDING_ADMIN_APPROVAL -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                            else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                        }
                                    ) {
                                        Text(
                                            text = driver.kycStatus.name,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (driver.kycStatus) {
                                                KycStatus.APPROVED -> Color(0xFF22C55E)
                                                KycStatus.PENDING_ADMIN_APPROVAL -> Color(0xFFF59E0B)
                                                else -> Color(0xFFEF4444)
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Plate Type: ${if (driver.isCommercialPlate) "Yellow Commercial" else "White Private Carpool"}",
                                    fontSize = 11.sp,
                                    color = Color(0xFFCAC4D0)
                                )
                                Text(
                                    text = "DL Photo: ${driver.dlPhotoUrl} | RC Photo: ${driver.rcPhotoUrl} | Selfie: ${driver.selfiePhotoUrl}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )

                                if (driver.kycStatus == KycStatus.PENDING_ADMIN_APPROVAL) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onApproveDriver(driver.id) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("approve_driver_button_${driver.id}"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.Black),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve KYC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { onRejectDriver(driver.id) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("reject_driver_button_${driver.id}"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reject", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
