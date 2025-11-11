import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar

plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.0"
    id("org.springframework.boot") version "4.0.0-RC2"
    id("io.spring.dependency-management") version "1.1.7"
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.named("bootJar"))
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${System.getenv("GITHUB_REPOSITORY")}")
            credentials {
                username = System.getenv("GPR_USER")
                    ?: System.getenv("GITHUB_ACTOR")
                    ?: project.findProperty("gpr.user") as String?
                password = System.getenv("GPR_KEY")
                    ?: System.getenv("GITHUB_TOKEN")
                    ?: project.findProperty("gpr.key") as String?
            }
        }
    }
}

tasks.named<Jar>("jar") {
    enabled = true
}

group = "com.bamboo.assur.partner-insurers"
version = (findProperty("version") as String?) ?: "0.0.0"
description = "Registre maître des assureurs partenaires de la plateforme Bamboo Assur"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    mavenLocal() // Keep for local development

    // GitHub Packages repository
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/billionaire-devs/partner-insurers-shared-kernel")
        credentials {
            username = System.getenv("GPR_USER")
                ?: System.getenv("GITHUB_ACTOR")
                ?: project.findProperty("gpr.user") as String?
            password = System.getenv("GPR_KEY")
                ?: System.getenv("GITHUB_TOKEN")
                ?: project.findProperty("gpr.key") as String?
        }
    }

}

dependencies {
    implementation("com.bamboo.assur.partner-insurers:shared-kernel:1.1.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webclient")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    // https://mvnrepository.com/artifact/org.postgresql/r2dbc-postgresql
    implementation("org.postgresql:r2dbc-postgresql:1.1.0.RELEASE")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1-0.6.x-compat")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework:spring-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.4"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.testcontainers:db2")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
