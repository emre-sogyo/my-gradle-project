# Development Gradle Tutorial
This section only applies if we need to update this tutorial, specifically, if we need to upgrade the version of Java 
or Gradle (and generate a new Gradle wrapper).

## Prerequisites
Please check the [compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html)
and make sure you're using a version of Java which is able to execute Gradle. If the version of Java and Gradle don't
match, please install a supported version of Java. You can check which version of Java is installed by running
```shell
java -version
```
Similarly, you can also check which (if any) version of Gradle is already installed by running
```shell
gradle --version
```

### Windows
- Download Gradle version 7.x.x.
- Extract the archive to a suitable install location.
- Put the location of Gradle's bin folder in the PATH variable.
- Ensure that JAVA_HOME is set in the PATH variable.
- Test your installation by running
    ```shell
    gradle --version
    ```

### Linux (Debian based)
- Run
    ```shell
  sudo add-apt-repository ppa:cwchien/gradle
  sudo apt-get install gradle
    ```
- Test your installation by running
  ```shell
  gradle --version
  ```

### Docker
- Pull the image
    ```shell
    docker image pull gradle:7.5.0-jdk18
    ```
- Run the image with your project mounted
    ```shell
    docker run --rm -it -v $PWD:/project gradle:7.5.0-jdk18 /bin/bash
    ```
- Test your installation by running
    ```shell
    gradle --version
    ```

## Creating a new wrapper
```shell
gradle wrapper
```

# Development Maven Tutorial
## Prerequisites
Please make sure you have Java installed. A minimum of 1.8.0 is required.
```shell
java -version
```
Similarly, you can also check which (if any) version of Maven is already installed by running
```shell
mvn --version
```

### Windows
- Download Maven version 3.6.x.
- Extract the archive to a suitable install location.
- Put the location of Maven's bin folder in the PATH variable.
- Ensure that JAVA_HOME is set in the PATH variable.
- Test your installation by running
    ```shell
    maven --version
    ```

### Linux (Debian based)
- Run
    ```shell
  sudo apt-get install maven
    ```
- Test your installation by running
  ```shell
  mvn --version
  ```

### Docker
- Pull the image
    ```shell
    docker image pull maven:3.8.1-jdk-8
    ```
- Run the image with your project mounted
    ```shell
    docker run --rm -it -v $PWD:/project maven:3.8.1-jdk-8 /bin/bash
    ```
- Test your installation by running
    ```shell
    gradle --version
    ```
