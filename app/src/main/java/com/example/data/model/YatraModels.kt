package com.example.data.model

import java.util.UUID

enum class RideMode {
    CARPOOL,
    CAB_AUTO_BIKE
}

enum class VehicleCategory {
    WHITE_PLATE_PRIVATE, // Carpool / Fuel share
    YELLOW_PLATE_COMMERCIAL // Cab / Auto / Bike
}

enum class VehicleType(val displayName: String, val capacity: Int, val iconName: String) {
    CARPOOL_HATCHBACK("Carpool Hatchback", 3, "DirectionsCar"),
    CARPOOL_SEDAN("Carpool Sedan", 4, "DirectionsCar"),
    CAB_SEDAN("Prime Cab (AC)", 4, "LocalTaxi"),
    CAB_HATCHBACK("Mini Cab", 4, "LocalTaxi"),
    AUTO_RICKSHAW("Kanpur Auto", 3, "ElectricRickshaw"),
    EXPRESS_BIKE("Express Bike", 1, "TwoWheeler")
}

enum class KycStatus {
    NONE,
    PENDING_ADMIN_APPROVAL,
    APPROVED,
    REJECTED
}

enum class TripStatus {
    SEARCHING,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class KanpurLocation(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String = "Popular Destination"
)

// Pre-configured Kanpur Landmarks
val KANPUR_LANDMARKS = listOf(
    KanpurLocation("1", "Kanpur Central Station", "Central Railway Station Rd, Kanpur", 26.4537, 80.3512, "Transit Hub"),
    KanpurLocation("2", "Z Square Mall", "Bada Ch चौराहे, Mall Rd, Civil Lines, Kanpur", 26.4678, 80.3508, "Shopping Center"),
    KanpurLocation("3", "IIT Kanpur", "Kalyanpur, Kanpur", 26.5123, 80.2329, "Educational Institute"),
    KanpurLocation("4", "Swaroop Nagar Market", "Swaroop Nagar, Kanpur", 26.4752, 80.3120, "Commercial Hub"),
    KanpurLocation("5", "GSVM Medical College", "Swaroop Nagar, Kanpur", 26.4791, 80.3065, "Hospital / College"),
    KanpurLocation("6", "Rawatpur Station", "Rawatpur, Kanpur", 26.4855, 80.3012, "Transit Hub"),
    KanpurLocation("7", "Chakeri Airport (KNU)", "Chakeri, Kanpur", 26.4021, 80.4123, "Airport"),
    KanpurLocation("8", "Jajmau Industrial Area", "Jajmau, Kanpur", 26.4312, 80.3956, "Industrial Zone"),
    KanpurLocation("9", "Allen Forest Zoo", "Nawabganj, Kanpur", 26.5021, 80.3210, "Tourist Attraction"),
    KanpurLocation("10", "Govind Nagar Market", "Govind Nagar, Kanpur", 26.4410, 80.2980, "Market Place")
)
