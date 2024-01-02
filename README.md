# acsp-api
A java repo for ACSP workstream, to build out IDV features for ACSP

# Eric setup
To understand how ERIC works, Please follow the confluence
https://companieshouse.atlassian.net/wiki/spaces/DEV/pages/3576889348/ERIC+for+Spring+Boot

For authorization, ERIC needs API_KEY. Ideally the CHS_API_KEY is configured in the environment setup. For local testing (not docker) of application, please set up the API_KEY in bash profile. 

Eg:
 

export CHS_API_KEY=0fec383d-6235-4a6f-9487-4857a8eb669b

export API_URL=http://api.chs.local:4001

export ERIC_API_URL=http://api.chs.local:4000

export ERIC_PORT=4001

export PAYMENTS_API_URL=http://api.chs.local:4001

export DOCUMENT_API_LOCAL_URL=http://document-api.devops1.aws.chdev.org

Note: Eric authorise API_KEY against accounts.ch.gov.uk. Therefore, for local testing,  the API Key can be anything in the account collection -> api_client_keys -> client id with _class = api_client/key.You may have to restart IntelliJ to pick up the variables.
