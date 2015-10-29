#!/usr/bin/env bash

set -e

function finish {
  if [ -n "$PID" ]; then
    kill -9 $PID;
  fi
  if [ -n "$ADMIN" ]; then
    kill -9 $ADMIN;
  fi
}

trap finish EXIT

npm install
npm run build
npm run admin:build

echo "********************* ./sbt test"

./sbt clean compile admin:compile test -J-Dembedded=true

echo "********************* npm run test-ui"

./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer" -J-Dembedded=true -J-Dmock=true &
PID=$!
while ! nc -z localhost 8081; do
  sleep 1
done
npm run test-ui

echo "********************* npm run admin:test-ui"

./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetAdminTestServer" -J-Dembedded=true &
while ! nc -z localhost 8091; do
  sleep 1
done
ADMIN=$!
npm run admin:test-ui
