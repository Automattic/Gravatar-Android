Here you can find an in-depth overview of the inner workings of Gravatar for Android. It is designed for contributors and maintainers who want to understand the app's architecture, design decisions, and implementation details. This document covers key topics such as structure, testing, release process, and more. Whether you're debugging, extending functionality, or simply exploring the codebase, this guide will help you navigate and work effectively with the project's technical foundations.

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