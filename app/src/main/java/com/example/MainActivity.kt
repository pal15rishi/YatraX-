package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.shaded.AdminPanelSettings
import androidx.compose.material.icons.shaded.Badge
import androidx.compose.material.icons.shaded.DirectionsCar
import androidx.compose.material.icons.shaded.VerifiedUser
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
import com.example.data.ui.viewmodel.YatraXViewModel
import com.example.ui.components.ActiveTripDrawer
import com.example.ui.components.AdminPanelScreen
import com.example.ui.components.AuthModalScreen
import com.example.ui.components.ChatAndKycScreen
import com.example.ui.components.CreateCarpoolOfferModal
import com.example.ui.components.KanpurMapView
import com.example.ui.components.KycOnboardingScreen
import com.example.ui.components.LocationSearchBar
import com.example.ui.components.MoodSwitcherBar
import com.example.ui.components.SOSEmergencyDialog
import com.example.ui.theme.YatraXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YatraXTheme {
                val viewModel: YatraXViewModel = viewModel()
                val isLoggedIn: Boolean by viewModel.isLoggedIn.collectAsState()
                val userRole: String by viewModel.userRole.collectAsState()
                
                var showAuthModal by remember { mutableStateOf(false) }
                val snackbarHostState = remember { SnackbarHostState() }

                // Dummy helper references to prevent unresolved references during compilation
                val verifyOtp: (String) -> Unit = { _ -> }
                val openAuthModal: () -> Unit = { showAuthModal = true }
                val resetDemoData: () -> Unit = {}
                val listBookingsOpen: Boolean = false

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                MoodSwitcherBar(
                                    currentMode = RideMode.NORMAL,
                                    onModeSelected = { mode: RideMode -> viewModel.setRideMode(mode) }
                                )
                                KanpurMapView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
