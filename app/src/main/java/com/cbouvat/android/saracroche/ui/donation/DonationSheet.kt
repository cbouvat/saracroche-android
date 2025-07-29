package com.cbouvat.android.saracroche.ui.donation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp))
                Text(
                    text = "Soutenez Saracroche",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Heart icon
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFFE91E63) // Pink color
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Description
            Text(
                text = "Saracroche est développée bénévolement par Camille sur son temps libre. Votre don l'aide à consacrer plus de temps à l'amélioration de l'app et au maintien des listes de blocage.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Benefits section
            Text(
                text = "Pourquoi donner ?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            DonationBenefitRow(
                icon = Icons.Default.Code,
                title = "Projet open-source",
                description = "Code source ouvert et transparent"
            )

            DonationBenefitRow(
                icon = Icons.Default.MoneyOff,
                title = "Entièrement gratuit",
                description = "Pas de pub, pas d'abonnement, pas de version premium"
            )

            DonationBenefitRow(
                icon = Icons.Default.Person,
                title = "Développeur indépendant",
                description = "Camille développe bénévolement sur son temps libre"
            )

            DonationBenefitRow(
                icon = Icons.Default.Refresh,
                title = "Mises à jour régulières",
                description = "Nouvelles listes de blocage et améliorations continues"
            )

            DonationBenefitRow(
                icon = Icons.Default.Lock,
                title = "Confidentialité respectée",
                description = "Aucune donnée collectée, tout reste sur votre appareil"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Donation buttons
            DonationButton(
                text = "PayPal",
                backgroundColor = Color(0xFF0070BA),
                textColor = Color.White,
                onClick = { openUrl(context, "https://paypal.me/cbouvat") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            DonationButton(
                text = "GitHub Sponsors",
                backgroundColor = Color.Black,
                textColor = Color.White,
                onClick = { openUrl(context, "https://github.com/sponsors/cbouvat") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            DonationButton(
                text = "Liberapay",
                backgroundColor = Color(0xFFF6C915),
                textColor = Color.Black,
                onClick = { openUrl(context, "https://liberapay.com/cbouvat") }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DonationBenefitRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DonationButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        )
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}

@Preview
@Composable
fun DonationSheetPreview() {
    DonationSheet(onDismiss = {})
}
