plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("dev.s7a.gradle.minecraft.server") version "3.2.1"

    id("com.gradleup.shadow") version "8.3.5"
}

group = "me.scarday"
version = "1.2"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.gitlab.ruany:LiteBansAPI:0.5.0")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:5.0.5")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:5.0.5")
}

bukkit {
    name = "LiteNotify"
    main = "dev.scarday.litenotify.Main"
    apiVersion = "1.16"

    softDepend = listOf("LiteBans")
}

//task<LaunchMinecraftServerTask>("launchMinecraftServer") {
//    dependsOn("shadowJar")
//
//    val path = buildDir.toPath().resolveSibling("server");
//
//    doFirst {
//        copy {
//            from(buildDir.resolve("libs/${project.name}-${project.version}-all.jar"))
//            into(buildDir.resolve("${path.toAbsolutePath()}/plugins"))
//        }
//    }
//
//    serverDirectory.set(path.toAbsolutePath().toString())
//    jvmArgument.set(listOf("-DPaper.IgnoreJavaVersion=true"))
//    jarUrl.set(LaunchMinecraftServerTask.JarUrl.Paper("1.16.5"))
//    agreeEula.set(true)
//}

// - Если вы хотите переписать под себя плагин, можете использовать тест сервер (код тот что сверху)