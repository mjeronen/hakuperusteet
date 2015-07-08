# hakuperusteet

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
