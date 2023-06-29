include("domain")
include("service")
include("service")
include("app")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("junit", "5.7.0")
            library("junit-api", "org.junit.jupiter", "junit-jupiter-api").versionRef("junit")
            library("junit-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef("junit")
        }
    }
}
