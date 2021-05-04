# AzureKeyVaultSpringBoot
Sample Spring Boot Application gets secret from Azure key vault

## Run app using Docker  Images
Create docker image based on jar
docker build -t springio/spring-boot-docker --file Dockerfile-openjdk2 . 

Run the container 
docker run -p 8082:8080 -t springio/spring-boot-docker


## Tutorial source
https://docs.microsoft.com/en-us/azure/developer/java/spring-framework/configure-spring-boot-starter-java-app-with-azure-key-vault

## Create a service principal

Azure AD service principals provide access to Azure resources within your subscription

az ad sp create-for-rbac --name arocks-app-sp

The output will looks like <br/>
{ <br/>
  "appId": "appId-VALUE",  <br/>
  "displayName": "SP_NAME",  <br/>
  "name": "http://SP_NAME",  <br/>
  "password": "PASSWORD-VALUE", <br/>
  "tenant": "TENANT_VALUE" <br/>
}

Save appId,  name, password, tenant values


## Create the Key Vault instance

### Get supported regions for the account
az account list-locations --output table

### create Resource Group
az group create --location eastus --name keyvault-rg <br/>
RG=keyvault-rg <br/>
KVNAME=arockssprngbootkv <br/>

### Create a new Key Vault in the resource group.
az keyvault create \
    --resource-group $RG \
    --name $KVNAME \
    --enabled-for-deployment true \
    --enabled-for-disk-encryption false \
    --enabled-for-template-deployment true \
    --location eastus \
    --query properties.vaultUri \
    --sku standard

The genreated Key vault URL is "https://arockssprngbootkv.vault.azure.net/"

### Configure key vault to allow get and list operatoins only - for the SP created before
SPNAME=http://arocks-app-sp
az keyvault set-policy --name $KVNAME --spn $SPNAME --secret-permissions get list

### list down existing secrets
az keyvault secret list --vault-name $KVNAME

### Store the secret - Here JDBC Connection string
az keyvault secret set --name "connectionString" \
    --vault-name "$KVNAME" \
    --value "jdbc:sqlserver://SERVER.database.windows.net:1433;database=DATABASE;"
    
    
## Build and Run Java Application

Udpate src/main/resources/application.properties file with the following settings

azure.keyvault.client-id=appId-from-service-principal-creation-output <br/>
azure.keyvault.client-key=password-from-service-principal-creation-output <br/>
azure.keyvault.enabled=true <br/>
azure.keyvault.tenant-id=Tenent-from-service-principal-creation-output <br/>
azure.keyvault.uri=keyvault-URL <br/>


mvn spring-boot:run <br/>
mvn clean package spring-boot:run <br/>
curl http://localhost:8080/get <br/>
