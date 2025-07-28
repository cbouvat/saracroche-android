package com.cbouvat.android.saracroche.ui.report

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen() {
  val scrollState = rememberScrollState()
  var phoneNumber by remember { mutableStateOf("") }
  var selectedProblemType by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var expanded by remember { mutableStateOf(false) }
  val context = LocalContext.current

  val problemTypes = listOf(
    "Sélectionnez un type",
    "Appel de démarchage commercial",
    "Appel frauduleux ou arnaque",
    "Appel répétitif non désiré",
    "Numéro non bloqué par l'app",
    "Faux positif (numéro légitime bloqué)",
    "Autre problème"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Signaler un numéro") }
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp)
        .verticalScroll(scrollState),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      // Header Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.ReportProblem,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.secondary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Aidez-nous à améliorer la protection",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Signalez les numéros indésirables pour améliorer notre système de blocage",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
          )
        }
      }

      // Phone Number Input
      OutlinedTextField(
        value = phoneNumber,
        onValueChange = { phoneNumber = it },
        label = { Text("Numéro de téléphone") },
        placeholder = { Text("Ex: 01 23 45 67 89") },
        leadingIcon = {
          Icon(Icons.Default.Phone, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      // Problem Type Dropdown
      ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = selectedProblemType,
          onValueChange = {},
          readOnly = true,
          label = { Text("Type de problème") },
          trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
          },
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
        )
        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          problemTypes.forEach { option ->
            DropdownMenuItem(
              text = { Text(option) },
              onClick = {
                selectedProblemType = option
                expanded = false
              }
            )
          }
        }
      }

      // Description Input
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description (optionnel)") },
        placeholder = { Text("Décrivez le problème rencontré...") },
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp),
        maxLines = 5
      )

      // Submit Button
      Button(
        onClick = {
          sendReport(context, phoneNumber, selectedProblemType, description)
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = phoneNumber.isNotBlank() && selectedProblemType.isNotBlank() && selectedProblemType != "Sélectionnez un type"
      ) {
        Icon(Icons.Default.Send, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Envoyer le signalement")
      }

      // Info Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Confidentialité",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
              )
            )
            Text(
              text = "Vos signalements nous aident à améliorer la protection. Aucune donnée personnelle n'est stockée.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
          }
        }
      }

      // Quick Actions
      Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp)
        ) {
          Text(
            text = "Actions rapides",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(12.dp))
          
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = { 
                // Open to 33700 SMS reporting
                openSMSReporting(context)
              },
              modifier = Modifier.weight(1f)
            ) {
              Text("SMS 33700", style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(
              onClick = {
                // Open Bloctel website
                openBlocktel(context)
              },
              modifier = Modifier.weight(1f)
            ) {
              Text("Bloctel", style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
    }
  }
}

private fun sendReport(
  context: Context,
  phoneNumber: String,
  problemType: String,
  description: String
) {
  val emailBody = """
    Signalement de numéro indésirable
    
    Numéro: $phoneNumber
    Type de problème: $problemType
    
    Description:
    $description
    
    -----------
    Envoyé depuis l'application Saracroche Android
  """.trimIndent()

  val intent = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:")
    putExtra(Intent.EXTRA_EMAIL, arrayOf("saracroche@cbouvat.com"))
    putExtra(Intent.EXTRA_SUBJECT, "Signalement de numéro - Android")
    putExtra(Intent.EXTRA_TEXT, emailBody)
  }

  try {
    context.startActivity(intent)
  } catch (e: Exception) {
    // Handle error silently
  }
}

private fun openSMSReporting(context: Context) {
  try {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:33700"))
    context.startActivity(intent)
  } catch (e: Exception) {
    // Handle error silently
  }
}

private fun openBlocktel(context: Context) {
  try {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bloctel.gouv.fr/"))
    context.startActivity(intent)
  } catch (e: Exception) {
    // Handle error silently
  }
}
