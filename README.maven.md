# Introduction to Maven

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
Please make sure you have installed a version of Java (1.8+). You can check which version of Java is installed by running
```shell
java -version
```

### Maven
Please check which (if any) version of Maven (3.6.0+) is already installed by running
```shell
mvn --version
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
  [here](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)
  within each module (like src/main/java/, src/test/java/)
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

# 3. Create Maven build files
- Create a minimal [project object model](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) 
file in the root of your project. This file should only contain you overall project name and all the modules you want 
to include in your build.
- Set your project artifact id to 'my-individual-project'. As a version use '1.0.0-SNAPSHOT'.
  Also make sure the packaging is set to 'pom'.
  <details>
    <summary>See solution</summary>

    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>nl.sogyo</groupId>
        <artifactId>my-individual-project</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <packaging>pom</packaging>
    </project>
    ```
  </details>
- Check and display all the modules of your project.
  This will also perform syntax checks on your pom.xml file(s).
  ```shell
  mvn help:evaluate -Dexpression=project.modules
  ```
  
## 3.1 Domain sources
- Create a 'pom.xml' file in the domain module (domain/pom.xml). Please make sure to use a group id and artifact id
  which is hierarchically a submodule of our top level project and the domain's parent is properly set. 
  For easier integration with CI servers, apply
  [CI-friendly version numbers](https://maven.apache.org/maven-ci-friendly.html).
  <details>
    <summary>See solution</summary>

    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>
        <parent>
            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>  
        </parent>

        <groupId>nl.sogyo.my-individual-project</groupId>
        <artifactId>domain</artifactId>

    </project>
    ```
    Please note we can omit the version from this artifact and use the version from the parent block.
  </details>
- Add the module to the top level pom.xml using the appropriate tags. Also make sure to set the version.
  <details>
    <summary>See solution</summary>

        <project xmlns="http://maven.apache.org/POM/4.0.0">
            <modelVersion>4.0.0</modelVersion>

            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>
            <packaging>pom</packaging>

            <properties>
                <revision>1.0.0-SNAPSHOT</revision>
            </properties>

            <modules>
                <module>domain</module>
            </modules>
        </project>
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
        ├── pom.xml
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

- Build the project using the appropriate 
  [Lifecycle Phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).
  <details>
    <summary>See solution</summary>

    ```shell
    mvn package
    ```
  </details>
- Make sure you find a jar artifact in domain/target/ named 'domain.jar'.
- For subsequent builds it's advisable to also clean the target/ directory. To do so, use the solution below.
  <details>
    <summary>See solution</summary>

    ```shell
    mvn clean package
    ```
  </details>

## 3.2 Domain tests
- Add a dependency on Junit by adding it to the domain/pom.xml. Make sure you are using the correct 
  [scope](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html) 
  for the Junit dependencies as we only need Junit during the testing phase. It should not end up as a part of our 
  final production build.
  
  We want to use 'org.junit.jupiter:junit-jupiter-api:5.7.0' during the implementation/compilation of our tests and
  'org.junit.jupiter:junit-jupiter-engine:5.7.0' ony during the execution of our tests.
  <details>
    <summary>See solution</summary>

    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <parent>
            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>
        </parent>
    
        <groupId>nl.sogyo.my-individual-project</groupId>
        <artifactId>domain</artifactId>
    
        <dependencies>
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter-api</artifactId>
                <version>5.7.0</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </project>
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
        ├── pom.xml
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
- Instruct Maven to use the JUnit Platform during execution of the test task. For this to happen we need 
  to add Maven's JUnit plugin which is capable of running Junit 5. Please consult the 
  ['Getting Started'](https://junit.org/junit5/docs/current/user-guide/#overview-getting-started) section of Junit's 
  documentation. Their example projects are really useful.
  <details>
    <summary>See solution</summary>

    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <parent>
            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>
        </parent>
    
        <groupId>nl.sogyo.my-individual-project</groupId>
        <artifactId>domain</artifactId>
    
        <dependencies>
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter-api</artifactId>
                <version>5.7.0</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>3.0.0-M6</version>
                </plugin>
            </plugins>
        </build>
    </project>
    ```
  </details>
- Run the build. Check and make sure if it fails.
- Modify the tests to run successfully and rerun the build. 

# 4. Add another module
Using all our gathered knowledge from the previous chapters, we can add a new shiny service module.
- Add the pom.xml for the service module.
- Make the service module part of the build by modifying the root pom.
- Also include the tests for this module.  
  Make sure the service module also correctly runs its unit tests.

# 5. Sharing and enforcing dependencies over the multi-module project
In the previous chapter, we have duplicated our build logic, especially our dependencies. In larger projects
this could blow up in our face (much like the service module did on Apollo 13) when we want to update
to new versions of our dependencies. So we need a way to share build logic across multiple modules.

- Move the dependency and build's plugins block from the domain and service modules to the root/parent pom.
  <details> 
    <summary>See solution</summary>

    pom.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <groupId>nl.sogyo</groupId>
        <artifactId>my-individual-project</artifactId>
        <version>${revision}</version>
        <packaging>pom</packaging>
    
        <properties>
            <revision>1.0.0-SNAPSHOT</revision>
        </properties>
    
        <modules>
            <module>domain</module>
            <module>service</module>
        </modules>
    
        <dependencies>
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter-api</artifactId>
                <version>5.7.0</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>3.0.0-M6</version>
                </plugin>
            </plugins>
        </build>
    </project>
    ```

    domain/pom.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <parent>
            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>
        </parent>
    
        <groupId>nl.sogyo.my-individual-project</groupId>
        <artifactId>domain</artifactId>
    </project>
    ```

    service/pom.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <parent>
            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>
        </parent>
    
        <groupId>nl.sogyo.my-individual-project</groupId>
        <artifactId>service</artifactId>
    </project>
    ```
  </details>
- Make sure the build runs successfully and creates all artifacts.
- This project setup is not ideal. We have now enforced that every module will use a fixed set of dependencies and
build plugins. This is not flexible and what we need for larger projects. We don't want to clutter our classpath with
libraries we don't need (in a submodule).

# 6. Sharing without enforcing dependencies over the multi-module project
It is also possible to define a set of dependencies which can optionally be used throughout the project. After all,
not all modules must be forced to use a particular dependency, e.g. an XML parser. But if the dependency is used, we 
would like to enforce a particular version. In such a case, you want some kind of 
[dependency management](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Management).

- In the root pom, create a dependency management block and add the Junit dependencies there. 
- Do the same for the management of build plugins.
- Add a dependency to Junit in the domain and service submodules. Omit the version number in the submodules.
  <details>
    <summary>See solution</summary>

    pom.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <groupId>nl.sogyo</groupId>
        <artifactId>my-individual-project</artifactId>
        <version>${revision}</version>
        <packaging>pom</packaging>
    
        <properties>
            <revision>1.0.0-SNAPSHOT</revision>
        </properties>
    
        <modules>
            <module>domain</module>
            <module>service</module>
        </modules>
    
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.junit.jupiter</groupId>
                    <artifactId>junit-jupiter-api</artifactId>
                    <version>5.7.0</version>
                    <scope>test</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    
        <build>
            <pluginManagement>
                <plugins>
                    <plugin>
                        <artifactId>maven-surefire-plugin</artifactId>
                        <version>3.0.0-M6</version>
                    </plugin>
                </plugins>
            </pluginManagement>
        </build>
    </project>
    ```
    domain/pom.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
      <modelVersion>4.0.0</modelVersion>

      <parent>
        <groupId>nl.sogyo</groupId>
        <artifactId>my-individual-project</artifactId>
        <version>${revision}</version>
      </parent>
    
      <groupId>nl.sogyo.my-individual-project</groupId>
      <artifactId>domain</artifactId>
    
      <build>
        <plugins>
          <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
          </plugin>
        </plugins>
      </build>
    
      <dependencies>
        <dependency>
          <groupId>org.junit.jupiter</groupId>
          <artifactId>junit-jupiter-api</artifactId>
        </dependency>
      </dependencies>
    </project>
    ```
    service/pom.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
      <modelVersion>4.0.0</modelVersion>

      <parent>
        <groupId>nl.sogyo</groupId>
        <artifactId>my-individual-project</artifactId>
        <version>${revision}</version>
      </parent>
    
      <groupId>nl.sogyo.my-individual-project</groupId>
      <artifactId>service</artifactId>
    
      <build>
        <plugins>
          <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
          </plugin>
        </plugins>
      </build>
    
      <dependencies>
        <dependency>
          <groupId>org.junit.jupiter</groupId>
          <artifactId>junit-jupiter-api</artifactId>
        </dependency>
      </dependencies>
    </project>
    ```
  </details>
- Make sure the project builds successfully.

# 7. Dependencies between modules
- Add a dependency to the service module's pom.xml, so we can use classes from our domain.
  ```mermaid
  graph TD
    module-service[service] --> module-domain[domain] 
  ```
  In this case we will need to declare a 
  [project dependency](https://maven.apache.org/guides/getting-started/index.html#how-do-i-build-more-than-one-project-at-once) on 
  the project 'domain'.
  <details>
    <summary>See solution</summary>

    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <parent>
            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>
        </parent>
    
        <groupId>nl.sogyo.my-individual-project</groupId>
        <artifactId>service</artifactId>
    
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    
        <dependencies>
            <dependency>
                <groupId>nl.sogyo.my-individual-project</groupId>
                <artifactId>domain</artifactId>
                <version>${revision}</version>
            </dependency>
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter-api</artifactId>
            </dependency>
        </dependencies>

    </project>
    ```
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
- Please think about the scope ('compile', 'test, 'runtime' and 'provided') of a dependency on the classpath 
and what this might mean for 
  - The availability of classes during compilation or when running tests.
  - Packaging. Do you really want to package Junit with your application?

# 8. Create a build of the entire application
- Create a new module called 'app', with its own pom.xml, and add it to the build.
- The app module should be dependent on 'domain' and 'service', so add these project dependencies.
- Add the [assembly plugin](https://maven.apache.org/plugins/maven-assembly-plugin/usage) 
  to create the 'jar' (provided by the assembly plugin's jar-with-dependencies descriptor reference) to the app module, 
  so it can package the service and all its dependencies into a single jar. Make sure the plugin execution is bound to 
  the 'package' phase of the build.
  <details>
    <summary>See solution</summary>

    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <parent>
            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>
        </parent>
    
        <groupId>nl.sogyo.my-individual-project</groupId>
        <artifactId>app</artifactId>
    
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-assembly-plugin</artifactId>
                    <configuration>
                        <archive>
                            <manifest>
                                <mainClass>nl.sogyo.myproject.service.Service</mainClass>
                            </manifest>
                        </archive>
                        <descriptorRefs>
                            <descriptorRef>jar-with-dependencies</descriptorRef>
                        </descriptorRefs>
                    </configuration>
                    <executions>
                        <execution>
                            <id>make-assembly</id> <!-- this is used for inheritance merges -->
                            <phase>package</phase> <!-- bind to the packaging phase -->
                            <goals>
                                <goal>single</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    
        <dependencies>
            <dependency>
                <groupId>nl.sogyo.my-individual-project</groupId>
                <artifactId>domain</artifactId>
                <version>${revision}</version>
            </dependency>
            <dependency>
                <groupId>nl.sogyo.my-individual-project</groupId>
                <artifactId>service</artifactId>
                <version>${revision}</version>
            </dependency>
        </dependencies>
    </project>
    ```
  </details>
- Run the build.  
  ```shell
  mvn clean package
  ```
- Check if all the domain and service classes are present in the jar file. As a jar file is nothing more than a ZIP 
  archive, with some additional descriptors within, you can view and/or extract the archive with an appropriate tool.

# 9. Adding version information to all the artifacts
- In a jar manifest we would like to include version information from GitLab.
  The information can be obtained from the OS' environment. We could also set an environment variable just before 
  starting Maven. An example using echo
  ```shell
  VERSION='1.0.0'; echo "$VERSION"
  ```
  In the same way, we could start Maven with
  ```shell
  VERSION='1.0.0'; ./mvn clean build
  ```
- The version information can be added to each jar by configuring the
  [jar plugin](https://maven.apache.org/plugins/maven-jar-plugin/examples/manifest-customization.html) correctly.
  So, create a configuration in the root pom.xml and make sure the plugin is used in all submodules.
  <details>
    <summary>See solution</summary>

    pom.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <groupId>nl.sogyo</groupId>
        <artifactId>my-individual-project</artifactId>
        <version>${revision}</version>
        <packaging>pom</packaging>
    
        <properties>
            <revision>1.0.0-SNAPSHOT</revision>
        </properties>
    
        <modules>
            <module>app</module>
            <module>domain</module>
            <module>service</module>
        </modules>
    
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.junit.jupiter</groupId>
                    <artifactId>junit-jupiter-api</artifactId>
                    <version>5.7.0</version>
                    <scope>test</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    
        <build>
            <pluginManagement>
                <plugins>
                    <plugin>
                        <artifactId>maven-surefire-plugin</artifactId>
                        <version>3.0.0-M6</version>
                    </plugin>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-jar-plugin</artifactId>
                        <version>3.2.2</version>
                        <configuration>
                            <archive>
                                <manifestEntries>
                                    <Version>${revision}</Version>
                                </manifestEntries>
                            </archive>
                        </configuration>
                    </plugin>
                </plugins>
            </pluginManagement>
        </build>
    </project>
    ```

    domain/pom.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <parent>
            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>
        </parent>
    
        <groupId>nl.sogyo.my-individual-project</groupId>
        <artifactId>domain</artifactId>
    
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-jar-plugin</artifactId>
                </plugin>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    
        <dependencies>
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter-api</artifactId>
            </dependency>
        </dependencies>
    </project>
    ```

    service/pom.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>

        <parent>
            <groupId>nl.sogyo</groupId>
            <artifactId>my-individual-project</artifactId>
            <version>${revision}</version>
        </parent>
    
        <groupId>nl.sogyo.my-individual-project</groupId>
        <artifactId>service</artifactId>
    
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-jar-plugin</artifactId>
                </plugin>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    
        <dependencies>
            <dependency>
                <groupId>nl.sogyo.my-individual-project</groupId>
                <artifactId>domain</artifactId>
                <version>${revision}</version>
            </dependency>
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter-api</artifactId>
            </dependency>
        </dependencies>
    
    </project>
    ```
  </details>
- If you build the project now and inspect each jar, you'll notice all jar's except the jar-with-dependencies contains
  the version information we desire so add the same manifest customization you added to the jar-plugin.
  <details>
    <summary>See solution</summary>
    ```xml
    <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <configuration>
            <archive>
                <manifest>
                    <mainClass>nl.sogyo.myproject.service.Service</mainClass>
                </manifest>
                <manifestEntries>
                    <Version>${revision}</Version>
                </manifestEntries>
            </archive>
            <descriptorRefs>
                <descriptorRef>jar-with-dependencies</descriptorRef>
            </descriptorRefs>
        </configuration>
        <executions>
            <execution>
                <id>make-assembly</id> <!-- this is used for inheritance merges -->
                <phase>package</phase> <!-- bind to the packaging phase -->
                <goals>
                    <goal>single</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ```
  </details>
- As you have already created a property called revision, all we need to do is find out how we pass a property from 
  the command line so Maven can use it in its runtime. Luckily, these days Maven is 
  [CI-friendly](https://maven.apache.org/maven-ci-friendly.html).
  <details>
    <summary>See solution</summary>

    ```shell
      mvn -Drevision=2.0.0-SNAPSHOT clean package
    ```
  </details>

# References
- https://maven.apache.org/
- https://junit.org/
