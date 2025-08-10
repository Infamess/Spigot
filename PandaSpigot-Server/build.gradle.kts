plugins {
    id("pandaspigot.conventions")
    id("com.gradleup.shadow") version "9.0.0-beta7"
}

val minecraftVersion = "1_8_R3"

repositories {
    maven(url = "https://libraries.minecraft.net")
}

dependencies {
    implementation(project(":pandaspigot-api"))

    // PandaSpigot start - Configuration
    implementation("com.hpfxd.configurate:configurate-eo-yaml:1.0.0") {
        exclude(group = "org.checkerframework", module = "checker-qual")
    }
    // PandaSpigot end

    // Minecraft libraries:
    implementation("io.netty:netty-all:4.1.121.Final") // PandaSpigot - Update Netty to 4.1.x
    implementation("com.mojang:authlib:1.5.21")
    // PandaSpigot start - Update log4j
    implementation("org.apache.logging.log4j:log4j-api:2.24.3")
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.24.3") // PandaSpigot - Add SLF4J logger
    implementation("org.apache.logging.log4j:log4j-iostreams:2.24.3") // PandaSpigot - Add Log4j IOStreams
    // PandaSpigot end
    implementation("org.apache.commons:commons-lang3:3.3.2")
    implementation("commons-io:commons-io:2.4")
    implementation("commons-codec:commons-codec:1.9")
    implementation("com.google.guava:guava:17.0")
    implementation("com.google.code.gson:gson:2.2.4")
    implementation("net.sf.trove4j:trove4j:3.0.3")
    implementation("net.sf.jopt-simple:jopt-simple:3.2")

    implementation("org.xerial:sqlite-jdbc:3.7.2")
    implementation("mysql:mysql-connector-java:5.1.14")

    // PandaSpigot start - Use TerminalConsoleAppender
    implementation("net.minecrell:terminalconsoleappender:1.3.0")
    implementation("org.jline:jline-terminal-jni:3.30.3")
    // PandaSpigot end

    implementation("net.kyori:adventure-key:4.21.0") // PandaSpigot - Add Channel initialization listeners

    testImplementation("junit:junit:4.11")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
}

fun TaskContainer.registerRunTask(
    name: String, block: JavaExec.() -> Unit
): TaskProvider<JavaExec> = register<JavaExec>(name) {
    group = "pandaspigot"
    standardInput = System.`in`
    workingDir = rootProject.layout.projectDirectory.dir(
        providers.gradleProperty("runWorkDir").orElse("run")
    ).get().asFile

    doFirst {
        workingDir.mkdirs()
    }
    block(this)
}

tasks {
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("unmapped")
        append("META-INF/io.netty.versions.properties")
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer()) // PandaSpigot - Use TerminalConsoleAppender
        // PandaSpigot start - Configuration
        arrayOf(
            "com.amihaiemil.eoyaml",
            "org.spongepowered.configurate",
            "io.leangen.geantyref",
        ).forEach { relocate(it, "com.hpfxd.pandaspigot.libs.$it") }
        // PandaSpigot end

        val cbLibsPkg = "org.bukkit.craftbukkit.libs"

        relocate("joptsimple", "$cbLibsPkg.joptsimple")
        relocate("org.ibex", "$cbLibsPkg.org.ibex")
        relocate("org.gjt", "$cbLibsPkg.org.gjt")

        relocate("org.bukkit.craftbukkit", "org.bukkit.craftbukkit.v${minecraftVersion}") {
            exclude("org.bukkit.craftbukkit.Main*") // don't relocate main class
        }
        relocate("net.minecraft.server", "net.minecraft.server.v${minecraftVersion}")
    }

    named("build") {
        dependsOn(named("shadowJar"))
    }

    test {
        exclude("org/bukkit/craftbukkit/inventory/ItemStack*Test.class", "org/bukkit/craftbukkit/inventory/ItemFactoryTest.class")
    }

    jar {
        archiveClassifier.set("original")
        manifest {
            val git = Git(rootProject.layout.projectDirectory)
            val gitHash = git("rev-parse", "--short=7", "HEAD").getText().trim()
            val implementationVersion = System.getenv("GITHUB_RUN_NUMBER") ?: "\"$gitHash\""
            val date = git("show", "-s", "--format=%ci", gitHash).getText().trim()
            val gitBranch = git("rev-parse", "--abbrev-ref", "HEAD").getText().trim()
            attributes(
                "Main-Class" to "org.bukkit.craftbukkit.Main",
                "Implementation-Title" to "CraftBukkit",
                "Implementation-Version" to "git-PandaSpigot-$implementationVersion",
                "Implementation-Vendor" to date,
                "Specification-Title" to "Bukkit",
                "Specification-Version" to project.version,
                "Specification-Vendor" to "Bukkit Team",
                "Multi-Release" to "true", // PandaSpigot
                "Git-Branch" to gitBranch,
                "Git-Commit" to gitHash,
            )
        }
    }

    registerRunTask("runShadow") {
        description = "Spin up a test server from the shadowJar archiveFile"
        classpath(shadowJar.flatMap { it.archiveFile })
    }

    registerRunTask("runDev") {
        description = "Spin up a non-shaded non-relocated test server"
        classpath = java.sourceSets.main.get().runtimeClasspath
        mainClass.set("org.bukkit.craftbukkit.Main")
    }

    register<RemapTask>("remap") {
        description = "Remap the output JAR file using the deprecation mappings"

        val inTask = shadowJar.get()
        inJarFile.set(inTask.archiveFile)
        outJarFile.set(inTask.destinationDirectory.map { dir ->
            val archiveName = ArchiveName.archiveNameFromTask(inTask).copy(classifier = "")
            dir.file(archiveName.toFileName())
        })

        mappingFile.set(project.layout.projectDirectory.file("deprecation-mappings.csrg"))
        accessTransformerFile.set(project.layout.projectDirectory.file("deprecation-mappings.at"))
    }

    assemble {
        dependsOn(named("remap"))
    }
}
