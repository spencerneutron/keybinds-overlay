Gradle re-init notes
====================

This project previously used Gradle. I removed the tracked Gradle build files and wrapper metadata to start clean.

When you want to re-initialize Gradle (recommended approach):

1. Install a local Gradle or a JDK + use Gradle to generate the wrapper. Recommended Gradle: 9.4.2

   Example (locally):

   gradle wrapper --gradle-version 9.4.2

   Commit the generated files: `gradlew`, `gradlew.bat`, and `gradle/wrapper/*`.

2. Add a Java toolchain to the (new) `build.gradle` so contributors don't need to set environment variables:

   java {
     toolchain {
       languageVersion = JavaLanguageVersion.of(17)
     }
   }

   If the plugin must produce Java 8 bytecode for RuneLite compatibility, set the compiler target explicitly:

   tasks.withType(JavaCompile) {
     options.release.set(8)
   }

3. Do NOT hardcode `org.gradle.java.home` or absolute `JAVA_HOME` paths in project files.

4. Add CI (GitHub Actions) that runs `./gradlew build` on a matrix of JDK versions (17, 21, 25) to validate changes.

5. Restore any RuneLite-specific dependencies in `build.gradle` (repository and `compileOnly` entries) as needed.

6. Commit the wrapper and push — after that, `./gradlew build` should work for anyone cloning the repo.

Notes:
- I intentionally removed tracked Gradle files to avoid machine-specific config. The source code and plugin files remain untouched.
- If you want, I can add a starter `build.gradle` with a Java toolchain and a `ci.yml` Action that runs the build.
