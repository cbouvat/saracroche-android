package com.cbouvat.android.saracroche.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PhoneDisabled
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.cbouvat.android.saracroche.util.PreferencesManager
import kotlinx.coroutines.launch

sealed class SettingsItem {
    data class Action(
        val title: String,
        val subtitle: String?,
        val icon: ImageVector,
        val onClick: () -> Unit
    ) : SettingsItem()

    data class Switch(
        val title: String,
        val subtitle: String?,
        val icon: ImageVector,
        val checked: Boolean,
        val onCheckedChange: (Boolean) -> Unit
    ) : SettingsItem()
}

@Composable
fun SettingsSection(
    title: String,
    items: List<SettingsItem>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.padding(16.dp)
        )

        items.forEach { item ->
            when (item) {
                is SettingsItem.Action -> {
                    SettingsActionItem(
                        title = item.title,
                        subtitle = item.subtitle,
                        icon = item.icon,
                        onClick = item.onClick
                    )
                }

                is SettingsItem.Switch -> {
                    SettingsSwitchItem(
                        title = item.title,
                        subtitle = item.subtitle,
                        icon = item.icon,
                        checked = item.checked,
                        onCheckedChange = item.onCheckedChange
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsActionItem(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun NotificationPermissionRationaleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission requise") },
        text = { Text("L'application a besoin de la permission pour envoyer des notifications.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Ouvrir les paramètres")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Annuler")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // DataStore state for anonymous call blocking
    val coroutineScope = rememberCoroutineScope()
    val blockAnonymousCallsState = PreferencesManager.getBlockAnonymousCallsFlow(context)
        .collectAsState(initial = false)
    val blockedCallNotification = PreferencesManager.getBlockedCallNotificationFlow(context)
        .collectAsState(initial = false)

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            coroutineScope.launch {
                PreferencesManager.setBlockedCallNotification(context, isGranted)
            }
        }
    )

    var showNotificationPermissionRationaleDialog by remember { mutableStateOf(false) }

    if (showNotificationPermissionRationaleDialog) {
        NotificationPermissionRationaleDialog(
            onDismiss = {
                coroutineScope.launch {
                    PreferencesManager.setBlockedCallNotification(context, false)
                }
                showNotificationPermissionRationaleDialog = false
            },
            onConfirm = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
                showNotificationPermissionRationaleDialog = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Réglages",
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.statusBars
            )
        },
        contentWindowInsets = WindowInsets.displayCutout
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Configuration Section
            SettingsSection(
                title = "Configuration",
                items = listOf(
                    SettingsItem.Action(
                        title = "Application par défaut pour le blocage d'appels",
                        subtitle = "Configurer l'application par défaut pour le blocage de spam dans les réglages du téléphone",
                        icon = Icons.Rounded.Settings,
                        onClick = { openCallBlockingSettings(context) }
                    ),
                    SettingsItem.Switch(
                        title = "Bloquer les appels masqués",
                        subtitle = "Bloquer automatiquement tous les appels provenant de numéros masqués ou privés",
                        icon = Icons.Rounded.PhoneDisabled,
                        checked = blockAnonymousCallsState.value,
                        onCheckedChange = { newValue ->
                            coroutineScope.launch {
                                PreferencesManager.setBlockAnonymousCalls(context, newValue)
                            }
                        }
                    ),
                    SettingsItem.Switch(
                        title = "Notification d'appel bloqué",
                        subtitle = "Recevoir une notification quand un appel est bloqué.",
                        icon = Icons.Rounded.Notifications,
                        checked = blockedCallNotification.value,
                        onCheckedChange = { isEnabled ->
                            if (isEnabled) {
                                // on Android 13+ it is required to ask permission
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val activity = context as? Activity ?: return@Switch

                                    when {
                                        // Case 1 : permission already granted -> let the user re-enable it
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED -> {
                                            coroutineScope.launch {
                                                PreferencesManager.setBlockedCallNotification(
                                                    context,
                                                    true
                                                )
                                            }
                                        }

                                        // Case 2 : show rational
                                        activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                                            showNotificationPermissionRationaleDialog = true
                                        }

                                        // Case 3 : first time asking -> launch permission request
                                        else -> {
                                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    }
                                } else {
                                    // On older devices, no check required
                                    coroutineScope.launch {
                                        PreferencesManager.setBlockedCallNotification(context, true)
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    PreferencesManager.setBlockedCallNotification(context, false)
                                }
                            }
                        }
                    )
                )
            )

            // Links Section
            SettingsSection(
                title = "Liens",
                items = listOf(
                    SettingsItem.Action(
                        title = "Aide et FAQ",
                        subtitle = "Questions fréquentes et guide d'utilisation",
                        icon = Icons.Rounded.QuestionMark,
                        onClick = { openHelpDialog(context) }
                    ),
                    SettingsItem.Action(
                        title = "Confidentialité",
                        subtitle = "En savoir plus sur la politique de confidentialité",
                        icon = Icons.Rounded.Shield,
                        onClick = { openPrivacyPolicy(context) }
                    ),
                    SettingsItem.Action(
                        title = "Site officiel",
                        subtitle = "Consulter le site officiel",
                        icon = Icons.Rounded.Link,
                        onClick = { openOfficialWebsite(context) }
                    ),
                    SettingsItem.Action(
                        title = "Noter l'application",
                        subtitle = "Évaluer l'app sur le Google Play Store",
                        icon = Icons.Rounded.Star,
                        onClick = { openPlayStore(context) }
                    ),
                    SettingsItem.Action(
                        title = "Code source",
                        subtitle = "Voir le code sur GitHub",
                        icon = Icons.Rounded.Code,
                        onClick = { openGitHub(context) }
                    )
                )
            )

            // Contact Section
            SettingsSection(
                title = "Contact",
                items = listOf(
                    SettingsItem.Action(
                        title = "Contacter par e-mail",
                        subtitle = "Signaler un bug ou suggérer une fonctionnalité par e-mail",
                        icon = Icons.Rounded.Mail,
                        onClick = { openBugReport(context) }
                    ),
                    SettingsItem.Action(
                        title = "Mastodon",
                        subtitle = "Suivre @cbouvat sur le réseau social Mastodon",
                        icon = Icons.Rounded.ChatBubble,
                        onClick = { openMastodon(context) }
                    )
                )
            )

            // Footer
            Text(
                text = "Version ${
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        0
                    ).versionName
                }\n\nBisou 😘",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Space to ensure content is not cut off
            Spacer(modifier = Modifier.height(128.dp))
        }
    }
}

// Configuration system functions
private fun openCallBlockingSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        context.startActivity(intent)
    } catch (e: Exception) {
        // If the specific setting is not available, open general settings
        try {
            val intent = Intent(Settings.ACTION_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle error silently
        }
    }
}

// Links functions
private fun openHelpDialog(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://saracroche.org/fr/help/"))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}

private fun openPrivacyPolicy(context: Context) {
    try {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://saracroche.org/fr/privacy/"))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}

private fun openOfficialWebsite(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://saracroche.org/"))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}

private fun openGitHub(context: Context) {
    try {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cbouvat/saracroche-android"))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}

// Store functions
private fun openPlayStore(context: Context) {
    try {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle error silently
        }
    }
}

// Contact functions
private fun openBugReport(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("mail@cbouvat.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Contact - Saracroche Android")
            // Add info about the device, version in French
            val deviceInfo = """
                Appareil : ${Build.MODEL} (${Build.MANUFACTURER})
                Version Android : ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
                Version de l'application : ${
                context.packageManager.getPackageInfo(
                    context.packageName,
                    0
                ).versionName
            }
            """.trimIndent()
            putExtra(
                Intent.EXTRA_TEXT,
                "Bonjour,\n\n(Votre message ici)\n\n$deviceInfo"
            )
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}

private fun openMastodon(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mastodon.social/@cbouvat"))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}
