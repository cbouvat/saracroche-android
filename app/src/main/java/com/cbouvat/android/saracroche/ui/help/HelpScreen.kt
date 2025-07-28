package com.cbouvat.android.saracroche.ui.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun HelpScreen() {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(scrollState),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
      // Quels numéros sont bloqués ?
      ExpandableCard(
        title = "Quels numéros sont bloqués ?",
        icon = Icons.Default.QuestionMark,
        iconTint = MaterialTheme.colorScheme.primary,
        content = "L'application bloque les préfixes suivants, communiqués par l'ARCEP : 0162, 0163, 0270, 0271, 0377, 0378, 0424, 0425, 0568, 0569, 0948, 0949, ainsi que ceux allant de 09475 à 09479. Ces préfixes sont réservés au démarchage téléphonique. Elle bloque aussi des numéros de téléphone de certains opérateurs comme Manifone, DVS Connect, Ze Telecom, Oxilog, BJT Partners, Ubicentrex, Destiny, Kav El International, Spartel Services et d'autres."
      )

      // Comment fonctionne l'application ?
      ExpandableCard(
        title = "Comment fonctionne l'application ?",
        icon = Icons.Default.Info,
        iconTint = MaterialTheme.colorScheme.tertiary,
        content = "L'application utilise une extension de blocage d'appels et de SMS fournie par le système pour filtrer les numéros indésirables. Elle est conçue pour être simple et efficace, sans nécessiter de configuration complexe."
      )

      // Comment signaler un numéro ?
      ExpandableCard(
        title = "Comment signaler un numéro ?",
        icon = Icons.Default.Shield,
        iconTint = MaterialTheme.colorScheme.secondary,
        content = "Pour signaler un numéro indésirable, allez dans l'onglet 'Signaler' de l'application. Cela aide à améliorer la liste de blocage et à rendre l'application plus efficace."
      )

      // Pourquoi gratuite et sans publicité ?
      ExpandableCardWithAction(
        title = "Pourquoi l'application est-elle gratuite et sans publicité ?",
        icon = Icons.Default.AttachMoney,
        iconTint = MaterialTheme.colorScheme.primary,
        content = "Elle est développée bénévolement par un développeur indépendant (Camille), qui en avait assez de recevoir des appels indésirables. L'application est développée sur son temps libre. Vous pouvez soutenir le projet en faisant un don.",
        actionText = "Faire un don",
        actionIcon = Icons.Default.Favorite,
        onActionClick = {
          // TODO: Implement donation functionality
        }
      )

      // Comment signaler un bug ?
      ExpandableCardWithAction(
        title = "Comment signaler un bug ?",
        icon = Icons.Default.BugReport,
        iconTint = MaterialTheme.colorScheme.error,
        content = "En cas de bug ou de problème avec l'application, merci de le signaler sur GitHub ou par e-mail.",
        actionText = "Signaler un bug",
        actionIcon = Icons.Default.Email,
        onActionClick = { context ->
          openEmailClient(context)
        }
      )

      // Comment noter l'application ?
      ExpandableCardWithAction(
        title = "Comment noter l'application ?",
        icon = Icons.Default.Star,
        iconTint = MaterialTheme.colorScheme.secondary,
        content = "Si l'application Saracroche vous est utile, une évaluation sur le Play Store serait appréciée. Ce soutien aide à toucher davantage de personnes et à améliorer continuellement l'application.",
        actionText = "Noter l'application",
        actionIcon = Icons.Default.Star,
        onActionClick = { context ->
          openPlayStore(context)
        }
      )

      // Respect de la vie privée
      ExpandableCard(
        title = "Respect de la vie privée",
        icon = Icons.Default.Lock,
        iconTint = MaterialTheme.colorScheme.outline,
        content = "Saracroche ne collecte aucune donnée personnelle, n'utilise aucun service tiers et ne transmet aucune information à qui que ce soit. Toutes les données restent sur votre appareil. Le respect de la vie privée est un droit fondamental, même si on n'a rien à cacher."
      )

      // Message final
      Text(
        text = "Bisou 😘",
        style = MaterialTheme.typography.bodySmall.copy(
          textAlign = TextAlign.Center
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp),
        textAlign = TextAlign.Center
      )
    }
}

@Composable
fun ExpandableCard(
  title: String,
  icon: ImageVector,
  iconTint: androidx.compose.ui.graphics.Color,
  content: String
) {
  var isExpanded by remember { mutableStateOf(false) }
  val rotationAngle by animateFloatAsState(
    targetValue = if (isExpanded) 180f else 0f,
    label = "arrow_rotation"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded },
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold
            )
          )
        }
        Icon(
          imageVector = Icons.Default.ExpandMore,
          contentDescription = "Expand",
          modifier = Modifier.rotate(rotationAngle)
        )
      }

      if (isExpanded) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = content,
          style = MaterialTheme.typography.bodyMedium.copy(
            lineHeight = 20.sp
          )
        )
      }
    }
  }
}

@Composable
fun ExpandableCardWithAction(
  title: String,
  icon: ImageVector,
  iconTint: androidx.compose.ui.graphics.Color,
  content: String,
  actionText: String,
  actionIcon: ImageVector,
  onActionClick: (Context) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  val rotationAngle by animateFloatAsState(
    targetValue = if (isExpanded) 180f else 0f,
    label = "arrow_rotation"
  )
  val context = LocalContext.current

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded },
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold
            )
          )
        }
        Icon(
          imageVector = Icons.Default.ExpandMore,
          contentDescription = "Expand",
          modifier = Modifier.rotate(rotationAngle)
        )
      }

      if (isExpanded) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = content,
          style = MaterialTheme.typography.bodyMedium.copy(
            lineHeight = 20.sp
          )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
          onClick = { onActionClick(context) },
          modifier = Modifier.padding(top = 8.dp)
        ) {
          Icon(
            imageVector = actionIcon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(actionText)
        }
      }
    }
  }
}

private fun openEmailClient(context: Context) {
  try {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName
    val versionCode = packageInfo.longVersionCode
    
    val deviceInfo = """
      
      
      -----------
      Version de l'application : $versionName ($versionCode)
      Appareil : ${Build.MANUFACTURER} ${Build.MODEL}
      Version Android : ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data = Uri.parse("mailto:")
      putExtra(Intent.EXTRA_EMAIL, arrayOf("saracroche@cbouvat.com"))
      putExtra(Intent.EXTRA_SUBJECT, "Bug Android")
      putExtra(Intent.EXTRA_TEXT, deviceInfo)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
      context.startActivity(intent)
    }
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
