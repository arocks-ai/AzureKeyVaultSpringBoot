# AzureKeyVaultSpringBoot

## Tutorial source
https://docs.microsoft.com/en-us/azure/developer/java/spring-framework/configure-spring-boot-starter-java-app-with-azure-key-vault

## Login to azure
az login
az account list
az account set -s ssssssss-ssss-ssss-ssss-ssssssssssss

## Create a service principal
Azure AD service principals provide access to Azure resources within your subscription

az ad sp create-for-rbac --name arocks-app-sp
Output will looks like 
{
  "appId": "appId-VALUE",
  "displayName": "SP_NAME",
  "name": "http://SP_NAME",
  "password": "PASSWORD-VALUE",
  "tenant": "TENANT_VALUE"
}
Save appId,  name, password, tenant values


# Create the Key Vault instance

### Get supported regions for the account
az account list-locations --output table

### create Resource Group
az group create --location eastus --name keyvault-rg
RG=keyvault-rg
KVNAME=arockssprngbootkv

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
    
    
# Run Java Application

## Add Key Vault integration to the app
Udpate src/main/resources/application.properties file with the following

azure.keyvault.client-id=appId-from-service-principal-creation-output
azure.keyvault.client-key=password-from-service-principal-creation-output
azure.keyvault.enabled=true
azure.keyvault.tenant-id=Tenent-from-service-principal-creation-output
azure.keyvault.uri=keyvault-URL


mvn spring-boot:run
mvn clean package spring-boot:run
curl http://localhost:8080/get
