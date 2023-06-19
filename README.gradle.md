# Introduction to Gradle

## Goals
- Understanding what a module is and where it is located in a multi-module build.
- Understanding (internal) dependencies between modules in a multi-module build.
- Understanding (external) dependencies of modules.
- Creating artifacts of each module.
- Organizing build logic to prevent duplication.
- Packaging the entire application (each module/artifact of the application).

## Required tools
### Git
In this tutorial we will be using Git. To check and make sure you have a version of Git installed, please run the
following command. If the command returns an error, please install Git
```shell
git --version
```

### Java
Please make sure you have installed a version of Java. Don't worry about the specific version as Gradle's wrapper is 
able to download and use any version we specify. You can check which version of Java is installed by running
```shell
java -version
```

### Gradle
Similarly, you can also check which (if any) version of Gradle is already installed by running
```shell
gradle --version
```
For this tutorial, we will NOT be using Gradle directly, instead we will be using Gradle's wrapper, so
you will NOT have to install Gradle. This also means all invocations to Gradle should be made with 
```shell 
./gradlew
```
and NOT with
```shell 
gradle
```

# 1. Create a git repository
- Create a project directory for this tutorial somewhere.
- In this directory, create a Git repository
    ```shell
    git init
    ```
- Create a file named '.gitignore' and ignore all directories which do not contain sources.
    ```shell
    touch .gitignore
    ```
- Add the following
    ```gitignore
    **/*.idea
    **/*.iml
    **/build
    **/target
    **/.gradle
    ```
- Commit the .gitignore

# 2. Create a directory structure for multiple modules
- Every module should be contained in its own directory. So create a directory for each module. I should include the 
modules 'domain' and 'service'.
- Each module should output an artifact (a .jar or .war file). This artifact should contain all compiled sources.
  A modules consists of sources and test-sources, so create te appropriate directories as described
  [here](https://docs.gradle.org/current/userguide/multi_project_builds.html)
  within each module (like src/main/java/, src/test/java/)
- Hint: The directory structure used by Gradle is identical to 
  [Maven's prescribed directory structure](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html).

  <details>
    <summary>See solution</summary>
  
    ```
    .
    ├── domain
    │   └── src
    │       ├── main
    │       │   └── java
    │       └── test
    │           └── java
    └── service
        └── src
            ├── main
            │   └── java
            └── test
                └── java
    ```
  </details>

# 3. Create Gradle build files
- Create a 'settings.gradle' file in the root of your project. This file should (for now) only contain you overall 
project name and all the modules you want to include in your build.
- Set your project name to 'my-individual-project' and only include the domain module for now...
  <details>
    <summary>See solution</summary>

    ```groovy
    rootProject.name = 'my-individual-project'
    include 'domain'
    ```
  </details>
- Check if your project is part of the build.
  ```shell
  gradlew -q projects
  ```
  
## 3.1 Domain sources
- Create a 'build.gradle' file in the domain module (domain/build.gradle). Add the appropriate plugin to build a 
  [Java library](https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_usage). 
  <details>
    <summary>See solution</summary>

    ```groovy
    plugins {
        id 'java-library'
    }
    ```
  </details>
- Populate the source directory with sources of my-individual-project's domain.
  Add a domain class 'Note.java' to the source's directory, keeping in mind this source file is also part of a Java 
  package.
  <details>
    <summary>Please use this code</summary>
  
    ```java
    package nl.sogyo.myproject.domain;

    public class Note {
    // A dummy domain class
    }
    ```
    In directory
    ```
    .
    └── domain
        ├── build.gradle
        └── src
            └── main
                └── java
                    └── nl
                        └── sogyo
                            └── myproject
                                └── domain
                                    └── Note.java
    ```
  </details>

- Build the project using the appropriate Lifecycle Task from the Java plugin.
  <details>
    <summary>See solution</summary>

    ```shell
    gradlew build
    ```
  </details>
- Make sure you find a jar artifact in domain/build/libs named 'domain.jar'.  
- Make sure we are using Java 18 by adding a 
  [toolchain](https://docs.gradle.org/current/userguide/toolchains.html#sec:consuming) section to the domain's 
  build.gradle. Make sure the toolchain is set to use OpenJDK 18.
  ```groovy
  plugins {
      id 'java-library'
  }
  java {
      toolchain {
          languageVersion.set(JavaLanguageVersion.of(18))
      }
  }
  ```
- Rebuild the project
- Check with javap if Java 18 has been used. The class's major version should be 62. This can be inspected by running
  ```shell
  javap -v domain/build/classes/java/main/nl/sogyo/myproject/domain/Note.class 
  ```
  Look for a property named 'major version'.

## 3.2 Domain tests
- Add the 
  [Maven Central repository](https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:declaring_public_repository) 
  and [declare dependencies](https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:what-are-dependency-configurations) 
  on Junit to the domain/build.gradle. Make sure you are using the correct 
  [dependency configuration](https://docs.gradle.org/current/userguide/java_plugin.html#sec:java_plugin_and_dependency_management) 
  for the Junit dependencies as we only need Junit during the testing phase. It should not end up as a part of our 
  final production build.
  
  We want to use 'org.junit.jupiter:junit-jupiter-api:5.7.0' during the implementation/compilation of our tests and
  'org.junit.jupiter:junit-jupiter-engine:5.7.0' ony during the execution of our tests.
  <details>
    <summary>See solution</summary>

    ```groovy
    plugins {
        id 'java-library'
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(18))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.0'
        testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.0'
    }
    ```
  </details>

- Populate the test sources directory with a test. Make sure one test fails.
  <details>
    <summary>Please use this code</summary>

    ```java
    package nl.sogyo.myproject.domain;

    import org.junit.jupiter.api.Test;
  
    import static org.junit.jupiter.api.Assertions.assertFalse;
  
    public class NoteTest {
  
        @Test
        void test() {
            assertFalse(true);
        }
    }
    ```
    In
    ```
    .
    └── domain
        ├── build.gradle
        └── src
            └── test
                └── java
                    └── nl
                        └── sogyo
                            └── myproject
                                └── domain
                                    └── NoteTest.java
    ```
  </details>
  
- Run the build. Check if it has failed. It probably hasn't.
- Instruct Gradle's test task to use the JUnit Platform during execution of the test task. For this to happen we need 
  to configure the java-library plugin's test task to use Junit's platform. Please consult the 
  ['Getting Started'](https://junit.org/junit5/docs/current/user-guide/#overview-getting-started) section of Junit's 
  documentation. Their example projects are really useful.
  <details>
    <summary>See solution</summary>

    ```groovy
    plugins {
        id 'java-library'
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.0'
        testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.0'
    }

    test {
        useJUnitPlatform()
    }
    ```
  </details>

- Run the build. Check and make sure if it fails.
- Modify the tests to run successfully and rerun the build. 

# 4. Add another module
Using all our gathered knowledge from the previous chapters, we can add a new shiny service module.
- Add the build.gradle for the service module.
- Make the service module part of the build by modifying the settings.gradle.
- Also include the tests for this module.  
  Make sure the service module also correctly runs its unit tests.

# 5. Sharing and enforcing dependencies over the multi-module project
In the previous chapter, we have duplicated our build logic, especially our dependencies. In larger projects
this could blow up in our face (much like the service module did on Apollo 13) when we want to update
to new versions of our dependencies. So we need a way to share build logic across multiple modules.
This can be achieved by creating a 
[gradle convention](https://docs.gradle.org/current/samples/sample_convention_plugins.html#organizing_build_logic).

- Create a buildSrc directory and add a build.gradle, so we can 
  [compile a convention plugin](https://docs.gradle.org/current/samples/sample_convention_plugins.html#compiling_convention_plugins).
  <details> 
    <summary>See solution</summary>

    ```groovy
    plugins {
        id 'groovy-gradle-plugin'
    }
    ```
  </details>
- Create a buildSrc/src/main/groovy/java-conventions.gradle and move as much build logic from the domain and 
  service modules over into the convention.
  <details>
    <summary>See solution</summary>
    ```groovy
    plugins {
        id 'java-library'
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(18))
        }
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.0'
        testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.0'
    }
    test {
        useJUnitPlatform()
    }
    ```
  </details>
- Make sure the java-conventions plugin is used in the domain and service modules.
  This can be achieved by replacing the java-library plugin with the java-convention.
  ```groovy
  plugins {
      id 'java-conventions'
  }
  ```
- Make sure the build runs successfully and creates all artifacts.

# 6. Sharing without enforcing dependencies over the multi-module project
It is also possible to define a set of dependencies which can optionally be used throughout the project. After all,
not all modules must be forced to use a particular dependency, e.g. an XML parser. But if the dependency is used, we 
would like to enforce a particular version. In such a case, you want some kind of 
[dependency management](https://docs.gradle.org/current/userguide/platforms.html).

- In the settings.gradle, define a version catalog with two entries.
  Define the aliases 'junit-api' and 'junit-engine' and add their dependency coordinates.
- Add a version reference to the version catalog so the version of Junit can be upgraded by changing one version number.
  <details>
    <summary>See solution</summary>

    ```groovy
    dependencyResolutionManagement {
        versionCatalogs {
            libs {
                version('junit', '5.7.0')
                library('junit-api', 'org.junit.jupiter', 'junit-jupiter-api').versionRef('junit')
                library('junit-engine', 'org.junit.jupiter', 'junit-jupiter-engine').versionRef('junit')
            }
        }
    }
    ```
  </details>
- Modify the java convention, so it uses the dependencies defined in the version catalog.
  <details>
    <summary>See solution</summary>

    ```groovy
    dependencies {
        testImplementation libs.junit.api
        testRuntimeOnly libs.junit.engine
    }
    ```
  </details>

# 7. Dependencies between modules
- Add a dependency to the service module's build.gradle, so we can use classes from our domain.
  ```mermaid
  graph TD
    module-service[service] --> module-domain[domain] 
  ```
  In this case we will need to declare a 
  [project dependency](https://docs.gradle.org/current/userguide/declaring_dependencies_between_subprojects.html) on 
  the project 'domain'.
  <details>
    <summary>See solution</summary>

    ```groovy
      dependencies {
          implementation project(':domain')
      }
    ```
    (Notice the difference in declaring the dependency: implementation __project(':domain')__ vs.  
    implementation __'org.junit.jupiter:junit-jupiter-api:5.7.0'__
  </details>
- Write a test in the service modules which uses a domain class.
  <details>
    <summary>Please use this code</summary>

    ```java
    package nl.sogyo.myproject.service;
  
    import nl.sogyo.myproject.domain.Note;
    import org.junit.jupiter.api.Test;
  
    import static org.junit.jupiter.api.Assertions.assertNotNull;
  
    public class ServiceTest {
  
        @Test
        void test() {
            assertNotNull(new Note());
        }
    }
    ```
  </details>
  If the dependency is properly declared, the build will succeed, otherwise it will fail with an error
  indicating the Note class could not be found.

Discussion:
- Please think about what project dependencies mean for the order of the build.
- Please think about the scope ('compileOnly', 'runtimeOnly' and 'implementation') of a dependency on the classpath 
and what this might mean for 
  - The availability of classes during compilation or when running tests.
  - Packaging. Do you really want to package Junit with your application?

# 8. Create a build of the entire application
- Create a new module called 'app', with its own build.gradle, and add it to the build.
- Make sure this module also adheres to the java-convention.
- The app module should be dependent on 'domain' and 'service', so add these project dependencies.
- Add a configuration for the 'jar' task (provided by the java-library plug-in) to the service module to 
  [package](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:java_packaging)
  the service and all its dependencies into a single jar. Please don't create your own task.

  The jar task should also customize the manifest. The
  manifest should contain and attribute 'Main-Class' which indicates which class' main method should be called 
  on startup.
 
  Tip: as of Gradle 7, use
  ```groovy
  configurations.compileClasspath
  ```
  to find all files needed.

  <details>
    <summary>See solution</summary>

    ```groovy
    plugins {
        id 'java-conventions'
    }
  
    dependencies {
        implementation project(':domain')
        implementation project(':service')
    }
  
    jar {
        manifest {
            attributes 'Main-Class': 'nl.sogyo.myproject.service.Service'
        }
        archiveClassifier = "dist"
        from {
            configurations.compileClasspath.collect { it.isDirectory() ? it : zipTree(it) }
        }
    }
    ```
  </details>
- Run the build and don't forget to run the added task.  
  ```shell
  gradlew clean build
  ```
- Check if all the domain and service classes are present in the jar file. As a jar file is nothing more than a ZIP 
  archive, with some additional descriptors within, you can view and/or extract the archive with an appropriate tool.

# 9. Adding version information to all the artifacts
- In a jar manifest we would like to include version information from GitLab.
  The information can be obtained from the OS' environment. We could also set an environment variable just before 
  starting gradle. An example using echo
  ```shell
  VERSION='1.0.0'; echo "$VERSION"
  ```
  In the same way, we could start gradle with
  ```shell
  VERSION='1.0.0'; ./gradlew clean build
  ```
- Create a hook into the default jar task and customize the jar's manifest to include the version information. This 
  can be done by extracting a [predefined variable](https://docs.gitlab.com/ee/ci/variables/predefined_variables.html) 
  from the OS' environment while the jar task is creating the manifest. Please use the property "Version" in the 
  manifest. Extra bonus points are awarded when using a fallback version when no value was found in the environment. 
  <details>
    <summary>See solution</summary>

    ```groovy
    plugins {
      id 'java-library'
    }

    java {
      toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
      }
    }
  
    repositories {
      mavenCentral()
    }
    dependencies {
      testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.0'
      testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.0'
    }
  
    jar {
      manifest {
        attributes (
          "Version": "$project.name-${System.env['CI_COMMIT_SHORT_SHA'] ?: 'local'}"
        )
      }
    }
  
    test {
      useJUnitPlatform()
    }
    ```
    Run this with
    ```shell
    CI_COMMIT_SHORT_SHA='1.0.0'; ./gradlew clean build
    ```
  </details>

# References
- https://docs.gradle.org/
- https://maven.apache.org/
- https://junit.org/
