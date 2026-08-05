package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.data.local.DriverProfileEntity
import com.example.data.model.KycStatus
import com.example.data.model.VehicleType

@Composable
fun KycOnboardingScreen(
    existingDriver: DriverProfileEntity?,
    onSubmitKyc: (DriverProfileEntity) -> Unit,
    onClose: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(existingDriver?.name ?: "") }
    var phone by remember { mutableStateOf(existingDriver?.phone ?: "") }
    var vehicleModel by remember { mutableStateOf(existingDriver?.vehicleModel ?: "") }
    var vehicleNumber by remember { mutableStateOf(existingDriver?.vehicleNumber ?: "") }
    var selectedVehicleType by remember { mutableStateOf(existingDriver?.vehicleType ?: VehicleType.CAB_SEDAN) }
    var isCommercialPlate by remember { mutableStateOf(existingDriver?.isCommercialPlate ?: true) }
    var seatsCapacity by remember { mutableStateOf(existingDriver?.seatsCapacity?.toString() ?: "3") }

    var dlUploaded by remember { mutableStateOf(existingDriver?.dlPhotoUrl?.isNotEmpty() == true) }
    var rcUploaded by remember { mutableStateOf(existingDriver?.rcPhotoUrl?.isNotEmpty() == true) }
    var selfieUploaded by remember { mutableStateOf(existingDriver?.selfiePhotoUrl?.isNotEmpty() == true) }

    var formError by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("kyc_onboarding_screen"),
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
                            modifier = Modifier.testTag("back_from_kyc_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Driver & Host KYC Verification",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenAdminPanel() }
                            .testTag("open_admin_portal_button"),
                        color = Color(0xFFFF6D00).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6D00))
                    ) {
                        Text(
                            text = "Owner Admin",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6D00),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Status Alert Card
                if (existingDriver != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (existingDriver.kycStatus) {
                                KycStatus.APPROVED -> Color(0xFF22C55E).copy(alpha = 0.15f)
                                KycStatus.PENDING_ADMIN_APPROVAL -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                KycStatus.REJECTED -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                else -> Color(0xFF2B2930)
                            }
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = when (existingDriver.kycStatus) {
                                    KycStatus.APPROVED -> Color(0xFF22C55E)
                                    KycStatus.PENDING_ADMIN_APPROVAL -> Color(0xFFF59E0B)
                                    KycStatus.REJECTED -> Color(0xFFEF4444)
                                    else -> Color.White
                                },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Current Status: ${existingDriver.kycStatus.name}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = when (existingDriver.kycStatus) {
                                        KycStatus.APPROVED -> "You are verified to accept commercial rides & carpools!"
                                        KycStatus.PENDING_ADMIN_APPROVAL -> "Your documents are currently under review by Admin."
                                        KycStatus.REJECTED -> "Verification rejected. Please re-upload documents."
                                        else -> "Fill form to start driving."
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFFCAC4D0)
                                )
                            }
                        }
                    }
                }

                Text("Personal & Vehicle Info", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF))
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kyc_name_input"),
                    label = { Text("Full Name (as per DL)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD0BCFF))
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kyc_phone_input"),
                    label = { Text("Phone Number (+91)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD0BCFF))
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = vehicleModel,
                    onValueChange = { vehicleModel = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kyc_vehicle_model_input"),
                    label = { Text("Vehicle Make & Model (e.g., Swift Dzire)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD0BCFF))
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = vehicleNumber,
                    onValueChange = { vehicleNumber = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kyc_vehicle_number_input"),
                    label = { Text("Registration Number (e.g., UP 78 AB 1234)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD0BCFF))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Commercial / Private Plate Classification", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF))
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isCommercialPlate,
                        onClick = { isCommercialPlate = true },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF6D00))
                    )
                    Text("Commercial Yellow Plate (Commercial Cab/Auto)", fontSize = 12.sp, color = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !isCommercialPlate,
                        onClick = { isCommercialPlate = false },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF6D00))
                    )
                    Text("Private White Plate (Private Seat Share Carpool)", fontSize = 12.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Mandatory Document Photo Verification", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF))
                Spacer(modifier = Modifier.height(10.dp))

                // Upload 1: Driving License
                DocUploadCard(
                    title = "Driving License (DL)",
                    isUploaded = dlUploaded,
                    testTag = "upload_dl_button",
                    onUploadClick = { dlUploaded = true }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Upload 2: Vehicle RC
                DocUploadCard(
                    title = "Vehicle RC Document",
                    isUploaded = rcUploaded,
                    testTag = "upload_rc_button",
                    onUploadClick = { rcUploaded = true }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Upload 3: Live Selfie
                DocUploadCard(
                    title = "Live Face Selfie Match",
                    isUploaded = selfieUploaded,
                    testTag = "upload_selfie_button",
                    onUploadClick = { selfieUploaded = true }
                )

                if (formError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = formError!!, color = Color(0xFFEF4444), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank() || vehicleNumber.isBlank()) {
                            formError = "Please enter name, phone, and vehicle registration."
                        } else if (!dlUploaded || !rcUploaded || !selfieUploaded) {
                            formError = "Please upload DL, RC, and Live Selfie photos."
                        } else {
                            val newProfile = DriverProfileEntity(
                                id = existingDriver?.id ?: "DRV_${System.currentTimeMillis()}",
                                name = name,
                                phone = phone,
                                vehicleModel = vehicleModel,
                                vehicleNumber = vehicleNumber,
                                vehicleType = selectedVehicleType,
                                isCommercialPlate = isCommercialPlate,
                                isFemale = false,
                                dlPhotoUrl = "dl_photo_verified.png",
                                rcPhotoUrl = "rc_photo_verified.png",
                                selfiePhotoUrl = "selfie_photo_verified.png",
                                kycStatus = KycStatus.PENDING_ADMIN_APPROVAL,
                                seatsCapacity = seatsCapacity.toIntOrNull() ?: 3,
                                currentRating = existingDriver?.currentRating ?: 4.9
                            )
                            onSubmitKyc(newProfile)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_kyc_form_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6D00),
                        contentColor = Color.White
                    )
                ) {
                    Text("Submit KYC Documents for Review", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DocUploadCard(
    title: String,
    isUploaded: Boolean,
    testTag: String,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onUploadClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = if (isUploaded) Color(0xFF22C55E).copy(alpha = 0.15f) else Color(0xFF2B2930)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUploaded) Color(0xFF22C55E) else Color(0xFF49454F)
        )
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
                    if (isUploaded) Icons.Default.CheckCircle else Icons.Default.Upload,
                    contentDescription = null,
                    tint = if (isUploaded) Color(0xFF22C55E) else Color(0xFFD0BCFF),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = if (isUploaded) "Photo Captured & Staged" else "Tap to simulate capture / upload",
                        fontSize = 11.sp,
                        color = Color(0xFFCAC4D0)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isUploaded) Color(0xFF22C55E) else Color(0xFF4F378B)
            ) {
                Text(
                    text = if (isUploaded) "Uploaded" else "Upload",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
