import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

plugins {
	id("java")
	id("maven-publish")
	id("signing")
	id("com.gradleup.shadow") version "9.0.0-beta4"
	id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "io.github.deathgod7"
version = "1.1.2-SNAPSHOT-1"
description = "A lib to aid in development for my java stuff."

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

    // ---------- [ MySQL Connector ] ----------
    // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    testImplementation("com.mysql:mysql-connector-j:8.3.0")

    // ---------- [ MongoDB ] ----------
    // https://www.mongodb.com/docs/drivers/java/sync/current/quick-start/#quick-start
    // !!! Mongo DB for efficient and quick BSON database !!!
    implementation("org.mongodb:mongodb-driver-sync:4.10.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.withType<ShadowJar> {
    //minimize()
    mergeServiceFiles()
    archiveFileName.set("${project.name}-${project.version}-all.jar")

    relocate ("com.zaxxer", "io.github.deathgod7")
    relocate ("org.mongodb", "io.github.deathgod7")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

val targetJavaVersion = 11

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
    options.compilerArgs.add("-Xlint:unchecked")
}

extra["isReleaseVersion"] = !(
		version.toString().contains("SNAPSHOT", ignoreCase = true) ||
				version.toString().contains("RC", ignoreCase = true)
		)
extra["groupID"] = "io.github.deathgod7"
extra["artifactID"] = "SE7ENLib"

tasks.register("printVersionInfo") {
	doLast {
		val isRelease = project.extra["isReleaseVersion"] as Boolean
		println("Publishing version: $version")
		println("Is this a release version? $isRelease")
		println(if (isRelease) "This is a PUBLIC RELEASE." else "This is a SNAPSHOT or RC version.")
	}
}

tasks.named("publish") {
	dependsOn("printVersionInfo")
}

mavenPublishing {
	if (extra["isReleaseVersion"] as Boolean) {
		publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
		signAllPublications()

		// Common configuration for both releases and snapshots
		configure(JavaLibrary(
			javadocJar = JavadocJar.Javadoc(),
			sourcesJar = true,
		))

		coordinates(
			groupId = extra["groupID"].toString(),
			artifactId = extra["artifactID"].toString(),
			version = project.version.toString()
		)

		pom {
			name.set("SE7ENLib")
			description.set(project.description)
			url.set("https://github.com/DeathGOD7/SE7ENLib")
			properties = mapOf(
				"release-type" to if (project.extra["isReleaseVersion"] as Boolean) "PUBLIC RELEASE" else "SNAPSHOT RELEASE"
			)
			licenses {
				license {
					name.set("GNU GENERAL PUBLIC LICENSE 3.0")
					url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
				}
			}
			developers {
				developer {
					id.set("deathgod7")
					name.set("Death GOD 7")
					email.set("seven@sthalokendra.com.np")
				}
			}
			scm {
				url.set("https://github.com/DeathGOD7/SE7ENLib.git")
			}
		}
	}
}

publishing {
	if (!(extra["isReleaseVersion"] as Boolean)) {
		repositories {
			maven {
				name = "SE7ENRepository"
				url = uri("https://repo.sthalokendra.com.np/snapshots")
				credentials(PasswordCredentials::class)
				authentication {
					create<BasicAuthentication>("basic")
				}
			}
		}

		publications {
			create<MavenPublication>("SE7ENRepositorySnapshot") {
				from(components["java"])
				groupId = extra["groupID"].toString()
				artifactId = extra["artifactID"].toString()
				version = project.version.toString()

				pom {
					name.set("SE7ENLib")
					description.set(project.description)
					url.set("https://github.com/DeathGOD7/SE7ENLib")
					properties = mapOf(
						"release-type" to if (project.extra["isReleaseVersion"] as Boolean) "PUBLIC RELEASE" else "SNAPSHOT RELEASE"
					)
					licenses {
						license {
							name.set("GNU GENERAL PUBLIC LICENSE 3.0")
							url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
						}
					}
					developers {
						developer {
							id.set("deathgod7")
							name.set("Death GOD 7")
							email.set("seven@sthalokendra.com.np")
						}
					}
					scm {
						url.set("https://github.com/DeathGOD7/SE7ENLib.git")
					}
				}
			}
		}
	}
}

signing {
	// Configure the GPG key to use for signing
	useGpgCmd()

}

tasks.withType<JavaCompile>().configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.test {
    useJUnitPlatform()
}
