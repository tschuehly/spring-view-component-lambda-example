import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    //id("org.graalvm.buildtools.native") version "0.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "de.tschuehly"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
}
//graalvmNative {
//    metadataRepository {
//        enabled.set(true)
//        version.set("0.3.2")
//        moduleToConfigVersion.put("org.thymeleaf:thymeleaf-spring6", "3.1.0.M2")
//    }
//}
extra["springCloudVersion"] = "2022.0.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("de.tschuehly:spring-view-component-thymeleaf:0.6.1-SNAPSHOT")

    api("org.springframework.cloud:spring-cloud-function-web")
    api("org.springframework.cloud:spring-cloud-function-adapter-aws")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("com.amazonaws:aws-lambda-java-events:3.11.2")
    compileOnly("com.amazonaws:aws-lambda-java-core:1.2.2")

    implementation("org.springframework.cloud:spring-cloud-function-kotlin:3.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets {
    main {
        resources {
            srcDir("src/main/kotlin")
            exclude("**/*.kt")
        }
    }
}


tasks.assemble {
    dependsOn("shadowJar")
}

tasks.jar{
    manifest{
        attributes["Main-Class"] = "de.tschuehly.viewcomponentlambda.ViewComponentLambdaApplicationKt"
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("awsLamdaSample.jar")
    dependencies {
        exclude("org.springframework.cloud:spring-cloud-function-web")
    }
    // Required for Spring
    mergeServiceFiles()
    append("META-INF/spring.handlers")
    append("META-INF/spring.schemas")
    append("META-INF/spring.tooling")
    transform(PropertiesFileTransformer::class.java) {
        paths.add("META-INF/spring.factories")
        paths.add("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")
        mergeStrategy = "append"
    }
}
