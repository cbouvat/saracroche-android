# Saracroche Android

> 📱 **Also available for iOS**: Check out [Saracroche iOS](https://github.com/cbouvat/saracroche-ios) for iPhone and iPad users!

## Description

Saracroche is an Android app that protects you from unwanted calls by blocking spam phone calls. It's designed to be simple, effective, and privacy-friendly.

## Features

- 🛡️ Automatically blocks numbers
- 📱 Native Android application
- 🔒 Privacy-respectful: no call data is collected
- 🔄 Regular updates of the number database

## Installation

### Google Play Store
Saracroche is available on the Google Play Store : [Download Saracroche](https://play.google.com/store/apps/details?id=com.cbouvat.android.saracroche)

### F-Droid
Saracroche will soon be available on F-Droid for users who prefer open-source app stores.

### Building from Source
1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Build and run the project on your device or emulator

**Requirements:**
- Android Studio
- Android SDK API level 29 or higher
- Gradle 8.0+

## Technology Stack

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **MVVM Architecture** - Clean architecture pattern
- **Android Call Screening API** - For call blocking functionality

## List of prefix numbers

The first list is the one containing numbers reserved for telemarketing by ARCEP : https://www.arcep.fr/la-regulation/grands-dossiers-thematiques-transverses/la-numerotation.html
And other numbers by the community.

### Information about prefixes

All prefixes are communicated by ARCEP : https://www.data.gouv.fr/fr/datasets/ressources-en-numerotation-telephonique/ and https://www.data.gouv.fr/fr/datasets/identifiants-de-communications-electroniques/
- `MAJNUM.csv` file for the list of prefixes : https://extranet.arcep.fr/uploads/MAJNUM.csv
- `identifiants_CE.csv` file for the operators of the prefixes : https://extranet.arcep.fr/uploads/identifiants_CE.csv

Tool to identify the operator by prefix : https://www.arcep.fr/mes-demarches-et-services/entreprises/fiches-pratiques/base-numerotation.html

## Contributing

Contributions are welcome! Here's how you can help:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Sponsorship

If you find Saracroche useful, consider sponsoring the project to help with maintenance and new features:

- [GitHub Sponsors](https://github.com/sponsors/cbouvat)

## Star the Project ⭐

If you like Saracroche, please consider giving it a star on GitHub to show your support and help others discover the project.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
