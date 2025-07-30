package com.cbouvat.android.saracroche.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddComment
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
                color = MaterialTheme.colorScheme.primary
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
fun SettingsSection(
    title: String,
    items: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(16.dp)
        )

        items()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
        contentWindowInsets = WindowInsets.navigationBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // System Settings Section
            SettingsSection(
                title = "Configuration système",
                items = listOf(
                    SettingsItem.Action(
                        title = "Application par défaut pour le blocage d'appels",
                        subtitle = "Configurer l'application par défaut pour le blocage de spam dans les réglages du téléphone",
                        icon = Icons.Rounded.Settings,
                        onClick = { openCallBlockingSettings(context) }
                    )
                )
            )

            // Links Section
            SettingsSection(
                title = "Liens utiles",
                items = listOf(
                    SettingsItem.Action(
                        title = "Code source",
                        subtitle = "Voir le code sur GitHub",
                        icon = Icons.Rounded.Code,
                        onClick = { openGitHub(context) }
                    ),
                    SettingsItem.Action(
                        title = "Site officiel",
                        subtitle = "Consulter le site officiel",
                        icon = Icons.Rounded.Link,
                        onClick = { openOfficialWebsite(context) }
                    ),
                    SettingsItem.Action(
                        title = "Mastodon : @cbouvat",
                        subtitle = "Me suivre sur Mastodon et discuter",
                        icon = Icons.Rounded.ChatBubble,
                        onClick = { openMastodon(context) }
                    )
                )
            )

            // Support Section
            SettingsSection(
                title = "Application",
                items = listOf(
                    SettingsItem.Action(
                        title = "Noter l'application",
                        subtitle = "Évaluer l'app sur le Play Store",
                        icon = Icons.Rounded.Star,
                        onClick = { openPlayStore(context) }
                    ),
                    SettingsItem.Action(
                        title = "Signaler un bug ou suggérer une amélioration",
                        subtitle = "Nous faire part d'un bug ou d'une suggestion",
                        icon = Icons.Rounded.AddComment,
                        onClick = { openBugReport(context) }
                    )
                )
            )

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Version de l'application : ${
                            context.packageManager.getPackageInfo(
                                context.packageName,
                                0
                            ).versionName
                        }",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

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

private fun openGitHub(context: Context) {
    try {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cbouvat/saracroche-android"))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}

private fun openOfficialWebsite(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cbouvat.com/saracroche/"))
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

private fun openBugReport(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("saracroche@cbouvat.com"))
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
                "Bonjour,\n\nJe souhaite signaler un problème ou faire une suggestion concernant l'application :\n\n$deviceInfo\n\nBisou 😘"
            )
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}

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
