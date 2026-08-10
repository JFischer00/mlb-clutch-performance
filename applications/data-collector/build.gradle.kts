plugins {
	kotlin("jvm") version "2.3.21"
	kotlin("plugin.spring") version "2.3.21"
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "2.3.21"
	kotlin("plugin.serialization") version "2.3.21"
}

group = "edu.colorado.jofi1212"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")

	implementation(platform("com.google.cloud:spring-cloud-gcp-dependencies:8.0.1"))
	implementation("com.google.cloud.sql:postgres-socket-factory")
	implementation("com.google.cloud:spring-cloud-gcp-starter-sql-postgresql")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation(platform("org.testcontainers:testcontainers-bom:2.0.5"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testImplementation("org.wiremock:wiremock-standalone:3.13.2")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	environment("SPRING_PROFILES_ACTIVE", "local")

	val envFile = file(".env")
	if (envFile.exists()) {
		envFile.readLines().forEach { line ->
			val trimmed = line.trim()
			if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
				val parts = trimmed.split("=", limit = 2)
				val key = parts[0].trim()
				val value = parts[1].trim()
				environment(key, value)
			}
		}
	}
}
