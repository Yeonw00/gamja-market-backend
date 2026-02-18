plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":libs:common-domain"))
    implementation(project(":libs:common-utils"))

    // 1. JPA 의존성 추가 (Auditing 및 DB 접근을 위해 필수)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 2. PostgreSQL 드라이버 추가
    implementation("org.postgresql:postgresql")

    // 3. Redis 연동을 위해 추가
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 스프링 배치 의존성
    implementation("org.springframework.boot:spring-boot-starter-batch")
    testImplementation("org.springframework.batch:spring-batch-test")
}