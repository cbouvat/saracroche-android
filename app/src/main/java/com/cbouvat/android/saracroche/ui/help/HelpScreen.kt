package com.cbouvat.android.saracroche.ui.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HelpItem(
    val title: String,
    val icon: ImageVector,
    val iconTint: androidx.compose.ui.graphics.Color,
    val content: String,
    val actionText: String? = null,
    val actionIcon: ImageVector? = null,
    val onActionClick: ((Context) -> Unit)? = null
)

@Preview
@Composable
fun HelpScreen() {
    val helpItems = listOf(
        HelpItem(
            title = "Quels numéros sont bloqués ?",
            icon = Icons.Default.QuestionMark,
            iconTint = MaterialTheme.colorScheme.primary,
            content = "L'application bloque les préfixes suivants, communiqués par l'ARCEP : 0162, 0163, 0270, 0271, 0377, 0378, 0424, 0425, 0568, 0569, 0948, 0949, ainsi que ceux allant de 09475 à 09479. Ces préfixes sont réservés au démarchage téléphonique. Elle bloque aussi des numéros de téléphone de certains opérateurs comme Manifone, DVS Connect, Ze Telecom, Oxilog, BJT Partners, Ubicentrex, Destiny, Kav El International, Spartel Services et d'autres."
        ),
        HelpItem(
            title = "Comment fonctionne l'application ?",
            icon = Icons.Default.Info,
            iconTint = MaterialTheme.colorScheme.primary,
            content = "L'application utilise une extension de blocage d'appels et de SMS fournie par le système pour filtrer les numéros indésirables. Elle est conçue pour être simple et efficace, sans nécessiter de configuration complexe."
        ),
        HelpItem(
            title = "Comment signaler un numéro ?",
            icon = Icons.Default.Shield,
            iconTint = MaterialTheme.colorScheme.primary,
            content = "Pour signaler un numéro indésirable, allez dans l'onglet 'Signaler' de l'application. Cela aide à améliorer la liste de blocage et à rendre l'application plus efficace."
        ),
        HelpItem(
            title = "Pourquoi l'application est-elle gratuite et sans publicité ?",
            icon = Icons.Default.AttachMoney,
            iconTint = MaterialTheme.colorScheme.primary,
            content = "Elle est développée bénévolement par un développeur indépendant (Camille), qui en avait assez de recevoir des appels indésirables. L'application est développée sur son temps libre. Vous pouvez soutenir le projet en faisant un don.",
            actionText = "Faire un don",
            actionIcon = Icons.Default.Favorite,
            onActionClick = {
                // TODO: Implement donation functionality
            }
        ),
        HelpItem(
            title = "Comment signaler un bug ?",
            icon = Icons.Default.BugReport,
            iconTint = MaterialTheme.colorScheme.primary,
            content = "En cas de bug ou de problème avec l'application, merci de le signaler sur GitHub ou par e-mail.",
            actionText = "Signaler un bug",
            actionIcon = Icons.Default.Email,
            onActionClick = { context ->
                openEmailClient(context)
            }
        ),
        HelpItem(
            title = "Comment noter l'application ?",
            icon = Icons.Default.Star,
            iconTint = MaterialTheme.colorScheme.primary,
            content = "Si l'application Saracroche vous est utile, une évaluation sur le Play Store serait appréciée. Ce soutien aide à toucher davantage de personnes et à améliorer continuellement l'application.",
            actionText = "Noter l'application",
            actionIcon = Icons.Default.Star,
            onActionClick = { context ->
                openPlayStore(context)
            }
        ),
        HelpItem(
            title = "Respect de la vie privée",
            icon = Icons.Default.Lock,
            iconTint = MaterialTheme.colorScheme.primary,
            content = "Saracroche ne collecte aucune donnée personnelle, n'utilise aucun service tiers et ne transmet aucune information à qui que ce soit. Toutes les données restent sur votre appareil. Le respect de la vie privée est un droit fondamental, même si on n'a rien à cacher."
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(helpItems) { helpItem ->
            HelpCard(helpItem = helpItem)
        }

        item {
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
}

@Composable
fun HelpCard(helpItem: HelpItem) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrow_rotation"
    )
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
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
                        imageVector = helpItem.icon,
                        contentDescription = null,
                        tint = helpItem.iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = helpItem.title,
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
                    text = helpItem.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp
                    )
                )

                // Afficher le bouton d'action s'il existe
                if (helpItem.actionText != null && helpItem.actionIcon != null && helpItem.onActionClick != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { helpItem.onActionClick.invoke(context) },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = helpItem.actionIcon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(helpItem.actionText)
                    }
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
