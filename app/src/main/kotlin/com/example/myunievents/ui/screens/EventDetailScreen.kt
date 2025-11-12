package com.example.myunievents.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.myunievents.ServiceLocator
import com.example.myunievents.ui.notifications.NotificationScheduler
import com.example.myunievents.viewmodel.EventDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(navController: NavController, eventId: String?) {
    val context = LocalContext.current
    val eventIdLong = eventId?.toLongOrNull()

    val viewModel = remember {
        EventDetailViewModel(ServiceLocator.provideEventRepo(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission Granted
            }
        }
    )

    LaunchedEffect(eventIdLong) {
        if (eventIdLong != null) {
            viewModel.getEvent(eventIdLong)
        }
    }

    val event by viewModel.event.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Event Details") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (event == null) {
            if (eventIdLong != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading event details...")
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Event not found", style = MaterialTheme.typography.headlineSmall)
                }
            }
        } else {
            event?.let { evt ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = evt.title,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        DetailItem(icon = Icons.Outlined.Description, label = "Description", value = evt.description)
                        DetailItem(icon = Icons.Outlined.CalendarToday, label = "Date", value = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(Date(evt.dateTimeMillis)))
                        DetailItem(icon = Icons.Outlined.Schedule, label = "Time", value = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(evt.dateTimeMillis)))
                        DetailItem(icon = Icons.Outlined.LocationOn, label = "Location", value = evt.location)

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = { addToCalendar(context, evt.title, evt.description, evt.location, evt.dateTimeMillis) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ADD TO CALENDAR")
                            }
                            Button(
                                onClick = { viewOnMap(context, evt.location) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("VIEW ON MAP")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        NotificationScheduler.scheduleNotification(context, evt.dateTimeMillis - 3600000, "Event Reminder", "Your event '${evt.title}' is starting in one hour.")
                                    }
                                } else {
                                    NotificationScheduler.scheduleNotification(context, evt.dateTimeMillis - 3600000, "Event Reminder", "Your event '${evt.title}' is starting in one hour.")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SET REMINDER (1 HOUR BEFORE)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    if (value.isNotBlank()) {
        Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.Top) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = label, style = MaterialTheme.typography.titleMedium)
                Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun addToCalendar(context: Context, title: String, description: String, location: String, startTime: Long) {
    val intent = Intent(Intent.ACTION_INSERT)
        .setData(CalendarContract.Events.CONTENT_URI)
        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
        .putExtra(CalendarContract.Events.TITLE, title)
        .putExtra(CalendarContract.Events.DESCRIPTION, description)
        .putExtra(CalendarContract.Events.EVENT_LOCATION, location)

    context.startActivity(intent)
}

private fun viewOnMap(context: Context, location: String) {
    val gmmIntentUri = Uri.parse("geo:0,0?q=$location")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}
