package com.cbouvat.android.saracroche.ui.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.cbouvat.android.saracroche.ui.donation.DonationSheet

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
    val onActionClick: (() -> Unit)? = null
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
                imageVector = Icons.Rounded.ExpandMore,
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
                    onClick = { helpItem.onActionClick.invoke() },
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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showDonationSheet by remember { mutableStateOf(false) }

    val faqItems = listOf(
        HelpItem(
            title = "Quels numéros sont bloqués ?",
            icon = Icons.Rounded.QuestionMark,
            content = "L'application bloque les préfixes suivants, communiqués par l'ARCEP : 0162, 0163, 0270, 0271, 0377, 0378, 0424, 0425, 0568, 0569, 0948, 0949, ainsi que ceux allant de 09475 à 09479. Ces préfixes sont réservés au démarchage téléphonique. Elle bloque aussi des numéros de téléphone de certains opérateurs comme Manifone, DVS Connect, Ze Telecom, Oxilog, BJT Partners, Ubicentrex, Destiny, Kav El International, Spartel Services et d'autres."
        ),
        HelpItem(
            title = "Comment fonctionne l'application ?",
            icon = Icons.Rounded.Info,
            content = "L'application utilise une extension de blocage d'appels et de SMS fournie par le système pour filtrer les numéros indésirables. Elle est conçue pour être simple et efficace, sans nécessiter de configuration complexe."
        ),
        HelpItem(
            title = "Comment signaler un numéro ?",
            icon = Icons.Rounded.Shield,
            content = "Pour signaler un numéro indésirable, allez dans l'onglet 'Signaler' de l'application. Cela aide à améliorer la liste de blocage et à rendre l'application plus efficace."
        ),
        HelpItem(
            title = "Pourquoi les numéros bloqués apparaissent-ils dans l'historique des appels ?",
            icon = Icons.Rounded.ClearAll,
            content = "L'application utilise une extension de blocage d'appels, le choix a été fait de ne pas supprimer les appels bloqués de l'historique. Cela permet de garder une trace des appels indésirables et de signaler les numéros si nécessaire. Vous pouvez supprimer manuellement les appels bloqués de l'historique si vous le souhaitez."
        ),
        HelpItem(
            title = "Comment soutenir le projet ?",
            icon = Icons.Rounded.Favorite,
            content = "Si vous appréciez l'application et souhaitez soutenir son développement, vous pouvez faire un don. Cela permet de financer le temps de développement et d'amélioration de l'application. Vous pouvez également partager l'application avec vos amis et votre famille pour aider à la faire connaître.",
            actionText = "Faire un don",
            actionIcon = Icons.Rounded.Favorite,
            onActionClick = {
                showDonationSheet = true
            }
        ),
        HelpItem(
            title = "Respect de la vie privée ?",
            icon = Icons.Rounded.Lock,
            content = "Saracroche ne collecte aucune donnée personnelle, n'utilise aucun service tiers et ne transmet aucune information à qui que ce soit. Toutes les données restent sur votre appareil. Le respect de la vie privée est un droit fondamental, même si on n'a rien à cacher."
        ),
        HelpItem(
            title = "Pourquoi l'application est-elle gratuite et sans publicité ?",
            icon = Icons.Rounded.AttachMoney,
            content = "Elle est développée bénévolement par un développeur indépendant (Camille), qui en avait assez de recevoir des appels indésirables. L'application est développée sur son temps libre. Vous pouvez soutenir le projet en faisant un don.",
            actionText = "Faire un don",
            actionIcon = Icons.Rounded.Favorite,
            onActionClick = {
                showDonationSheet = true
            }
        ),
        HelpItem(
            title = "Pourquoi une patte d'ours ?",
            icon = Icons.Rounded.Bolt,
            content = "Sarah est une ourse qui a été sauvée par Camille, le développeur de l'application. C'est elle qui raccroche en disant : « Tu connais Sarah ? », l'autre répond : « Sarah qui ? », et elle répond : « Sarah Croche ! » à chaque appel indésirable qu'elle reçoit. Merci à Sarah.",
        )
    )

    val supportItems = listOf(
        HelpItem(
            title = "Comment signaler un bug ?",
            icon = Icons.Rounded.BugReport,
            content = "En cas de bug ou de problème avec l'application, merci de le signaler sur GitHub ou par e-mail.",
            actionText = "Signaler un bug",
            actionIcon = Icons.Rounded.Email,
            onActionClick = {
                openEmailClient(context)
            }
        ),
        HelpItem(
            title = "Comment noter l'application ?",
            icon = Icons.Rounded.Star,
            content = "Si l'application Saracroche vous est utile, une évaluation sur le Play Store serait appréciée. Ce soutien aide à toucher davantage de personnes et à améliorer continuellement l'application.",
            actionText = "Noter l'application",
            actionIcon = Icons.Rounded.Star,
            onActionClick = {
                openPlayStore(context)
            }
        )
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Aide",
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
                        text = "Bisou 😘",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Space to ensure content is not cut off
            Spacer(modifier = Modifier.height(64.dp))
        }
    }

    if (showDonationSheet) {
        DonationSheet(
            onDismiss = { showDonationSheet = false }
        )
    }
}

private fun openEmailClient(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("saracroche@cbouvat.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Bug - Saracroche Android")
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
                "Bonjour,\n\nJ'ai rencontré un problème avec l'application et voici une capture d'écran :\n\n$deviceInfo\n\nBisou 😘"
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

