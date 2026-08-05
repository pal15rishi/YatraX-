package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

data class DriverProfileEntity(
    val id: String,
    val name: String,
    val phone: String,
    val vehicleModel: String,
    val vehicleNumber: String,
    val vehicleType: com.example.data.model.VehicleType,
    val isCommercialPlate: Boolean,
    val isFemale: Boolean,
    val dlPhotoUrl: String,
    val rcPhotoUrl: String,
    val selfiePhotoUrl: String,
    val kycStatus: com.example.data.model.KycStatus,
    val seatsCapacity: Int,
    val currentRating: Double
)

data class ChatMessageEntity(
    val id: String,
    val tripId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: String
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val isFemale: Boolean,
    val kycStatus: String, // KycStatus enum name
    val rating: Float,
    val totalRides: Int,
    val role: String // "RIDER", "DRIVER_HOST", "ADMIN"
)

@Entity(tableName = "kyc_documents")
data class KycDocumentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val dlNumber: String,
    val dlPhotoUri: String,
    val rcNumber: String,
    val rcPhotoUri: String,
    val plateCategory: String, // "WHITE_PLATE_PRIVATE", "YELLOW_PLATE_COMMERCIAL"
    val liveSelfieUri: String,
    val vehicleMakeModel: String,
    val licensePlateNumber: String,
    val seatingCapacity: Int,
    val status: String, // KycStatus enum name
    val rejectionReason: String = "",
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val hostDriverId: String,
    val hostName: String,
    val hostPhone: String,
    val hostIsFemale: Boolean,
    val hostRating: Float,
    val vehicleDetails: String, // Make/Model - License Plate
    val rideMode: String, // RideMode enum name ("CARPOOL", "CAB_AUTO_BIKE")
    val vehicleType: String, // VehicleType enum name
    val pickupName: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropName: String,
    val dropLat: Double,
    val dropLng: Double,
    val departureTime: String,
    val availableSeats: Int,
    val totalSeats: Int,
    val pricePerSeatOrKm: Double,
    val surgeMultiplier: Double,
    val isPinkShield: Boolean,
    val status: String, // TripStatus enum name
    val otp: String,
    val riderId: String? = null,
    val riderName: String? = null,
    val distanceKm: Double,
    val totalFare: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: String,
    val senderId: String,
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ratings")
data class RatingEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val ratedUserId: String,
    val reviewerName: String,
    val stars: Int,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "fare_configs")
data class FareConfigEntity(
    @PrimaryKey val id: Int = 1,
    val carpoolRatePerKm: Double = 3.5,
    val cabRatePerKm: Double = 12.0,
    val autoRatePerKm: Double = 8.0,
    val bikeRatePerKm: Double = 5.0,
    val nightSurgeMultiplier: Double = 1.2,
    val updatedTimestamp: Long = System.currentTimeMillis()
)
