plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":libs:common-domain"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // 관리자 UI를 위한 Thymeleaf 등을 추가할 수 있습니다.
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // 1. DB 연동을 위해 JPA 스타터 추가
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 2. postresql 연동을 위해 추가
    implementation("org.postgresql:postgresql")

    // 3. Redis 연동을 위해 추가
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("org.postgresql:postgresql")
}