package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.DriverProfileEntity
import com.example.data.local.KycDocumentEntity
import com.example.data.model.KycStatus
import com.example.data.model.RideMode
import com.example.data.model.TripStatus
import com.example.data.model.VehicleType
import com.example.data.ui.viewmodel.YatraViewModel
import com.example.ui.components.ActiveTripDrawer
import com.example.ui.components.AdminPanelScreen
import com.example.ui.components.AuthModalScreen
import com.example.ui.components.ChatModalScreen
import com.example.ui.components.CreateCarpoolOfferModal
import com.example.ui.components.KanpurMapView
import com.example.ui.components.KycOnboardingScreen
import com.example.ui.components.LocationSearchBar
import com.example.ui.components.ModeSwitcherBar
import com.example.ui.components.RatingReviewModal
import com.example.ui.components.RideSelectionBottomSheet
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.theme.MyApplicationTheme
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class MainActivity : ComponentActivity(), PaymentResultListener {
    private var activeViewModel: YatraViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Checkout.preload(applicationContext)
        } catch (e: Exception) {
            // Handled safely
        }
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val vm: YatraViewModel = viewModel()
                activeViewModel = vm
                YatraAppMainScreen(
                    viewModel = vm,
                    onLaunchRazorpay = { fare, description ->
                        launchRazorpayCheckout(fare, description)
                    }
                )
            }
        }
    }

    private fun launchRazorpayCheckout(fareInRupees: Double, description: String) {
        try {
            val checkout = Checkout()
            val rawKey = try { BuildConfig.RAZORPAY_KEY_ID } catch (e: Exception) { "" }
            val keyId = if (!rawKey.isNullOrBlank() && rawKey != "rzp_test_placeholder") rawKey else "rzp_test_placeholder"
            checkout.setKeyID(keyId)

            val options = JSONObject().apply {
                put("name", "YatraX Ride Booking")
                put("description", description)
                put("currency", "INR")
                val amountInPaise = (fareInRupees * 100).toLong()
                put("amount", if (amountInPaise <= 0) 100 else amountInPaise)

                val prefill = JSONObject().apply {
                    put("email", "rider@yatrax.in")
                    put("contact", "+919876543210")
                }
                put("prefill", prefill)

                val theme = JSONObject().apply {
                    put("color", "#381E72")
                }
                put("theme", theme)
            }
            checkout.open(this, options)
        } catch (e: Exception) {
            activeViewModel?.onRazorpayPaymentError("Checkout launch error: ${e.message}")
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        val pid = razorpayPaymentId ?: ("pay_" + java.util.UUID.randomUUID().toString().take(8))
        activeViewModel?.onRazorpayPaymentSuccess(pid)
    }

    override fun onPaymentError(code: Int, response: String?) {
        val msg = response ?: "Payment cancelled or failed (Code $code)"
        activeViewModel?.onRazorpayPaymentError(msg)
    }
}

@Composable
fun YatraAppMainScreen(
    viewModel: YatraViewModel = viewModel(),
    onLaunchRazorpay: ((Double, String) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val allTrips by viewModel.allTrips.collectAsState()
    val pendingKycs by viewModel.pendingKycs.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var otpInputState by remember { mutableStateOf("") }
    var otpErrorState by remember { mutableStateOf<String?>(null) }
    var activeNotification by remember { mutableStateOf<String?>(null) }

    // Show Snackbar & Ride Notification Popups when updated in ViewModel
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            activeNotification = msg
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF121212)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Full-screen Interactive Vector Kanpur Map Canvas
            KanpurMapView(
                pickupLocation = uiState.pickupLocation,
                dropLocation = uiState.dropLocation,
                onPickupChanged = { viewModel.setPickupLocation(it) },
                onDropChanged = { viewModel.setDropLocation(it) },
                activeTripStatus = uiState.activeTrip?.status?.let { statusStr ->
                    try { TripStatus.valueOf(statusStr) } catch(e: Exception) { null }
                },
                liveVehicleProgress = uiState.liveVehicleProgress,
                isNightMode = uiState.isSimulatedNightMode,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Top Header Controls (Ride Notifications + Mode Switcher + Search Bar + Branding Bar)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                com.example.ui.components.RideNotificationBanner(
                    message = activeNotification,
                    onDismiss = { activeNotification = null }
                )
                // YatraX Branding & Fast Switcher Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E1C24).copy(alpha = 0.95f),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "YatraX",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFF6D00)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF381E72)
                                ) {
                                    Text(
                                        text = "KANPUR",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD0BCFF),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Carpool & Commercial Cab Network",
                                fontSize = 10.sp,
                                color = Color(0xFFCAC4D0)
                            )
                        }

                        // Top Action Buttons: My Bookings, Driver KYC & Admin Portal
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.openMyBookings(true) }
                                    .testTag("my_bookings_tab_button"),
                                color = Color(0xFF381E72),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0BCFF))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.DirectionsCar,
                                        contentDescription = "My Bookings",
                                        tint = Color(0xFFD0BCFF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("My Bookings", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.openKycScreen(true) }
                                    .testTag("driver_kyc_portal_button"),
                                color = Color(0xFF2B2930),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF49454F))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Badge,
                                        contentDescription = "KYC",
                                        tint = Color(0xFFFF6D00),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("KYC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            if (uiState.isLoggedIn && uiState.userRole == "ADMIN") {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { viewModel.openAdminPanel(true) }
                                        .testTag("admin_portal_fab"),
                                    color = Color(0xFF381E72)
                                ) {
                                    Icon(
                                        Icons.Default.AdminPanelSettings,
                                        contentDescription = "Admin",
                                        tint = Color(0xFFFF6D00),
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Mode Switcher Bar (Carpool vs Commercial Cabs)
                ModeSwitcherBar(
                    selectedMode = uiState.currentMode,
                    isPinkShieldActive = uiState.isPinkShieldActive,
                    isNightSurgeActive = uiState.isSimulatedNightMode,
                    kycStatus = uiState.userKyc?.status,
                    isAdmin = uiState.isLoggedIn && uiState.userRole == "ADMIN",
                    userPhone = uiState.authPhone,
                    onModeSelected = { viewModel.selectRideMode(it) },
                    onTogglePinkShield = { viewModel.togglePinkShield() },
                    onToggleNightMode = { viewModel.toggleSimulatedNightMode() },
                    onOpenKycScreen = { viewModel.openKycScreen(true) },
                    onOpenAdminPanel = { viewModel.openAdminPanel(true) },
                    onLogout = { viewModel.logout() }
                )

                // Pickup & Drop Landmark Search Engine
                LocationSearchBar(
                    pickupLocation = uiState.pickupLocation,
                    dropLocation = uiState.dropLocation,
                    pickupQuery = uiState.pickupSearchQuery,
                    dropQuery = uiState.dropSearchQuery,
                    filteredResults = uiState.filteredLocations,
                    onPickupQueryChanged = { viewModel.onPickupSearchQueryChanged(it) },
                    onDropQueryChanged = { viewModel.onDropSearchQueryChanged(it) },
                    onPickupSelected = { viewModel.setPickupLocation(it) },
                    onDropSelected = { viewModel.setDropLocation(it) }
                )
            }

            // 3. Floating Action Controls (Pink Shield & Night Surge Simulation)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                // Pink Shield Women Safety Floating Button
                FloatingActionButton(
                    onClick = { viewModel.togglePinkShield() },
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .testTag("pink_shield_toggle_fab"),
                    containerColor = if (uiState.isPinkShieldActive) Color(0xFFEC4899) else Color(0xFF2B2930),
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = "Pink Shield",
                        tint = if (uiState.isPinkShieldActive) Color.White else Color(0xFFEC4899)
                    )
                }

                // Night Surge Toggle FAB
                FloatingActionButton(
                    onClick = { viewModel.toggleSimulatedNightMode() },
                    modifier = Modifier.testTag("night_surge_toggle_fab"),
                    containerColor = if (uiState.isSimulatedNightMode) Color(0xFFF59E0B) else Color(0xFF2B2930),
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.Nightlight,
                        contentDescription = "Night Surge",
                        tint = if (uiState.isSimulatedNightMode) Color.Black else Color(0xFFF59E0B)
                    )
                }
            }

            // 4. Slide-Up Bottom Drawers (Active Trip vs Ride Selection)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                if (uiState.activeTrip != null) {
                    ActiveTripDrawer(
                        trip = uiState.activeTrip!!,
                        cancelCountdownSeconds = uiState.cancelTimerSeconds,
                        otpInput = otpInputState,
                        otpError = otpErrorState,
                        onOtpInputChanged = {
                            otpInputState = it
                            otpErrorState = null
                        },
                        onVerifyOtp = {
                            val success = viewModel.verifyOtpAndStartTrip(otpInputState)
                            if (!success) {
                                otpErrorState = "Incorrect OTP code. Please check driver app!"
                            }
                        },
                        onCancelTrip = { viewModel.cancelTrip() },
                        onCompleteTrip = { viewModel.completeTrip() },
                        onOpenChat = { viewModel.openChatModal(true) },
                        onTriggerSos = { viewModel.openSosAlert(true) }
                    )
                } else {
                    RideSelectionBottomSheet(
                        rideMode = uiState.currentMode,
                        pickupLocation = uiState.pickupLocation,
                        dropLocation = uiState.dropLocation,
                        isPinkShieldActive = uiState.isPinkShieldActive,
                        isNightSurgeActive = uiState.isSimulatedNightMode,
                        surgeMultiplier = viewModel.calculateSurgeMultiplier(),
                        availableCarpools = allTrips.filter { it.status == TripStatus.SEARCHING.name },
                        carpoolRatePerKm = uiState.fareConfig.carpoolRatePerKm,
                        cabRatePerKm = uiState.fareConfig.cabRatePerKm,
                        autoRatePerKm = uiState.fareConfig.autoRatePerKm,
                        bikeRatePerKm = uiState.fareConfig.bikeRatePerKm,
                        onBookCarpool = { viewModel.bookTrip(it) },
                        onBookCabOption = { vehicleType, dist, fare ->
                            viewModel.createCabBooking(vehicleType, dist, fare)
                        },
                        onOpenCreateOffer = { viewModel.openCreateOffer(true) },
                        onLaunchRazorpay = onLaunchRazorpay
                    )
                }
            }

            // 5. Full Screen & Dialog Overlay Screens
            // Chat Screen
            if (uiState.isChatOpen && uiState.activeTrip != null) {
                val chatMessages = uiState.activeTripMessages.map { dbMsg ->
                    com.example.data.local.ChatMessageEntity(
                        id = dbMsg.id.toString(),
                        tripId = dbMsg.tripId,
                        senderId = dbMsg.senderId,
                        senderName = dbMsg.senderName,
                        content = dbMsg.messageText,
                        timestamp = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(dbMsg.timestamp))
                    )
                }

                ChatModalScreen(
                    hostName = uiState.activeTrip?.hostName ?: "Driver",
                    messages = chatMessages,
                    onSendMessage = { viewModel.sendMessage(it) },
                    onCloseChat = { viewModel.openChatModal(false) }
                )
            }

            // Driver KYC Registration Screen
            if (uiState.isKycScreenOpen) {
                val driverProfile = uiState.userKyc?.let { kyc ->
                    DriverProfileEntity(
                        id = kyc.userId,
                        name = uiState.currentUserProfile?.name ?: "Driver User",
                        phone = "+91 98765 43210",
                        vehicleModel = kyc.vehicleMakeModel,
                        vehicleNumber = kyc.licensePlateNumber,
                        vehicleType = VehicleType.CAB_SEDAN,
                        isCommercialPlate = kyc.plateCategory == "COMMERCIAL",
                        isFemale = false,
                        dlPhotoUrl = kyc.dlPhotoUri,
                        rcPhotoUrl = kyc.rcPhotoUri,
                        selfiePhotoUrl = kyc.liveSelfieUri,
                        kycStatus = try { KycStatus.valueOf(kyc.status) } catch (e: Exception) { KycStatus.PENDING_ADMIN_APPROVAL },
                        seatsCapacity = kyc.seatingCapacity,
                        currentRating = 4.9
                    )
                }

                KycOnboardingScreen(
                    existingDriver = driverProfile,
                    onSubmitKyc = { profile ->
                        viewModel.submitKycForm(
                            dlNumber = "DL-78-2024-9988",
                            dlPhotoUri = profile.dlPhotoUrl,
                            rcNumber = "RC-UP78-1234",
                            rcPhotoUri = profile.rcPhotoUrl,
                            plateCategory = if (profile.isCommercialPlate) "COMMERCIAL" else "PRIVATE_CARPOOL",
                            liveSelfieUri = profile.selfiePhotoUrl,
                            vehicleMakeModel = profile.vehicleModel,
                            licensePlateNumber = profile.vehicleNumber,
                            seatingCapacity = profile.seatsCapacity
                        )
                    },
                    onClose = { viewModel.openKycScreen(false) },
                    onOpenAdminPanel = {
                        viewModel.openKycScreen(false)
                        viewModel.openAdminPanel(true)
                    }
                )
            }

            // Phone + OTP Authentication System Overlay
            if (!uiState.isLoggedIn || uiState.isAuthModalOpen) {
                AuthModalScreen(
                    onSendOtp = { phone -> viewModel.sendOtp(phone) },
                    onVerifyOtp = { phone, otp, role -> viewModel.verifyOtp(phone, otp, role) },
                    onDismiss = { viewModel.openAuthModal(false) }
                )
            }

            // Owner Admin Panel (ONLY accessible if logged in as Admin)
            if (uiState.isAdminPanelOpen && uiState.isLoggedIn && uiState.userRole == "ADMIN") {
                val adminDriversList = pendingKycs.map { kyc ->
                    DriverProfileEntity(
                        id = kyc.id,
                        name = "Driver ${kyc.userId.takeLast(4)}",
                        phone = "+91 94150 11223",
                        vehicleModel = kyc.vehicleMakeModel,
                        vehicleNumber = kyc.licensePlateNumber,
                        vehicleType = VehicleType.CAB_SEDAN,
                        isCommercialPlate = kyc.plateCategory == "COMMERCIAL",
                        isFemale = false,
                        dlPhotoUrl = kyc.dlPhotoUri,
                        rcPhotoUrl = kyc.rcPhotoUri,
                        selfiePhotoUrl = kyc.liveSelfieUri,
                        kycStatus = try { KycStatus.valueOf(kyc.status) } catch (e: Exception) { KycStatus.PENDING_ADMIN_APPROVAL },
                        seatsCapacity = kyc.seatingCapacity,
                        currentRating = 4.9
                    )
                }

                AdminPanelScreen(
                    driversList = adminDriversList,
                    carpoolRate = uiState.fareConfig.carpoolRatePerKm,
                    cabRate = uiState.fareConfig.cabRatePerKm,
                    autoRate = uiState.fareConfig.autoRatePerKm,
                    bikeRate = uiState.fareConfig.bikeRatePerKm,
                    isNightSurge = uiState.isSimulatedNightMode,
                    onApproveDriver = { kycId ->
                        viewModel.approveKyc(kycId, "user_current")
                    },
                    onRejectDriver = { kycId ->
                        viewModel.rejectKyc(kycId, "user_current", "Documents incomplete")
                    },
                    onUpdateRates = { c, cb, a, b ->
                        viewModel.updateFareRates(c, cb, a, b, uiState.fareConfig.nightSurgeMultiplier)
                    },
                    onToggleNightSurge = { viewModel.toggleSimulatedNightMode() },
                    onResetDemoData = { viewModel.resetDemoData() },
                    onClose = { viewModel.openAdminPanel(false) }
                )
            }

            // My Bookings & Active Rides Overlay Modal
            if (uiState.isMyBookingsOpen) {
                com.example.ui.components.MyBookingsModal(
                    trips = allTrips,
                    onSelectActiveTrip = { trip ->
                        viewModel.selectActiveTrip(trip)
                    },
                    onOpenChat = { trip ->
                        viewModel.selectActiveTrip(trip)
                        viewModel.openChatModal(true)
                    },
                    onClose = { viewModel.openMyBookings(false) }
                )
            }

            // SOS Emergency Alert Dialog
            if (uiState.isSosAlertOpen) {
                SosEmergencyDialog(
                    locationName = uiState.pickupLocation.name,
                    onConfirmSos = {
                        viewModel.openSosAlert(false)
                        // Triggered SOS
                    },
                    onDismiss = { viewModel.openSosAlert(false) }
                )
            }

            // Trip Completion Rating Modal
            if (uiState.isRatingModalOpen && uiState.activeTrip != null) {
                RatingReviewModal(
                    hostName = uiState.activeTrip?.hostName ?: "Driver",
                    onSubmitRating = { stars, text ->
                        viewModel.submitRating(stars.toInt(), text)
                    },
                    onDismiss = { viewModel.dismissRatingModal() }
                )
            }

            // Host Carpool Seat Offer Modal
            if (uiState.isCreateOfferOpen) {
                CreateCarpoolOfferModal(
                    pickupName = uiState.pickupLocation.name,
                    dropName = uiState.dropLocation.name,
                    onCreateOffer = { name, vehicle, seats, price ->
                        viewModel.createCarpoolOffer(
                            vehicleMakeModel = vehicle,
                            licensePlate = "UP 78 EV 1001",
                            seats = seats,
                            pricePerSeat = price,
                            pickup = uiState.pickupLocation,
                            drop = uiState.dropLocation,
                            departureTime = "10 mins"
                        )
                    },
                    onDismiss = { viewModel.openCreateOffer(false) }
                )
            }
        }
    }
}
