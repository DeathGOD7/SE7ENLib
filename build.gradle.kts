import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.deathgod7.SE7ENLib"
version = "1.1.0"
description = "A lib to aid in development of my plugins."

repositories {
    mavenLocal()
    mavenCentral()

    // ---------- [ Sonatype ] ----------
    maven(url = "https://oss.sonatype.org/content/groups/public/")

    // ---------- [ Jitpack ] ----------
    maven (url = "https://jitpack.io/")

    // ---------- [ CodeMC ] ----------
    maven(url = "https://repo.codemc.org/repository/maven-public/")

    // ---------- [ Apache Maven ] ----------
    maven(url ="https://repo.maven.apache.org/maven2/")

}

dependencies {
    // ---------- [ SLF4J ] ----------
    // For Internal Testing
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.22.1")
    // ---------- [ DotEnv ] ----------
    // For Internal Testing
    // JAVA 11 +
    // testImplementation("io.github.cdimascio:dotenv-java:3.0.0")
    // JAVA 8 +
    testImplementation("io.github.cdimascio:dotenv-java:2.3.2")
    // ---------- [ SQLite JDBC ] ----------
    // For Internal Testing
    testImplementation("org.xerial:sqlite-jdbc:3.43.2.1")

    // ---------- [ HikariCP ] ----------
    // https://mvnrepository.com/artifact/com.zaxxer/HikariCP
    // !!! Hikari CP for efficient MySQL support. Fast as F bois. :> !!!
    // JAVA 11 +
	// implementation("com.zaxxer:HikariCP:5.0.1")
    // JAVA 8 +
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("mysql:mysql-connector-java:8.0.33")

    // ---------- [ MongoDB ] ----------
    // https://www.mongodb.com/docs/drivers/java/sync/current/quick-start/#quick-start
    // !!! Mongo DB for efficient and quick BSON database !!!
    implementation("org.mongodb:mongodb-driver-sync:4.10.2")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.withType<ShadowJar> {
    minimize()
    mergeServiceFiles()
    archiveFileName.set("${project.name}-${project.version}-shadow.jar")
    relocate ("com.zaxxer", "com.github.deathgod7")
//    relocate ("mysql", "com.github.deathgod7")
    relocate ("org.mongodb", "com.github.deathgod7")
}


tasks {
    build {
        dependsOn(shadowJar)
    }
}

val targetJavaVersion = 8

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

if (hasProperty("buildScan")) {
    extensions.findByName("buildScan")?.withGroovyBuilder {
        setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
        setProperty("termsOfServiceAgree", "yes")
    }
}

tasks.withType<JavaCompile> {
    val sourceCompatibility = JavaVersion.VERSION_1_8
    val targetCompatibility = JavaVersion.VERSION_1_8
}

// for publishing in jitpack
publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = "com.github.deathgod7"
      artifactId = "SE7ENLib"
      version = "1.0.0"

      from(components["java"])
    }
  }
}

tasks.withType<JavaCompile>().configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.test {
    useJUnitPlatform()
}
