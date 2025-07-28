package com.cbouvat.android.saracroche.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
  val scrollState = rememberScrollState()
  val context = LocalContext.current
  var notificationsEnabled by remember { mutableStateOf(true) }
  var autoUpdateEnabled by remember { mutableStateOf(true) }

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
      // App Settings Section
      SettingsSection(
        title = "Application",
        items = listOf(
          SettingsItem.Switch(
            title = "Notifications",
            subtitle = "Recevoir des notifications importantes",
            icon = Icons.Default.Notifications,
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
          ),
          SettingsItem.Switch(
            title = "Mise à jour automatique",
            subtitle = "Mettre à jour la liste de blocage automatiquement",
            icon = Icons.Default.Update,
            checked = autoUpdateEnabled,
            onCheckedChange = { autoUpdateEnabled = it }
          )
        )
      )

      Divider(modifier = Modifier.padding(horizontal = 16.dp))

      // Privacy Section
      SettingsSection(
        title = "Confidentialité et sécurité",
        items = listOf(
          SettingsItem.Action(
            title = "Politique de confidentialité",
            subtitle = "Consultez notre politique de confidentialité",
            icon = Icons.Default.PrivacyTip,
            onClick = { openPrivacyPolicy(context) }
          ),
          SettingsItem.Action(
            title = "Permissions",
            subtitle = "Gérer les permissions de l'application",
            icon = Icons.Default.Security,
            onClick = { openAppSettings(context) }
          )
        )
      )

      Divider(modifier = Modifier.padding(horizontal = 16.dp))

      // About Section
      SettingsSection(
        title = "À propos",
        items = listOf(
          SettingsItem.Action(
            title = "Version",
            subtitle = "1.0.0 (1)",
            icon = Icons.Default.Info,
            onClick = { }
          ),
          SettingsItem.Action(
            title = "Code source",
            subtitle = "Voir le code sur GitHub",
            icon = Icons.Default.Code,
            onClick = { openGitHub(context) }
          ),
          SettingsItem.Action(
            title = "Développeur",
            subtitle = "Camille Bouvat",
            icon = Icons.Default.Person,
            onClick = { openDeveloperContact(context) }
          )
        )
      )

      Divider(modifier = Modifier.padding(horizontal = 16.dp))

      // Support Section
      SettingsSection(
        title = "Support",
        items = listOf(
          SettingsItem.Action(
            title = "Signaler un problème",
            subtitle = "Nous faire part d'un bug ou problème",
            icon = Icons.Default.BugReport,
            onClick = { openBugReport(context) }
          ),
          SettingsItem.Action(
            title = "Évaluer l'application",
            subtitle = "Noter l'app sur le Play Store",
            icon = Icons.Default.Star,
            onClick = { openPlayStore(context) }
          ),
          SettingsItem.Action(
            title = "Faire un don",
            subtitle = "Soutenir le développement",
            icon = Icons.Default.Favorite,
            onClick = { /* TODO: Implement donation */ }
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
            text = "Fait avec ❤️ en France",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Application gratuite et open source",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
        }
      }
    }
  }
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
  icon: androidx.compose.ui.graphics.vector.ImageVector,
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
      imageVector = Icons.Default.ChevronRight,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    )
  }
}

@Composable
fun SettingsSwitchItem(
  title: String,
  subtitle: String?,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
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

sealed class SettingsItem {
  data class Action(
    val title: String,
    val subtitle: String?,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
  ) : SettingsItem()

  data class Switch(
    val title: String,
    val subtitle: String?,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
  ) : SettingsItem()
}

// Helper functions
private fun openPrivacyPolicy(context: Context) {
  try {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cbouvat/saracroche/blob/main/PRIVACY.md"))
    context.startActivity(intent)
  } catch (e: Exception) {
    // Handle error silently
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
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cbouvat/saracroche"))
    context.startActivity(intent)
  } catch (e: Exception) {
    // Handle error silently
  }
}

private fun openDeveloperContact(context: Context) {
  try {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data = Uri.parse("mailto:")
      putExtra(Intent.EXTRA_EMAIL, arrayOf("saracroche@cbouvat.com"))
      putExtra(Intent.EXTRA_SUBJECT, "Contact Développeur - Saracroche Android")
    }
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
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
    context.startActivity(intent)
  } catch (e: Exception) {
    try {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
      context.startActivity(intent)
    } catch (e: Exception) {
      // Handle error silently
    }
  }
}
