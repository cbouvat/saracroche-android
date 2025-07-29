package com.cbouvat.android.saracroche.ui.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.core.net.toUri

@Composable
fun HelpSection(
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

data class HelpItem(
    val title: String,
    val icon: ImageVector,
    val content: String,
    val actionText: String? = null,
    val actionIcon: ImageVector? = null,
    val onActionClick: ((Context) -> Unit)? = null
)

@Composable
fun HelpItemView(helpItem: HelpItem) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrow_rotation"
    )
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = helpItem.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = helpItem.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "Expand",
                modifier = Modifier.rotate(rotationAngle),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = helpItem.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            )

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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HelpScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val faqItems = listOf(
        HelpItem(
            title = "Quels numéros sont bloqués ?",
            icon = Icons.Default.QuestionMark,
            content = "L'application bloque les préfixes suivants, communiqués par l'ARCEP : 0162, 0163, 0270, 0271, 0377, 0378, 0424, 0425, 0568, 0569, 0948, 0949, ainsi que ceux allant de 09475 à 09479. Ces préfixes sont réservés au démarchage téléphonique. Elle bloque aussi des numéros de téléphone de certains opérateurs comme Manifone, DVS Connect, Ze Telecom, Oxilog, BJT Partners, Ubicentrex, Destiny, Kav El International, Spartel Services et d'autres."
        ),
        HelpItem(
            title = "Comment fonctionne l'application ?",
            icon = Icons.Default.Info,
            content = "L'application utilise une extension de blocage d'appels et de SMS fournie par le système pour filtrer les numéros indésirables. Elle est conçue pour être simple et efficace, sans nécessiter de configuration complexe."
        ),
        HelpItem(
            title = "Comment signaler un numéro ?",
            icon = Icons.Default.Shield,
            content = "Pour signaler un numéro indésirable, allez dans l'onglet 'Signaler' de l'application. Cela aide à améliorer la liste de blocage et à rendre l'application plus efficace."
        ),
        HelpItem(
            title = "Respect de la vie privée",
            icon = Icons.Default.Lock,
            content = "Saracroche ne collecte aucune donnée personnelle, n'utilise aucun service tiers et ne transmet aucune information à qui que ce soit. Toutes les données restent sur votre appareil. Le respect de la vie privée est un droit fondamental, même si on n'a rien à cacher."
        ),
        HelpItem(
            title = "Pourquoi l'application est-elle gratuite et sans publicité ?",
            icon = Icons.Default.AttachMoney,
            content = "Elle est développée bénévolement par un développeur indépendant (Camille), qui en avait assez de recevoir des appels indésirables. L'application est développée sur son temps libre. Vous pouvez soutenir le projet en faisant un don.",
            actionText = "Faire un don",
            actionIcon = Icons.Default.Favorite,
            onActionClick = { context ->
                // TODO: Implement donation functionality
            }
        )
    )

    val supportItems = listOf( 
        HelpItem(
            title = "Comment signaler un bug ?",
            icon = Icons.Default.BugReport,
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
            content = "Si l'application Saracroche vous est utile, une évaluation sur le Play Store serait appréciée. Ce soutien aide à toucher davantage de personnes et à améliorer continuellement l'application.",
            actionText = "Noter l'application",
            actionIcon = Icons.Default.Star,
            onActionClick = { context ->
                openPlayStore(context)
            }
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aide") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            HelpSection(title = "Questions fréquentes") {
                faqItems.forEach { item ->
                    HelpItemView(helpItem = item)
                }
            }

            HelpSection(title = "Support") {
                supportItems.forEach { item ->
                    HelpItemView(helpItem = item)
                }
            }

            Text(
                text = "Bisou 😘",
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                textAlign = TextAlign.Center
            )
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
            data = "mailto:".toUri()
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
            Intent(Intent.ACTION_VIEW, "market://details?id=${context.packageName}".toUri())
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

