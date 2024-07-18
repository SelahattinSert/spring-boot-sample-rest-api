# Camera Onboarding REST API

## Description
Spring Boot which project provides a REST API to handle the camera on-boarding process for a surveillance system inside a fridge

## Requirements
Java Dependency: 21
Maven

## Technologies Used
- Spring Boot
- Maven
- JUnit 5
- Mockito
- Log4j2
- PostgreSQL
- Liquibase

## Installation Instructions
clone the project:
```sh
git clone https://github.com/SelahattinSert/demo.git
cd demo
```

build the project with maven:
```sh
mvn clean install
```

run the project with maven:
```sh
mvn spring-boot:run
```

## Database Configuration

Install PostgreSQL from https://www.postgresql.org/download/

create a new database named CameraDB:
```sh
createdb CameraDB
```

Ensure your 'application.properties' file is configured

Ensure you have the necessary dependencies in 'pom.xml' file

Ensure you have the 'changelog-master.xml' file in the 'src/main/resources/db/changelog/' directory with Liquibase changesets 
