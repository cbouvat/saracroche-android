package com.cbouvat.android.saracroche

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cbouvat.android.saracroche.ui.help.HelpScreen
import com.cbouvat.android.saracroche.ui.home.HomeScreen
import com.cbouvat.android.saracroche.ui.report.ReportScreen
import com.cbouvat.android.saracroche.ui.settings.SettingsScreen
import com.cbouvat.android.saracroche.ui.theme.SaracrocheTheme

@Preview(showBackground = true, name = "App Navigation Light", group = "App")
@Composable
fun SaracrocheAppPreview() {
    SaracrocheTheme {
        SaracrocheApp()
    }
}

@Preview(showBackground = true, name = "App Navigation Dark", group = "App")
@Composable
fun SaracrocheAppDarkPreview() {
    SaracrocheTheme(darkTheme = true) {
        SaracrocheApp()
    }
}

@Preview(showBackground = true, name = "Home Screen Light", group = "Screens")
@Composable
fun HomeScreenPreview() {
    SaracrocheTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true, name = "Home Screen Dark", group = "Screens")
@Composable
fun HomeScreenDarkPreview() {
    SaracrocheTheme(darkTheme = true) {
        HomeScreen()
    }
}

@Preview(showBackground = true, name = "Report Screen Light", group = "Screens")
@Composable
fun ReportScreenPreview() {
    SaracrocheTheme {
        ReportScreen()
    }
}

@Preview(showBackground = true, name = "Report Screen Dark", group = "Screens")
@Composable
fun ReportScreenDarkPreview() {
    SaracrocheTheme(darkTheme = true) {
        ReportScreen()
    }
}

@Preview(showBackground = true, name = "Help Screen Light", group = "Screens")
@Composable
fun HelpScreenPreview() {
    SaracrocheTheme {
        HelpScreen()
    }
}

@Preview(showBackground = true, name = "Help Screen Dark", group = "Screens")
@Composable
fun HelpScreenDarkPreview() {
    SaracrocheTheme(darkTheme = true) {
        HelpScreen()
    }
}

@Preview(showBackground = true, name = "Settings Screen Light", group = "Screens")
@Composable
fun SettingsScreenPreview() {
    SaracrocheTheme {
        SettingsScreen()
    }
}

@Preview(showBackground = true, name = "Settings Screen Dark", group = "Screens")
@Composable
fun SettingsScreenDarkPreview() {
    SaracrocheTheme(darkTheme = true) {
        SettingsScreen()
    }
}
