# Gravatar Android

A native Android application for Gravatar, allowing users to manage their Gravatar profiles. The app is built using Kotlin and Jetpack Compose.
This Android application provides a mobile interface for Gravatar, a service that provides globally recognized avatars.

## Testing

### Unit Testing

To run unit tests, use the following command:

```bash
./gradlew testDebug
```

### Screenshot Testing

The project uses Roborazzi for screenshot testing. Screenshots are saved in the `screenshotTests/roborazzi` directory of each module.

To run screenshot tests:

1. Execute the tests with the `screenshot` parameter
2. Check the generated screenshots in the output directory

Example:
```bash
./gradlew verifyRoborazziDebug -Pscreenshot 
./gradlew :homeUi:verifyRoborazziDebug -Pscreenshot
```

If you need to update the screenshots, use the `recordRoborazziDebug` task:

```bash
./gradlew recordRoborazziDebug -Pscreenshot
./gradlew :homeUi:recordRoborazziDebug -Pscreenshot
```

## Code Style

### Detekt

The project uses Detekt for static code analysis with the Ktlint wrapper. To run Detekt:

```bash
./gradlew detekt
```

or 

```bash
./gradlew detekt --auto-correct
```

## License

This project is licensed under the Mozilla Public License Version 2.0 (MPL-2.0) - see the [LICENSE](LICENSE) file for details.
