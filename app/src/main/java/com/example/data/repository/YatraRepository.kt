package com.example.data.repository

import com.example.data.local.FareConfigEntity
import com.example.data.local.KycDocumentEntity
import com.example.data.local.MessageEntity
import com.example.data.local.RatingEntity
import com.example.data.local.TripEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.YatraDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class YatraRepository(private val dao: YatraDao) {

    val fareConfig: Flow<FareConfigEntity?> = dao.getFareConfig()
    val allTrips: Flow<List<TripEntity>> = dao.getAllTrips()
    val pendingKycs: Flow<List<KycDocumentEntity>> = dao.getPendingKycDocuments()
    val allKycs: Flow<List<KycDocumentEntity>> = dao.getAllKycDocuments()
    val allProfiles: Flow<List<UserProfileEntity>> = dao.getAllUserProfiles()

    fun getTrip(tripId: String): Flow<TripEntity?> = dao.getTripById(tripId)
    fun getMessages(tripId: String): Flow<List<MessageEntity>> = dao.getMessagesForTrip(tripId)
    fun getUserProfile(userId: String): Flow<UserProfileEntity?> = dao.getUserProfile(userId)
    fun getKycForUser(userId: String): Flow<KycDocumentEntity?> = dao.getKycDocumentByUserId(userId)

    suspend fun insertUserProfile(profile: UserProfileEntity) = dao.insertUserProfile(profile)
    suspend fun submitKyc(kyc: KycDocumentEntity) {
        dao.insertKycDocument(kyc)
        dao.updateUserKycStatus(kyc.userId, kyc.status)
    }

    suspend fun approveKyc(kycId: String, userId: String) {
        dao.updateKycStatus(kycId, "APPROVED")
        dao.updateUserKycStatus(userId, "APPROVED")
    }

    suspend fun rejectKyc(kycId: String, userId: String, reason: String) {
        dao.updateKycStatus(kycId, "REJECTED", reason)
        dao.updateUserKycStatus(userId, "REJECTED")
    }

    suspend fun createTrip(trip: TripEntity) = dao.insertTrip(trip)

    suspend fun bookTrip(tripId: String, riderId: String, riderName: String) {
        dao.acceptTrip(tripId, riderId, riderName, status = "ACCEPTED")
    }

    suspend fun updateTripStatus(tripId: String, status: String) {
        dao.updateTripStatus(tripId, status)
    }

    suspend fun sendMessage(messageText: String, tripId: String, senderId: String, senderName: String) {
        dao.insertMessage(
            MessageEntity(
                tripId = tripId,
                senderId = senderId,
                senderName = senderName,
                messageText = messageText,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun submitRating(rating: RatingEntity) {
        dao.insertRating(rating)
    }

    suspend fun updateFareConfig(config: FareConfigEntity) {
        dao.insertFareConfig(config)
    }

    // Seed database if empty
    suspend fun seedInitialData() {
        val existingConfig = dao.getFareConfigDirect()
        if (existingConfig == null) {
            dao.insertFareConfig(FareConfigEntity())
        }

        val existingUser = dao.getUserProfileDirect("user_current")
        if (existingUser == null) {
            // Main user (Rider / Driver)
            dao.insertUserProfile(
                UserProfileEntity(
                    id = "user_current",
                    name = "Ananya Sharma",
                    phone = "+91 98765 43210",
                    email = "ananya.sharma@yatrax.in",
                    isFemale = true,
                    kycStatus = "APPROVED",
                    rating = 4.9f,
                    totalRides = 24,
                    role = "RIDER"
                )
            )

            // Seed Sample Verified Drivers/Hosts in Kanpur
            val drivers = listOf(
                UserProfileEntity("driver_1", "Rajesh Kumar", "+91 98234 11223", "rajesh.k@gmail.com", isFemale = false, kycStatus = "APPROVED", rating = 4.8f, totalRides = 142, role = "DRIVER_HOST"),
                UserProfileEntity("driver_2", "Pooja Verma", "+91 98711 55443", "pooja.v@gmail.com", isFemale = true, kycStatus = "APPROVED", rating = 4.95f, totalRides = 88, role = "DRIVER_HOST"),
                UserProfileEntity("driver_3", "Amit Yadav", "+91 94150 99887", "amit.yadav@gmail.com", isFemale = false, kycStatus = "APPROVED", rating = 4.7f, totalRides = 210, role = "DRIVER_HOST"),
                UserProfileEntity("driver_4", "Neha Gupta", "+91 93361 77221", "neha.gupta@gmail.com", isFemale = true, kycStatus = "PENDING_ADMIN_APPROVAL", rating = 5.0f, totalRides = 12, role = "DRIVER_HOST")
            )
            drivers.forEach { dao.insertUserProfile(it) }

            // Seed Driver KYCs
            dao.insertKycDocument(
                KycDocumentEntity(
                    id = "kyc_1",
                    userId = "driver_1",
                    dlNumber = "UP78 20210048291",
                    dlPhotoUri = "dl_photo_sample_1",
                    rcNumber = "UP78 AB 1234",
                    rcPhotoUri = "rc_photo_sample_1",
                    plateCategory = "WHITE_PLATE_PRIVATE",
                    liveSelfieUri = "selfie_sample_1",
                    vehicleMakeModel = "Maruti Suzuki Swift",
                    licensePlateNumber = "UP 78 AB 1234",
                    seatingCapacity = 3,
                    status = "APPROVED"
                )
            )
            dao.insertKycDocument(
                KycDocumentEntity(
                    id = "kyc_2",
                    userId = "driver_2",
                    dlNumber = "UP78 20220088192",
                    dlPhotoUri = "dl_photo_sample_2",
                    rcNumber = "UP78 BX 9988",
                    rcPhotoUri = "rc_photo_sample_2",
                    plateCategory = "WHITE_PLATE_PRIVATE",
                    liveSelfieUri = "selfie_sample_2",
                    vehicleMakeModel = "Hyundai i20",
                    licensePlateNumber = "UP 78 BX 9988",
                    seatingCapacity = 3,
                    status = "APPROVED"
                )
            )
            dao.insertKycDocument(
                KycDocumentEntity(
                    id = "kyc_3",
                    userId = "driver_3",
                    dlNumber = "UP78 20190011223",
                    dlPhotoUri = "dl_photo_sample_3",
                    rcNumber = "UP78 T 4321",
                    rcPhotoUri = "rc_photo_sample_3",
                    plateCategory = "YELLOW_PLATE_COMMERCIAL",
                    liveSelfieUri = "selfie_sample_3",
                    vehicleMakeModel = "Maruti Dzire Tour (Cab)",
                    licensePlateNumber = "UP 78 T 4321",
                    seatingCapacity = 4,
                    status = "APPROVED"
                )
            )
            dao.insertKycDocument(
                KycDocumentEntity(
                    id = "kyc_4",
                    userId = "driver_4",
                    dlNumber = "UP78 20230099112",
                    dlPhotoUri = "dl_photo_sample_4",
                    rcNumber = "UP78 CY 5566",
                    rcPhotoUri = "rc_photo_sample_4",
                    plateCategory = "WHITE_PLATE_PRIVATE",
                    liveSelfieUri = "selfie_sample_4",
                    vehicleMakeModel = "Tata Nexon EV",
                    licensePlateNumber = "UP 78 CY 5566",
                    seatingCapacity = 3,
                    status = "PENDING_ADMIN_APPROVAL"
                )
            )

            // Seed Active Trips in Kanpur
            val trips = listOf(
                TripEntity(
                    id = "trip_1",
                    hostDriverId = "driver_1",
                    hostName = "Rajesh Kumar",
                    hostPhone = "+91 98234 11223",
                    hostIsFemale = false,
                    hostRating = 4.8f,
                    vehicleDetails = "Swift (White) • UP 78 AB 1234",
                    rideMode = "CARPOOL",
                    vehicleType = "CARPOOL_HATCHBACK",
                    pickupName = "IIT Kanpur Main Gate",
                    pickupLat = 26.5123,
                    pickupLng = 80.2329,
                    dropName = "Z Square Mall",
                    dropLat = 26.4678,
                    dropLng = 80.3508,
                    departureTime = "18:30 (Today)",
                    availableSeats = 2,
                    totalSeats = 3,
                    pricePerSeatOrKm = 45.0,
                    surgeMultiplier = 1.0,
                    isPinkShield = false,
                    status = "SEARCHING",
                    otp = "4821",
                    distanceKm = 14.2,
                    totalFare = 45.0
                ),
                TripEntity(
                    id = "trip_2",
                    hostDriverId = "driver_2",
                    hostName = "Pooja Verma",
                    hostPhone = "+91 98711 55443",
                    hostIsFemale = true,
                    hostRating = 4.95f,
                    vehicleDetails = "Hyundai i20 (Red) • UP 78 BX 9988",
                    rideMode = "CARPOOL",
                    vehicleType = "CARPOOL_SEDAN",
                    pickupName = "Swaroop Nagar Market",
                    pickupLat = 26.4752,
                    pickupLng = 80.3120,
                    dropName = "Chakeri Airport",
                    dropLat = 26.4021,
                    dropLng = 80.4123,
                    departureTime = "19:00 (Today)",
                    availableSeats = 3,
                    totalSeats = 3,
                    pricePerSeatOrKm = 60.0,
                    surgeMultiplier = 1.0,
                    isPinkShield = true,
                    status = "SEARCHING",
                    otp = "1952",
                    distanceKm = 16.8,
                    totalFare = 60.0
                ),
                TripEntity(
                    id = "trip_3",
                    hostDriverId = "driver_3",
                    hostName = "Amit Yadav",
                    hostPhone = "+91 94150 99887",
                    hostIsFemale = false,
                    hostRating = 4.7f,
                    vehicleDetails = "Dzire Commercial Cab • UP 78 T 4321",
                    rideMode = "CAB_AUTO_BIKE",
                    vehicleType = "CAB_SEDAN",
                    pickupName = "Kanpur Central Station",
                    pickupLat = 26.4537,
                    pickupLng = 80.3512,
                    dropName = "Allen Forest Zoo",
                    dropLat = 26.5021,
                    dropLng = 80.3210,
                    departureTime = "Instant",
                    availableSeats = 4,
                    totalSeats = 4,
                    pricePerSeatOrKm = 12.0,
                    surgeMultiplier = 1.0,
                    isPinkShield = false,
                    status = "SEARCHING",
                    otp = "7734",
                    distanceKm = 9.5,
                    totalFare = 114.0
                )
            )
            trips.forEach { dao.insertTrip(it) }
        }
    }
}
