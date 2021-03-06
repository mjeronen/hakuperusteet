# hakuperusteet

## Setup

### Requirements
* JDK 1.8
* node

### Local Postgres setup

MacOS users install docker with command `brew cask install dockertoolbox`.

1. Create new docker-machine `docker-machine create --driver virtualbox dockerVM`
2. Run `docker-machine env dockerVM` and check DOCKER_HOST variable
4. Edit /etc/hosts. Add line `<docker-host-ip-goes-here> hakuperusteetdb`
5. `docker run -d -p 5432:5432 postgres`
6. `psql -hhakuperusteetdb -p5432 -Upostgres postgres -c "CREATE ROLE OPH;"`
7. `psql -hhakuperusteetdb -p5432 -Upostgres postgres -c "CREATE DATABASE hakuperusteet;"`
8. `psql -hhakuperusteetdb -p5432 -Upostgres postgres -c "CREATE DATABASE hakuperusteettest;"`

To start docker again (e.g. after boot), run the following command and continue from step 2 above.

1. `docker-machine start dockerVM`

### Run

To start hakuperusteet after Postgres is up, run the following commands:

1. `npm install`
2. `./sbt run`
3. Access hakuperusteet at [https://localhost:18080/hakuperusteet/](https://localhost:18080/hakuperusteet/)
4. `npm run watch` on separate console to enable front auto compile

By default hakuperusteet uses services from Luokka-environment.

### Run using mock configuration

Mock configuration does not have any external dependencies (except Google Authentication, but email one can be used).
This setup needs a running mock server, which should be started with following commands first:

1. `cd mockserver`
2. `npm install`
3. `node server.js`

or use nodemon to auto reload on changes

1. `npm install -g nodemon`
2. `nodemon server.js`

Mock configuration is enabled when running the following command:

1. `./sbt run -J-Dmock=true`

## Configuration

This project has multiple configuration files, which are used for following purposes.

### src/main/resources/reference.conf

 - Development time configuration file, which uses luokka-environment

### src/main/resources/mockReference.conf

 - Development time configuration file, which uses mock server (see below).

### src/main/resources/oph-configuration/hakuperusteet.properties.template

 - This file is the configuration template used with real environments.

### src/test/resources/reference.conf

 - This file is used during unit and UI-tests, uses mock server and Postgres. Both mock server and Posgres has different ports
   than in reference.conf above. Unit tests do not use mock server, hence their port numbers are irrelevant.

### src/test/resources/hsqlReference.conf

 - This optional config files is used to enable in-memory HSQLDB. Used with UI-tests, because Bamboo does not have Postgres.

## Build

To create assembly jars (app and admin), run the following commands

1. `npm install`
2. `./sbt assembly`
3. `./sbt admin:assembly`

## Postgres client classes for Slick

Currently we store generated Postgres-client classes in git, and hence it is not necessary to run this normally.
During development, after schema changes you must regenerate db-classes with command:

`./sbt "run-main fi.vm.sade.hakuperusteet.db.CodeGenerator"`

Remember to create separate schema migration script for HSQLDB also, and ensure that UI-tests continue to work.

## UI-tests

1. `./sbt "test:run-main fi.vm.sade.hakuperusteet.db.CodeGeneratorTest"`
2. `./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer" -J-DuseHsql=true`
3. `./node_modules/.bin/mocha-phantomjs --ignore-resource-errors --ssl-protocol=any --ignore-ssl-errors=true  https://localhost:18081/hakuperusteet/spec/testRunner.html`

To run tests in browser, open following url after steps 1. and 2. above.

1. http://localhost:8081/hakuperusteet/spec/testRunner.html

Remember to remove HSQLD-client classes after tests, because their existence breaks unit tests

1. `rm -r src/test/scala/fi/vm/sade/hakuperusteet/db/generated/`
