package com.example.data.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FareConfigEntity
import com.example.data.local.KycDocumentEntity
import com.example.data.local.MessageEntity
import com.example.data.local.RatingEntity
import com.example.data.local.TripEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.YatraDatabase
import com.example.data.model.KANPUR_LANDMARKS
import com.example.data.model.KanpurLocation
import com.example.data.model.KycStatus
import com.example.data.model.RideMode
import com.example.data.model.TripStatus
import com.example.data.model.VehicleType
import com.example.data.repository.YatraRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

data class YatraUiState(
    val currentMode: RideMode = RideMode.CARPOOL,
    val isPinkShieldActive: Boolean = false,
    val pickupLocation: KanpurLocation = KANPUR_LANDMARKS[0], // Kanpur Central
    val dropLocation: KanpurLocation = KANPUR_LANDMARKS[2], // IIT Kanpur
    val pickupSearchQuery: String = "",
    val dropSearchQuery: String = "",
    val filteredLocations: List<KanpurLocation> = emptyList(),
    val activeTrip: TripEntity? = null,
    val activeTripMessages: List<MessageEntity> = emptyList(),
    val currentUserProfile: UserProfileEntity? = null,
    val userKyc: KycDocumentEntity? = null,
    val fareConfig: FareConfigEntity = FareConfigEntity(),
    val isKycScreenOpen: Boolean = false,
    val isAdminPanelOpen: Boolean = false,
    val isChatOpen: Boolean = false,
    val isSosAlertOpen: Boolean = false,
    val isRatingModalOpen: Boolean = false,
    val isCreateOfferOpen: Boolean = false,
    val isSimulatedNightMode: Boolean = false,
    val cancelTimerSeconds: Int = 180, // 3-minute free cancellation window
    val liveVehicleProgress: Float = 0.0f, // 0.0 to 1.0
    val snackbarMessage: String? = null
)

class YatraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: YatraRepository
    private val _uiState = MutableStateFlow(YatraUiState())
    val uiState: StateFlow<YatraUiState> = _uiState.asStateFlow()

    private var searchDebounceJob: Job? = null
    private var tripAnimationJob: Job? = null
    private var cancellationTimerJob: Job? = null

    init {
        val database = YatraDatabase.getDatabase(application)
        repository = YatraRepository(database.yatraDao())

        viewModelScope.launch {
            repository.seedInitialData()
        }

        // Observe Room DB flows
        viewModelScope.launch {
            repository.getUserProfile("user_current").collect { profile ->
                _uiState.value = _uiState.value.copy(currentUserProfile = profile)
            }
        }

        viewModelScope.launch {
            repository.getKycForUser("user_current").collect { kyc ->
                _uiState.value = _uiState.value.copy(userKyc = kyc)
            }
        }

        viewModelScope.launch {
            repository.fareConfig.collect { config ->
                if (config != null) {
                    _uiState.value = _uiState.value.copy(fareConfig = config)
                }
            }
        }
    }

    val pendingKycs: StateFlow<List<KycDocumentEntity>> = repository.pendingKycs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTrips: StateFlow<List<TripEntity>> = repository.allTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated surge multiplier based on actual time (10 PM to 6 AM) or simulated night toggle
    fun calculateSurgeMultiplier(): Double {
        if (_uiState.value.isSimulatedNightMode) return _uiState.value.fareConfig.nightSurgeMultiplier
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return if (currentHour >= 22 || currentHour < 6) {
            _uiState.value.fareConfig.nightSurgeMultiplier
        } else {
            1.0
        }
    }

    fun selectRideMode(mode: RideMode) {
        _uiState.value = _uiState.value.copy(currentMode = mode)
    }

    fun togglePinkShield() {
        val newPink = !_uiState.value.isPinkShieldActive
        _uiState.value = _uiState.value.copy(
            isPinkShieldActive = newPink,
            snackbarMessage = if (newPink) "Pink Shield Activated: Matching verified female drivers" else "Pink Shield Deactivated"
        )
    }

    fun toggleSimulatedNightMode() {
        val newNight = !_uiState.value.isSimulatedNightMode
        _uiState.value = _uiState.value.copy(
            isSimulatedNightMode = newNight,
            snackbarMessage = if (newNight) "Simulated Night Mode Activated (1.2x Surge applied)" else "Day Mode Restored"
        )
    }

    fun setPickupLocation(location: KanpurLocation) {
        _uiState.value = _uiState.value.copy(
            pickupLocation = location,
            pickupSearchQuery = ""
        )
    }

    fun setDropLocation(location: KanpurLocation) {
        _uiState.value = _uiState.value.copy(
            dropLocation = location,
            dropSearchQuery = ""
        )
    }

    fun onPickupSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(pickupSearchQuery = query)
        performDebouncedSearch(query)
    }

    fun onDropSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(dropSearchQuery = query)
        performDebouncedSearch(query)
    }

    private fun performDebouncedSearch(query: String) {
        searchDebounceJob?.cancel()
        if (query.trim().isEmpty()) {
            _uiState.value = _uiState.value.copy(filteredLocations = emptyList())
            return
        }
        searchDebounceJob = viewModelScope.launch {
            delay(500) // 500ms client debouncing
            val results = KANPUR_LANDMARKS.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.address.contains(query, ignoreCase = true)
            }
            _uiState.value = _uiState.value.copy(filteredLocations = results)
        }
    }

    fun bookTrip(trip: TripEntity) {
        val currentUser = _uiState.value.currentUserProfile ?: return
        viewModelScope.launch {
            repository.bookTrip(trip.id, currentUser.id, currentUser.name)
            val updatedTrip = trip.copy(
                status = TripStatus.ACCEPTED.name,
                riderId = currentUser.id,
                riderName = currentUser.name
            )
            _uiState.value = _uiState.value.copy(
                activeTrip = updatedTrip,
                cancelTimerSeconds = 180,
                snackbarMessage = "Ride Booked! Verification OTP: ${updatedTrip.otp}"
            )
            startCancellationTimer()
            observeActiveTripMessages(updatedTrip.id)
        }
    }

    fun createCabBooking(vehicleType: VehicleType, distanceKm: Double, fare: Double) {
        val currentUser = _uiState.value.currentUserProfile ?: return
        val pickup = _uiState.value.pickupLocation
        val drop = _uiState.value.dropLocation
        val generatedOtp = (1000..9999).random().toString()

        val newTrip = TripEntity(
            id = UUID.randomUUID().toString(),
            hostDriverId = "driver_3", // Sample commercial cab driver
            hostName = "Amit Yadav (Cab)",
            hostPhone = "+91 94150 99887",
            hostIsFemale = false,
            hostRating = 4.8f,
            vehicleDetails = "${vehicleType.displayName} • UP 78 T 4321",
            rideMode = RideMode.CAB_AUTO_BIKE.name,
            vehicleType = vehicleType.name,
            pickupName = pickup.name,
            pickupLat = pickup.latitude,
            pickupLng = pickup.longitude,
            dropName = drop.name,
            dropLat = drop.latitude,
            dropLng = drop.longitude,
            departureTime = "Instant",
            availableSeats = vehicleType.capacity,
            totalSeats = vehicleType.capacity,
            pricePerSeatOrKm = fare / distanceKm,
            surgeMultiplier = calculateSurgeMultiplier(),
            isPinkShield = _uiState.value.isPinkShieldActive,
            status = TripStatus.ACCEPTED.name,
            otp = generatedOtp,
            riderId = currentUser.id,
            riderName = currentUser.name,
            distanceKm = distanceKm,
            totalFare = fare
        )

        viewModelScope.launch {
            repository.createTrip(newTrip)
            _uiState.value = _uiState.value.copy(
                activeTrip = newTrip,
                cancelTimerSeconds = 180,
                snackbarMessage = "Cab Requested! Driver assigned. OTP: $generatedOtp"
            )
            startCancellationTimer()
            observeActiveTripMessages(newTrip.id)
        }
    }

    fun verifyOtpAndStartTrip(enteredOtp: String): Boolean {
        val currentTrip = _uiState.value.activeTrip ?: return false
        if (currentTrip.otp == enteredOtp.trim()) {
            viewModelScope.launch {
                repository.updateTripStatus(currentTrip.id, TripStatus.IN_PROGRESS.name)
                val updated = currentTrip.copy(status = TripStatus.IN_PROGRESS.name)
                _uiState.value = _uiState.value.copy(
                    activeTrip = updated,
                    snackbarMessage = "OTP Verified! Trip Started."
                )
                startLiveVehicleSimulation()
            }
            return true
        }
        return false
    }

    private fun startLiveVehicleSimulation() {
        tripAnimationJob?.cancel()
        tripAnimationJob = viewModelScope.launch {
            var progress = 0.0f
            while (progress < 1.0f) {
                delay(1000)
                progress += 0.05f
                _uiState.value = _uiState.value.copy(liveVehicleProgress = progress.coerceAtMost(1.0f))
            }
            // Auto complete trip when destination reached
            completeTrip()
        }
    }

    fun completeTrip() {
        val currentTrip = _uiState.value.activeTrip ?: return
        viewModelScope.launch {
            repository.updateTripStatus(currentTrip.id, TripStatus.COMPLETED.name)
            val updated = currentTrip.copy(status = TripStatus.COMPLETED.name)
            _uiState.value = _uiState.value.copy(
                activeTrip = updated,
                isRatingModalOpen = true,
                snackbarMessage = "You've arrived! Please rate your trip."
            )
            cancellationTimerJob?.cancel()
            tripAnimationJob?.cancel()
        }
    }

    fun cancelTrip() {
        val currentTrip = _uiState.value.activeTrip ?: return
        val isFeeApplied = _uiState.value.cancelTimerSeconds == 0
        viewModelScope.launch {
            repository.updateTripStatus(currentTrip.id, TripStatus.CANCELLED.name)
            _uiState.value = _uiState.value.copy(
                activeTrip = null,
                cancelTimerSeconds = 180,
                snackbarMessage = if (isFeeApplied) "Trip cancelled. Cancellation fee ₹25 recorded." else "Trip cancelled for free."
            )
            cancellationTimerJob?.cancel()
            tripAnimationJob?.cancel()
        }
    }

    private fun startCancellationTimer() {
        cancellationTimerJob?.cancel()
        cancellationTimerJob = viewModelScope.launch {
            var seconds = 180
            while (seconds > 0) {
                delay(1000)
                seconds--
                _uiState.value = _uiState.value.copy(cancelTimerSeconds = seconds)
            }
        }
    }

    private fun observeActiveTripMessages(tripId: String) {
        viewModelScope.launch {
            repository.getMessages(tripId).collect { messages ->
                _uiState.value = _uiState.value.copy(activeTripMessages = messages)
            }
        }
    }

    fun sendMessage(text: String) {
        val trip = _uiState.value.activeTrip ?: return
        val user = _uiState.value.currentUserProfile ?: return
        if (text.trim().isEmpty()) return
        viewModelScope.launch {
            repository.sendMessage(
                messageText = text.trim(),
                tripId = trip.id,
                senderId = user.id,
                senderName = user.name
            )
        }
    }

    fun submitKycForm(
        dlNumber: String,
        dlPhotoUri: String,
        rcNumber: String,
        rcPhotoUri: String,
        plateCategory: String,
        liveSelfieUri: String,
        vehicleMakeModel: String,
        licensePlateNumber: String,
        seatingCapacity: Int
    ) {
        val currentUser = _uiState.value.currentUserProfile ?: return
        val newKyc = KycDocumentEntity(
            id = UUID.randomUUID().toString(),
            userId = currentUser.id,
            dlNumber = dlNumber,
            dlPhotoUri = dlPhotoUri,
            rcNumber = rcNumber,
            rcPhotoUri = rcPhotoUri,
            plateCategory = plateCategory,
            liveSelfieUri = liveSelfieUri,
            vehicleMakeModel = vehicleMakeModel,
            licensePlateNumber = licensePlateNumber,
            seatingCapacity = seatingCapacity,
            status = KycStatus.PENDING_ADMIN_APPROVAL.name
        )
        viewModelScope.launch {
            repository.submitKyc(newKyc)
            _uiState.value = _uiState.value.copy(
                isKycScreenOpen = false,
                snackbarMessage = "KYC Documents Submitted! Pending Owner Admin approval."
            )
        }
    }

    fun createCarpoolOffer(
        vehicleMakeModel: String,
        licensePlate: String,
        seats: Int,
        pricePerSeat: Double,
        pickup: KanpurLocation,
        drop: KanpurLocation,
        departureTime: String
    ) {
        val currentUser = _uiState.value.currentUserProfile ?: return
        if (currentUser.kycStatus != KycStatus.APPROVED.name) {
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Driver KYC Verification Required before creating carpool offers."
            )
            return
        }

        val generatedOtp = (1000..9999).random().toString()
        val newOffer = TripEntity(
            id = UUID.randomUUID().toString(),
            hostDriverId = currentUser.id,
            hostName = currentUser.name,
            hostPhone = currentUser.phone,
            hostIsFemale = currentUser.isFemale,
            hostRating = currentUser.rating,
            vehicleDetails = "$vehicleMakeModel • $licensePlate",
            rideMode = RideMode.CARPOOL.name,
            vehicleType = VehicleType.CARPOOL_SEDAN.name,
            pickupName = pickup.name,
            pickupLat = pickup.latitude,
            pickupLng = pickup.longitude,
            dropName = drop.name,
            dropLat = drop.latitude,
            dropLng = drop.longitude,
            departureTime = departureTime,
            availableSeats = seats,
            totalSeats = seats,
            pricePerSeatOrKm = pricePerSeat,
            surgeMultiplier = 1.0,
            isPinkShield = currentUser.isFemale,
            status = TripStatus.SEARCHING.name,
            otp = generatedOtp,
            distanceKm = 12.0,
            totalFare = pricePerSeat
        )

        viewModelScope.launch {
            repository.createTrip(newOffer)
            _uiState.value = _uiState.value.copy(
                isCreateOfferOpen = false,
                snackbarMessage = "Carpool Offer Created! Commuters can now book seats."
            )
        }
    }

    fun approveKyc(kycId: String, userId: String) {
        viewModelScope.launch {
            repository.approveKyc(kycId, userId)
            _uiState.value = _uiState.value.copy(snackbarMessage = "KYC Approved! Driver account activated.")
        }
    }

    fun rejectKyc(kycId: String, userId: String, reason: String) {
        viewModelScope.launch {
            repository.rejectKyc(kycId, userId, reason)
            _uiState.value = _uiState.value.copy(snackbarMessage = "KYC Rejected.")
        }
    }

    fun updateFareRates(carpoolRate: Double, cabRate: Double, autoRate: Double, bikeRate: Double, surge: Double) {
        val current = _uiState.value.fareConfig
        val updated = current.copy(
            carpoolRatePerKm = carpoolRate,
            cabRatePerKm = cabRate,
            autoRatePerKm = autoRate,
            bikeRatePerKm = bikeRate,
            nightSurgeMultiplier = surge,
            updatedTimestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.updateFareConfig(updated)
            _uiState.value = _uiState.value.copy(
                fareConfig = updated,
                snackbarMessage = "Dynamic Fare Controller updated successfully."
            )
        }
    }

    fun submitRating(stars: Int, comment: String) {
        val trip = _uiState.value.activeTrip ?: return
        val currentUser = _uiState.value.currentUserProfile ?: return
        viewModelScope.launch {
            repository.submitRating(
                RatingEntity(
                    id = UUID.randomUUID().toString(),
                    tripId = trip.id,
                    ratedUserId = trip.hostDriverId,
                    reviewerName = currentUser.name,
                    stars = stars,
                    comment = comment
                )
            )
            _uiState.value = _uiState.value.copy(
                isRatingModalOpen = false,
                activeTrip = null,
                snackbarMessage = "Thank you for rating your YatraX ride!"
            )
        }
    }

    fun openKycScreen(open: Boolean) { _uiState.value = _uiState.value.copy(isKycScreenOpen = open) }
    fun openAdminPanel(open: Boolean) { _uiState.value = _uiState.value.copy(isAdminPanelOpen = open) }
    fun openChatModal(open: Boolean) { _uiState.value = _uiState.value.copy(isChatOpen = open) }
    fun openSosAlert(open: Boolean) { _uiState.value = _uiState.value.copy(isSosAlertOpen = open) }
    fun openCreateOffer(open: Boolean) { _uiState.value = _uiState.value.copy(isCreateOfferOpen = open) }
    fun dismissRatingModal() { _uiState.value = _uiState.value.copy(isRatingModalOpen = false, activeTrip = null) }
    fun clearSnackbar() { _uiState.value = _uiState.value.copy(snackbarMessage = null) }
}
