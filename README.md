# hakuperusteet

## Configuration

As default, the application runs in the port `8080`. This can be changed by
setting the environment variable `PORT`.

## Standalone JAR

`sbt assembly` builds a standalone jar.

## Test API

The application serves a simple test form in `/test_form.html`. The form can
be used to call the `/api/v1/test` endpoint to create a redirect to a user
given URL with signed parameters. See the specification of this redirect in
SPEC.md.

The test API uses a RSA keypair to sign the parameters. These keys are
included in the repository in the `testkey.pub.pem` and
`src/main/resources/testkey.pem` files. New keypair can be generated using
openssl as follows.

1. Generate the keypair: `openssl genrsa <number of bits> > key.pem`
2. Create a copy of the keypair in PKCS8 format for easy use from Java:  
   `cat key.pem | openssl pkcs8 -topk8 -inform PEM -outform DER -nocrypt > key.der`
3. Extract the public key from the keypair in PEM and DER formats:  
   `cat key.pem | openssl rsa -pubout -inform PEM -outform PEM > key.pub.pem`  
   `cat key.pem | openssl rsa -pubout -inform PEM -outform DER > key.pub.der`

## Local Postgres setup

MacOS users install docker with command `rew cask install dockertoolbox`. 

1. Create new docker-machine `docker-machine create —-driver virtualbox dockerV`
2. Eval "$(docker-machine env dockerVM)"`
3. Check DOCKER_HOST variable
4. Edit /etc/hosts. Add line `<docker-host-ip-goes-here> hakuperusteetdb`
5. `docker run -p 5432:5432 postgres`
6. `psql -hhakuperusteetdb -p5432 -Upostgres postgres -c "CREATE DATABASE hakuperusteet;"`
7. `psql -hhakuperusteetdb -p5432 -Upostgres postgres -c "CREATE DATABASE hakuperusteettest;"`
8. Create Slick-db-classes `./sbt "run-main fi.vm.sade.hakuperusteet.db.CodeGenerator"` 