dependencies {
    // spring boot
    implementation("org.springframework.boot:spring-boot-starter")

    // feign client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.1.8")
    implementation("io.github.openfeign:feign-jackson:12.1")

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.5")

    // test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.mockk:mockk:1.12.4")

    // core module
    implementation(project(":grida-core"))
}

tasks.register("prepareKotlinBuildScriptModel"){}