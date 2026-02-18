plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // 다른 모듈 연결
    implementation(project(":libs:common-domain"))
    implementation(project(":libs:common-utils"))

    // 스프링 부트 웹 설정
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // 1. DB 연동을 위해 JPA 스타터 추가
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 2. postresql 연동을 위해 추가
    implementation("org.postgresql:postgresql")

    // 3. Redis 연동을 위해 추가
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}