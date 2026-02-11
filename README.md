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
- Argo CD (GitOps)

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

### API Documentation

The Swagger API documentation for the Camera Onboarding project can be accessed at:

Local Environment: http://localhost:8080/swagger-ui.html

## GitOps Deployment Instructions (New Workflow)

We have adopted a GitOps workflow using **Argo CD**. Git is now the single source of truth for the Kubernetes cluster state.

**Do not use `kubectl apply` for deployments.** All changes should be made via Git commits to the `main` branch.

### Prerequisites

1. Kubernetes cluster.
2. Argo CD installed in the cluster.

### Setup Argo CD

1. **Install Argo CD** (if not already installed):
   ```sh
   kubectl create namespace argocd
   kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
   ```

2. **Access Argo CD UI:**
   ```sh
   kubectl port-forward svc/argocd-server -n argocd 8080:443
   # Open https://localhost:8080
   # Username: admin
   # Password: Get it via: kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
   ```

3. **Bootstrap the Application:**
   Run this command **once** to set up the "App of Apps" which manages all other components:
   ```sh
   kubectl apply -f k8s/argo-cd/root-app.yaml
   ```

### How to Deploy Changes

1. Modify the Kubernetes manifests in the `k8s/` directory.
2. Commit and push your changes to the `main` branch.
3. Argo CD will automatically detect the changes and sync the cluster state (within 3 minutes by default).

---

## (Deprecated) Manual Deployment Instructions

*These instructions are kept for reference but should not be used for standard deployments.*

### Prerequisites

-Kubernetes cluster with kubectl configured
-Docker image is already built and pushed to Azure Container Registry (ACR)
-Secret values are defined in .env file

### 1-Create Secret

Create a secret using your .env file:

   ```sh
    kubectl create secret generic camera-onboarding-secrets \
    --from-env-file=camera.env \
    -n default
```

### 2-Deploy Resources

   ```sh
    kustomize build k8s/ | kubectl apply -f -
```

### Check Deployment

To verify that the app is running:

```sh
kubectl get pods
kubectl get svc
```
