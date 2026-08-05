package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface YatraDao {
    // User Profiles
    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    fun getUserProfile(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    suspend fun getUserProfileDirect(userId: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles")
    fun getAllUserProfiles(): Flow<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    // KYC Documents
    @Query("SELECT * FROM kyc_documents WHERE userId = :userId ORDER BY submittedAt DESC LIMIT 1")
    fun getKycDocumentByUserId(userId: String): Flow<KycDocumentEntity?>

    @Query("SELECT * FROM kyc_documents ORDER BY submittedAt DESC")
    fun getAllKycDocuments(): Flow<List<KycDocumentEntity>>

    @Query("SELECT * FROM kyc_documents WHERE status = 'PENDING_ADMIN_APPROVAL' ORDER BY submittedAt DESC")
    fun getPendingKycDocuments(): Flow<List<KycDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKycDocument(kyc: KycDocumentEntity)

    @Query("UPDATE kyc_documents SET status = :status, rejectionReason = :reason WHERE id = :kycId")
    suspend fun updateKycStatus(kycId: String, status: String, reason: String = "")

    @Query("UPDATE user_profiles SET kycStatus = :status WHERE id = :userId")
    suspend fun updateUserKycStatus(userId: String, status: String)

    // Trips
    @Query("SELECT * FROM trips ORDER BY createdAt DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE rideMode = :mode ORDER BY createdAt DESC")
    fun getTripsByMode(mode: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripById(tripId: String): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripByIdDirect(tripId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Query("UPDATE trips SET status = :status WHERE id = :tripId")
    suspend fun updateTripStatus(tripId: String, status: String)

    @Query("UPDATE trips SET status = :status, riderId = :riderId, riderName = :riderName WHERE id = :tripId")
    suspend fun acceptTrip(tripId: String, riderId: String, riderName: String, status: String = "ACCEPTED")

    @Query("UPDATE trips SET availableSeats = :availableSeats, status = :status, riderId = :riderId, riderName = :riderName WHERE id = :tripId")
    suspend fun acceptTripWithSeats(tripId: String, riderId: String, riderName: String, availableSeats: Int, status: String)

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()

    @Query("DELETE FROM kyc_documents")
    suspend fun deleteAllKycDocuments()

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()

    // Messages / Chat
    @Query("SELECT * FROM messages WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getMessagesForTrip(tripId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    // Ratings
    @Query("SELECT * FROM ratings WHERE ratedUserId = :userId ORDER BY timestamp DESC")
    fun getRatingsForUser(userId: String): Flow<List<RatingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: RatingEntity)

    // Fare Configs
    @Query("SELECT * FROM fare_configs WHERE id = 1")
    fun getFareConfig(): Flow<FareConfigEntity?>

    @Query("SELECT * FROM fare_configs WHERE id = 1")
    suspend fun getFareConfigDirect(): FareConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFareConfig(config: FareConfigEntity)
}
