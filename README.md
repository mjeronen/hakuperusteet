# hakuperusteet

## Configuration

As default, the application runs in the port `8080`. This can be changed by
setting the environment variable `PORT`.

## Standalone JAR building

To create assembly jars (app and admin), run the following commands

1. `npm install`
2. `./sbt assembly`
3. `./sbt admin:assembly`

## Auto compile frontend while developing
1. `npm install`
2. `npm run watch`

## Local Postgres setup

MacOS users install docker with command `brew cask install dockertoolbox`. 

1. Create new docker-machine `docker-machine create —-driver virtualbox dockerVM`
2. `eval "$(docker-machine env dockerVM)"`
3. Check DOCKER_HOST variable
4. Edit /etc/hosts. Add line `<docker-host-ip-goes-here> hakuperusteetdb`
5. `docker run -p -d 5432:5432 postgres`
6. `psql -hhakuperusteetdb -p5432 -Upostgres postgres -c "CREATE ROLE OPH;"`
7. `psql -hhakuperusteetdb -p5432 -Upostgres postgres -c "CREATE DATABASE hakuperusteet;"`
8. `psql -hhakuperusteetdb -p5432 -Upostgres postgres -c "CREATE DATABASE hakuperusteettest;"`

To start docker again (e.g. after boot), run the following command and continue from step 2 above. 

1. `docker-machine start dockerVM`

## Create slick-classes

During development, after schema changes you must regenerate db-classes with command:

`./sbt "run-main fi.vm.sade.hakuperusteet.db.CodeGenerator"`

Currently we store generated code in git, and hence it is not necessary to run this normally.

## Run using mock configuration

`./sbt run -J-Dmock=true` starts server using mock configuration

## Start mock server

1. `cd mockserver`
2. `npm install`
3. `node server.js`

or use nodemon to auto reload on changes

1. `npm install -g nodemon`
2. `nodemon server.js`
