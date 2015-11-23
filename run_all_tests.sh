#!/usr/bin/env bash

set -e

function finish {
  if [ -n "$PID" ]; then
    kill -SIGTERM $PID;
  fi
  echo "********************* Finished run_all_tests.sh"
}

trap finish EXIT

echo "********************* Running all hakuperuste tests"

echo "********************* npm install for hakuperusteet"
npm install

echo "********************* npm install for hakuperusteet mockserver"
(cd mockserver && npm install && pwd)

echo "********************* ./sbt test"

./sbt clean compile admin:compile test -J-Dembedded=true

echo "********************* Starting test servers"
./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer" -J-Dembedded=true &
PID=$!
while ! nc -z localhost 8081; do
  sleep 1
done

echo "********************* npm run test-ui"
npm run test-ui

echo "********************* npm run admin:test-ui"
npm run admin:test-ui

echo "********************* ALL TESTS OK!"