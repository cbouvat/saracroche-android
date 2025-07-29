package com.cbouvat.android.saracroche.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cbouvat.android.saracroche.ui.components.SettingsItem
import com.cbouvat.android.saracroche.ui.components.SettingsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Réglages") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Permission Section
            SettingsSection(
                title = "Permission de l'application",
                items = listOf(
                    SettingsItem.Action(
                        title = "Permissions",
                        subtitle = "Gérer les permissions de l'application",
                        icon = Icons.Default.Security,
                        onClick = { openAppSettings(context) }
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
                        icon = Icons.Filled.Code,
                        onClick = { openGitHub(context) }
                    ),
                    SettingsItem.Action(
                        title = "Site officiel",
                        subtitle = "Consulter le site officiel",
                        icon = Icons.Filled.Link,
                        onClick = { openOfficialWebsite(context) }
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
                        icon = Icons.Default.Star,
                        onClick = { openPlayStore(context) }
                    ),
                    SettingsItem.Action(
                        title = "Signaler un problème",
                        subtitle = "Nous faire part d'un bug ou problème",
                        icon = Icons.Default.BugReport,
                        onClick = { openBugReport(context) }
                    )
                )
            )

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

private fun openAppSettings(context: Context) {
    try {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:${context.packageName}")
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

private fun openOfficialWebsite(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cbouvat.com/saracroche/"))
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
            putExtra(Intent.EXTRA_SUBJECT, "Bug Report - Saracroche Android")
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
