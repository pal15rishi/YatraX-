package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ActiveTripDrawer
import com.example.ui.components.AdminPanelScreen
import com.example.ui.components.AuthModalScreen
import com.example.ui.components.ChatModalScreen
import com.example.ui.components.CreateCarpoolOfferModal
import com.example.ui.components.KanpurMapView
import com.example.ui.components.KycOnboardingScreen
import com.example.ui.components.LocationSearchBar
import com.example.ui.components.ModeSwitcherBar
import com.example.ui.components.MyBookingsModal
import com.example.ui.components.RatingReviewModal
import com.example.ui.components.RideSelectionBottomSheet
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.YatraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                YatraXApp()
            }
        }
    }
}

@Composable
fun YatraXApp(viewModel: YatraViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var isBookingsModalOpen by remember { mutableStateOf(false) }
    var isKycModalOpen by remember { mutableStateOf(false) }
    var isAdminPanelOpen by remember { mutableStateOf(false) }
    var isAuthModalOpen by remember { mutableStateOf(false) }
    var isCreateCarpoolModalOpen by remember { mutableStateOf(false) }
    var isRatingModalOpen by remember { mutableStateOf(false) }
    var isChatModalOpen by remember { mutableStateOf(false) }

    var offerHostName by remember { mutableStateOf("Rohit Sharma") }
    var offerVehicleDetails by remember { mutableStateOf("Maruti Swift • UP 78 EV 9900") }
    var offerSeats by remember { mutableStateOf("3") }
    var offerPrice by remember { mutableStateOf("60") }

    var authPhoneInput by remember { mutableStateOf("") }
    var otpInputState by remember { mutableStateOf("") }
    var otpErrorState by remember { mutableStateOf<String?>(null) }
    var activeNotification by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            activeNotification = msg
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            KanpurMapView(
                rides = uiState.availableRides,
                selectedRide = uiState.selectedRide,
                userPickupLocation = uiState.userPickupLocation,
                userDropLocation = uiState.userDropLocation,
                activeRide = uiState.activeRide,
                onRideMarkerClick = { ride -> viewModel.selectRide(ride) },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                com.example.ui.components.RideNotificationBanner(
                    message = activeNotification,
                    onDismiss = { activeNotification = null }
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = CircleShape,
                    color = Color(0xFF1E1C24).copy(alpha = 0.95f),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YatraX",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6D00)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF2B2930)
                        ) {
                            Text(
                                text = "KANPUR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE6E1E5),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = { isAuthModalOpen = true },
                            modifier = Modifier.testTag("auth_profile_button")
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = if (uiState.isLoggedIn) Color(0xFF22C55E) else Color(0xFFCAC4D0)
                            )
                        }

                        IconButton(
                            onClick = { isBookingsModalOpen = true },
                            modifier = Modifier.testTag("my_bookings_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (uiState.myBookings.isNotEmpty()) {
                                        Badge { Text(uiState.myBookings.size.toString()) }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.BookOnline,
                                    contentDescription = "My Bookings",
                                    tint = Color(0xFFD0BCFF)
                                )
                            }
                        }

                        IconButton(
                            onClick = { isKycModalOpen = true },
                            modifier = Modifier.testTag("kyc_onboarding_button")
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = "KYC Verification",
                                tint = if (uiState.isKycVerified) Color(0xFF22C55E) else Color(0xFFF59E0B)
                            )
                        }

                        IconButton(
                            onClick = { isAdminPanelOpen = true },
                            modifier = Modifier.testTag("admin_panel_button")
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Fare Settings",
                                tint = Color(0xFF38BDF8)
                            )
                        }
                    }
                }

                ModeSwitcherBar(
                    selectedMode = uiState.travelMode,
                    onModeSelected = { mode -> viewModel.setTravelMode(mode) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LocationSearchBar(
                    pickupQuery = uiState.pickupSearchQuery,
                    dropQuery = uiState.dropSearchQuery,
                    onPickupQueryChange = { q -> viewModel.setPickupSearchQuery(q) },
                    onDropQueryChange = { q -> viewModel.setDropSearchQuery(q) },
                    onSelectLocation = { location, isPickup -> viewModel.selectLocation(location, isPickup) },
                    pickupSuggestions = uiState.pickupSuggestions,
                    dropSuggestions = uiState.dropSuggestions,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (uiState.travelMode == com.example.data.TravelMode.CARPOOL) {
                FloatingActionButton(
                    onClick = { isCreateCarpoolModalOpen = true },
                    containerColor = Color(0xFFFF6D00),
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 280.dp)
                        .testTag("offer_carpool_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Offer Ride", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            IconButton(
                onClick = { viewModel.toggleSosDialog(true) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 180.dp, end = 16.dp)
                    .background(Color(0xFFEF4444), shape = CircleShape)
                    .testTag("sos_emergency_button")
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "SOS",
                    tint = Color.White
                )
            }

            RideSelectionBottomSheet(
                rides = uiState.availableRides,
                selectedRide = uiState.selectedRide,
                travelMode = uiState.travelMode,
                onSelectRide = { ride -> viewModel.selectRide(ride) },
                onBookRide = { ride -> viewModel.bookRide(ride) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            AnimatedVisibility(
                visible = uiState.activeRide != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.activeRide?.let { activeRide ->
                    ActiveTripDrawer(
                        activeRide = activeRide,
                        userRole = uiState.userRole,
                        driverOtpInput = uiState.driverEnteredOtp,
                        onOtpChange = { otp -> viewModel.updateDriverOtpInput(otp) },
                        onVerifyOtp = { viewModel.verifyOtpAndStartTrip() },
                        onUpdateTripStatus = { status -> viewModel.updateTripStatus(status) },
                        onCancelTrip = { viewModel.cancelActiveTrip() },
                        onOpenChat = { isChatModalOpen = true },
                        onOpenRating = { isRatingModalOpen = true }
                    )
                }
            }

            if (isAuthModalOpen) {
                AuthModalScreen(
                    isLoggedIn = uiState.isLoggedIn,
                    userRole = uiState.userRole,
                    currentPhone = uiState.userPhone,
                    generatedOtpCode = uiState.generatedOtpCode,
                    phoneInput = authPhoneInput,
                    otpInput = otpInputState,
                    otpError = otpErrorState,
                    onPhoneInputChange = { authPhoneInput = it },
                    onOtpInputChange = {
                        otpInputState = it
                        otpErrorState = null
                    },
                    onRoleChange = { role -> viewModel.setUserRole(role) },
                    onSendOtp = { viewModel.requestOtp(authPhoneInput) },
                    onVerifyOtp = {
                        val success = viewModel.verifyAuthOtp(otpInputState)
                        if (success) {
                            isAuthModalOpen = false
                            otpInputState = ""
                        } else {
                            otpErrorState = "Invalid OTP code. Try entering ${uiState.generatedOtpCode}"
                        }
                    },
                    onLogout = {
                        viewModel.logoutUser()
                        isAuthModalOpen = false
                    },
                    onDismiss = { isAuthModalOpen = false }
                )
            }

            if (isCreateCarpoolModalOpen) {
                CreateCarpoolOfferModal(
                    hostName = offerHostName,
                    vehicleDetails = offerVehicleDetails,
                    seats = offerSeats,
                    price = offerPrice,
                    onHostNameChange = { offerHostName = it },
                    onVehicleDetailsChange = { offerVehicleDetails = it },
                    onSeatsChange = { offerSeats = it },
                    onPriceChange = { offerPrice = it },
                    onPublishOffer = {
                        viewModel.publishCarpoolOffer(
                            hostName = offerHostName,
                            vehicleDetails = offerVehicleDetails,
                            seats = offerSeats.toIntOrNull() ?: 3,
                            pricePerSeat = offerPrice.toDoubleOrNull() ?: 60.0
                        )
                        isCreateCarpoolModalOpen = false
                    },
                    onDismiss = { isCreateCarpoolModalOpen = false }
                )
            }

            if (isBookingsModalOpen) {
                MyBookingsModal(
                    bookings = uiState.myBookings,
                    onDismiss = { isBookingsModalOpen = false }
                )
            }

            if (isKycModalOpen) {
                KycOnboardingScreen(
                    isVerified = uiState.isKycVerified,
                    driverName = uiState.driverKycData.name,
                    driverPhone = uiState.driverKycData.phone,
                    vehicleModel = uiState.driverKycData.vehicleModel,
                    vehicleNumber = uiState.driverKycData.vehicleNumber,
                    dlPhotoUri = uiState.driverKycData.dlPhotoUri,
                    rcPhotoUri = uiState.driverKycData.rcPhotoUri,
                    onNameChange = { viewModel.updateKycField("name", it) },
                    onPhoneChange = { viewModel.updateKycField("phone", it) },
                    onVehicleModelChange = { viewModel.updateKycField("vehicleModel", it) },
                    onVehicleNumberChange = { viewModel.updateKycField("vehicleNumber", it) },
                    onUploadDlPhoto = { viewModel.updateKycField("dlPhotoUri", "uploaded_dl_stub.jpg") },
                    onUploadRcPhoto = { viewModel.updateKycField("rcPhotoUri", "uploaded_rc_stub.jpg") },
                    onSubmitKyc = {
                        viewModel.submitDriverKyc()
                        isKycModalOpen = false
                    },
                    onDismiss = { isKycModalOpen = false }
                )
            }

            if (isAdminPanelOpen) {
                AdminPanelScreen(
                    carpoolRate = uiState.carpoolPerKmRate.toString(),
                    cabRate = uiState.cabPerKmRate.toString(),
                    autoRate = uiState.autoPerKmRate.toString(),
                    bikeRate = uiState.bikePerKmRate.toString(),
                    surgeMultiplier = uiState.surgeMultiplier.toString(),
                    commissionPercentage = uiState.platformCommissionPercent.toString(),
                    rides = uiState.availableRides,
                    onCarpoolRateChange = { viewModel.updateAdminRate("carpool", it) },
                    onCabRateChange = { viewModel.updateAdminRate("cab", it) },
                    onAutoRateChange = { viewModel.updateAdminRate("auto", it) },
                    onBikeRateChange = { viewModel.updateAdminRate("bike", it) },
                    onSurgeChange = { viewModel.updateAdminRate("surge", it) },
                    onCommissionChange = { viewModel.updateAdminRate("commission", it) },
                    onSaveSettings = { isAdminPanelOpen = false },
                    onDismiss = { isAdminPanelOpen = false }
                )
            }

            if (isRatingModalOpen) {
                RatingReviewModal(
                    activeRide = uiState.activeRide,
                    onSubmitRating = { rating, review ->
                        viewModel.submitRatingAndReview(rating, review)
                        isRatingModalOpen = false
                    },
                    onDismiss = { isRatingModalOpen = false }
                )
            }

            if (isChatModalOpen) {
                ChatModalScreen(
                    chatMessages = uiState.chatMessages,
                    onSendMessage = { text -> viewModel.sendChatMessage(text) },
                    onDismiss = { isChatModalOpen = false }
                )
            }

            if (uiState.showSosDialog) {
                SosEmergencyDialog(
                    onConfirmSos = { viewModel.triggerSosAlert() },
                    onDismiss = { viewModel.toggleSosDialog(false) }
                )
            }
        }
    }
}
