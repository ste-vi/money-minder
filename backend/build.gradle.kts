import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.graalvm.buildtools.native") version "0.11.0"
    id("org.hibernate.orm") version "6.5.2.Final"
}

group = "com.stevi"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

configurations {
    all {
        exclude(group = "commons-logging", module = "commons-logging")
    }
}

tasks.register("buildNativeAmazonLinux") {
    group = "build"
    description = "Builds a native executable compatible with Amazon Linux 2023"

    doLast {
        exec {
            workingDir(".")
            commandLine("powershell", "-File", "build-native.ps1")
        }
    }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.ehcache:ehcache:3.10.8:jakarta")
	implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.google.api-client:google-api-client:2.6.0")
	implementation("com.google.api-client:google-api-client-jackson2:2.6.0")
    implementation("org.hibernate.orm:hibernate-graalvm:6.5.2.Final")

	implementation("io.jsonwebtoken:jjwt-api:0.13.0")
	implementation("io.jsonwebtoken:jjwt-impl:0.13.0")
	implementation("io.jsonwebtoken:jjwt-jackson:0.13.0")

	implementation("org.liquibase:liquibase-core:4.30.0")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

hibernate {
    enhancement {
        enableAssociationManagement.set(true)
    }
}

tasks.withType<Test> {
	useJUnitPlatform()
}
