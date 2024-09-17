# Camera Onboarding REST API

## Description

Spring Boot which project provides a REST API to handle the camera on-boarding process for a surveillance system inside
a fridge

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
- Azure Blob Storage

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

Ensure you have the 'changelog-master.xml' file in the 'src/main/resources/db/changelog/' directory with Liquibase
changesets

## Azure Blob Storage Configuration

This project integrates with Azure Blob Storage for handling image uploads related to the camera onboarding process

### Prerequisites:

1. You need an Azure account. If you don't have one, create it at [Azure](https://azure.microsoft.com/en-us/free/)
2. Set up an Azure Storage Account and create a Blob Container. For more
   information, [Azure Blob Storage documentation](https://docs.microsoft.com/en-us/azure/storage/blobs/)

### Azure Blob Storage Setup

1. Create a Storage Account in the Azure Portal or via Azure CLI:
   ```sh
   az storage account create --name <your-storage-account-name> --resource-group <your-resource-group> --location <your-location> --sku Standard_LRS

2. Create a Blob Container inside the Storage Account:
   ```sh
   az storage container create --name <your-container-name> --account-name <your-storage-account-name>

3. Configure the following properties in your application.properties file:
   ```sh
   azure.storage.account-name=<your-storage-account-name>
   azure.storage.container-name=<your-container-name>
   azure.storage.connection-string=<your-storage-connection-string>

Ensure that related dependencies are added to your pom.xml file