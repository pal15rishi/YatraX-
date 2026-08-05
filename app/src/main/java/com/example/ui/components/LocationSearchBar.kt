package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.KANPUR_LANDMARKS
import com.example.data.model.KanpurLocation

@Composable
fun LocationSearchBar(
    pickupLocation: KanpurLocation,
    dropLocation: KanpurLocation,
    pickupQuery: String,
    dropQuery: String,
    filteredResults: List<KanpurLocation>,
    onPickupQueryChanged: (String) -> Unit,
    onDropQueryChanged: (String) -> Unit,
    onPickupSelected: (KanpurLocation) -> Unit,
    onDropSelected: (KanpurLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSearchField by remember { mutableStateOf<String?>(null) } // "PICKUP" or "DROP"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("location_search_bar"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Pickup Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = "Pickup Pin",
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = if (activeSearchField == "PICKUP") pickupQuery else pickupLocation.name,
                    onValueChange = {
                        activeSearchField = "PICKUP"
                        onPickupQueryChanged(it)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pickup_location_input"),
                    placeholder = { Text("Search Pickup Point in Kanpur...") },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF22C55E),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        cursorColor = Color.White,
                        focusedPlaceholderColor = Color(0xFFCAC4D0),
                        unfocusedPlaceholderColor = Color(0xFFCAC4D0)
                    ),
                    trailingIcon = {
                        if (activeSearchField == "PICKUP" && pickupQuery.isNotEmpty()) {
                            IconButton(onClick = { onPickupQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear Pickup")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Drop Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Drop Pin",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = if (activeSearchField == "DROP") dropQuery else dropLocation.name,
                    onValueChange = {
                        activeSearchField = "DROP"
                        onDropQueryChanged(it)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("drop_location_input"),
                    placeholder = { Text("Search Destination in Kanpur...") },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFEF4444),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        cursorColor = Color.White,
                        focusedPlaceholderColor = Color(0xFFCAC4D0),
                        unfocusedPlaceholderColor = Color(0xFFCAC4D0)
                    ),
                    trailingIcon = {
                        if (activeSearchField == "DROP" && dropQuery.isNotEmpty()) {
                            IconButton(onClick = { onDropQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear Drop")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Quick Kanpur Destination Chips
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(KANPUR_LANDMARKS.take(6)) { landmark ->
                    Surface(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onDropSelected(landmark)
                                activeSearchField = null
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = null
                    ) {
                        Text(
                            text = landmark.name,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Debounced Search Results Dropdown List
            if (activeSearchField != null && filteredResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                LazyColumn(modifier = Modifier.height(140.dp)) {
                    items(filteredResults) { location ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (activeSearchField == "PICKUP") {
                                        onPickupSelected(location)
                                    } else {
                                        onDropSelected(location)
                                    }
                                    activeSearchField = null
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = location.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = location.address,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
