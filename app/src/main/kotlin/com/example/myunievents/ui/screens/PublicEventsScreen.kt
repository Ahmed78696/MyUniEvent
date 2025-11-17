package com.example.myunievents.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myunievents.ServiceLocator
import com.example.myunievents.data.model.PublicEvent
import com.example.myunievents.viewmodel.DiscoverViewModel
import com.example.myunievents.viewmodel.EventsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicEventsScreen(navController: NavController) {
    val discoverViewModel = remember { DiscoverViewModel(ServiceLocator.provideFirebaseRepo()) }
    val eventsViewModel = remember { EventsViewModel(ServiceLocator.provideEventRepo(navController.context)) }
    val publicEvents by discoverViewModel.publicEvents.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf( "Academic", "Social", "Sports", "Workshop","experience", "Other")

    val filteredEvents = publicEvents.filter { event ->
        val matchesCategory = selectedCategory == "All" || event.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || event.title.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Public Events") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        // FIX: Replaced outer Column with LazyColumn to make the whole screen scrollable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header Item 1: Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Events") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                )
            }

            // Header Item 2: Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) }
                        )
                    }
                }
            }

            // Content: Either the list or the empty state
            if (filteredEvents.isEmpty()) {
                item {
                    EmptyPublicEventsState(modifier = Modifier.fillParentMaxSize())
                }
            } else {
                items(filteredEvents, key = { it.title + it.createdAt }) { event ->
                    PublicEventItem(event = event, modifier = Modifier.padding(horizontal = 16.dp)) {
                        // The logic for saving the event remains unchanged
                        eventsViewModel.addEvent(
                            title = event.title,
                            desc = event.description,
                            location = event.location,
                            millis = event.dateTimeMillis,
                            category = event.category,
                            createdBy = event.createdBy,
                            publishToAnnouncements = false,
                            isPublic = false // When saving, it becomes a private event
                        )
                    }
                }
            }
        }
    }
}

// FIX: Added a modifier parameter to be used by the LazyColumn item
@Composable
fun PublicEventItem(event: PublicEvent, modifier: Modifier = Modifier, onSave: () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(), // Use the passed-in modifier
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(event.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(event.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val fmt = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                Text(fmt.format(Date(event.dateTimeMillis)), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.BookmarkAdd, contentDescription = "Save to My Events")
                Spacer(modifier = Modifier.size(8.dp))
                Text("SAVE TO MY EVENTS")
            }
        }
    }
}

// Unchanged
@Composable
fun EmptyPublicEventsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = "No events",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Public Events Found",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try adjusting your search or filter. Or, be the first to share an event with the university community!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}
