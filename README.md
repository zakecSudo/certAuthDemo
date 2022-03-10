# certAuthDemo
x.509 certificate authentication demo spring boot app

### All paswords are: 
    geslo123
### Link to tutorial: 
https://dzone.com/articles/securing-rest-apis-with-client-certificates

### Create folders to generate all files (separated for client and server)
    mkdir ssl && cd ssl && mkdir client && mkdir server && mkdir certificateAuthority

# Code

## Server
### Certificate authority (local)
    openssl req -x509 -sha256 -days 3650 -newkey rsa:4096 -keyout certificateAuthority/rootCA.key -out certificateAuthority/rootCA.crt
### Generate server private key and certificate signing request in one step
    openssl  req -new -newkey rsa:4096 -keyout server/serverPrivateKey.key -out server/request.csr
### Generate extra configuration for serve cert signing
    printf "authorityKeyIdentifier=keyid,issuer\nbasicConstraints=CA:FALSE\nsubjectAltName = @alt_names\n[alt_names]\nDNS.1 = localhost" >> certificateAuthority/configuration.ext
### Generate (sign) server certificate
    openssl x509 -req -CA certificateAuthority/rootCA.crt -CAkey certificateAuthority/rootCA.key -in server/request.csr -out server/server.crt -days 365 -CAcreateserial -extfile certificateAuthority/configuration.ext
### Package private key and certifikate in PKCS file
    openssl pkcs12 -export -out server/keyStore.p12 -name "localhost" -inkey server/serverPrivateKey.key -in server/server.crt
### Generate keyStore which get serve during SSL handshakee
    keytool -importkeystore -srckeystore server/keyStore.p12 -srcstoretype PKCS12 -destkeystore server/keyStore.jks -deststoretype JKS
### Generate trustStore that holds certificates of clients we trust
    keytool -import -trustcacerts -noprompt -alias ca -ext san=dns:localhost,ip:127.0.0.1 -file certificateAuthority/rootCA.crt -keystore server/truststore.jks
### Add root SI-TRUST certificat (for trusting certs from SI-TRUST) to trustStore
Link to SI_TRUST root cert: https://www.si-trust.gov.si/sl/podpora-uporabnikom/korenski-izdajatelj-si-trust-root/
    
    keytool -trustcacerts -keystore "server/truststore.jks" -storepass geslo123 -importcert -alias siroot -file "certificateAuthority/si-trust-root.cer"
### [OPTIONAL] list all certs in trustStore
    keytool -list -v -keystore server/truststore.jks
### [OPTIONAL] Delete cert by alias in trustStore
    keytool -delete -alias zak -keystore server/truststore.jks

# . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

## Client
### Generate client's private key and a certificate signing request (CSR)
    openssl req -new -newkey rsa:4096 -nodes -keyout client/clientBob.key -out client/request.csr
### Sign the request with our certificate authority
    openssl x509 -req -CA certificateAuthority/rootCA.crt -CAkey certificateAuthority/rootCA.key -in client/request.csr -out client/clientBob.crt -days 365 -CAcreateserial
### Package private key and certifikate in PKCS file
    openssl pkcs12 -export -out client/clientBob.p12 -name "clientBob" -inkey client/clientBob.key -in client/clientBob.crt
### [POSTMAN] Package private key and certifikate in PXM file
    openssl pkcs12 -export -out client/clientBob.pfx -name "clientBob" -inkey client/clientBob.key -in client/clientBob.crt

# . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

## aplication.properties
```
## Server certificate (HTTPS)

# The format used for the keystore
server.ssl.key-store-type=PKCS12
# The path to the keystore containing the certificate
server.ssl.key-store=src/main/resources/ssl/server/keyStore.p12
# The password used to generate the certificate
server.ssl.key-store-password=geslo123

## Client certificate authentication

# Trust store that holds SSL certificates.
server.ssl.trust-store=src/main/resources/ssl/server/truststore.jks
# Password used to access the trust store.
server.ssl.trust-store-password=geslo123
# Type of the trust store.
server.ssl.trust-store-type=JKS
# Whether client authentication is wanted ("want") or needed ("need").
server.ssl.client-auth=need
```
