package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RideMode

@Composable
fun ModeSwitcherBar(
    selectedMode: RideMode,
    isPinkShieldActive: Boolean,
    isNightSurgeActive: Boolean,
    kycStatus: String?,
    onModeSelected: (RideMode) -> Unit,
    onTogglePinkShield: () -> Unit,
    onToggleNightMode: () -> Unit,
    onOpenKycScreen: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("mode_switcher_bar"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Title & Action Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFF6D00)
                    ) {
                        Text(
                            text = "YATRA",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "X",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Kanpur",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Action Icons (Pink Shield, Night Surge, Driver KYC, Admin Panel)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pink Shield Toggle Button
                    val pinkBg by animateColorAsState(
                        targetValue = if (isPinkShieldActive) Color(0xFFEC4899) else MaterialTheme.colorScheme.surfaceVariant,
                        label = "pinkShieldBg"
                    )
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onTogglePinkShield() }
                            .testTag("pink_shield_button"),
                        shape = CircleShape,
                        color = pinkBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Female,
                                contentDescription = "Pink Shield",
                                tint = if (isPinkShieldActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Pink",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPinkShieldActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Night Surge Toggle
                    IconButton(
                        onClick = onToggleNightMode,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("night_mode_toggle")
                    ) {
                        Icon(
                            Icons.Default.Nightlight,
                            contentDescription = "Toggle Night Surge Mode",
                            tint = if (isNightSurgeActive) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // KYC Status Badge Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenKycScreen() }
                            .testTag("kyc_status_badge"),
                        shape = RoundedCornerShape(12.dp),
                        color = when (kycStatus) {
                            "APPROVED" -> Color(0xFF22C55E).copy(alpha = 0.15f)
                            "PENDING_ADMIN_APPROVAL" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Badge,
                                contentDescription = "Driver KYC",
                                tint = when (kycStatus) {
                                    "APPROVED" -> Color(0xFF16A34A)
                                    "PENDING_ADMIN_APPROVAL" -> Color(0xFFD97706)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (kycStatus) {
                                    "APPROVED" -> "Driver Verified"
                                    "PENDING_ADMIN_APPROVAL" -> "KYC Pending"
                                    else -> "Apply KYC"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (kycStatus) {
                                    "APPROVED" -> Color(0xFF16A34A)
                                    "PENDING_ADMIN_APPROVAL" -> Color(0xFFD97706)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Admin Panel Button
                    IconButton(
                        onClick = onOpenAdminPanel,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("admin_panel_button")
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Panel",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dual Mode Selector Switcher Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Carpool / Seat Share Tab
                val isCarpool = selectedMode == RideMode.CARPOOL
                val carpoolBg by animateColorAsState(
                    targetValue = if (isCarpool) Color(0xFFFF6D00) else Color.Transparent,
                    label = "carpoolBg"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(carpoolBg)
                        .clickable { onModeSelected(RideMode.CARPOOL) }
                        .padding(vertical = 10.dp)
                        .testTag("mode_carpool_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = "Carpool Mode",
                            tint = if (isCarpool) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Carpool / Seat Share",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCarpool) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Fuel Rate Split",
                                fontSize = 9.sp,
                                color = if (isCarpool) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Commercial Cab / Auto / Bike Tab
                val isCab = selectedMode == RideMode.CAB_AUTO_BIKE
                val cabBg by animateColorAsState(
                    targetValue = if (isCab) MaterialTheme.colorScheme.primary else Color.Transparent,
                    label = "cabBg"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(cabBg)
                        .clickable { onModeSelected(RideMode.CAB_AUTO_BIKE) }
                        .padding(vertical = 10.dp)
                        .testTag("mode_cab_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalTaxi,
                            contentDescription = "Cab Mode",
                            tint = if (isCab) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Book Cab / Auto",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCab) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Instant Commercial",
                                fontSize = 9.sp,
                                color = if (isCab) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
