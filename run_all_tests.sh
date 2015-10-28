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

./sbt test -J-Dembedded=true

./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer" -J-Dembedded=true &
PID=$!
while ! nc -z localhost 8081; do
  sleep 1
done
npm run test-ui

./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetAdminTestServer" -J-Dembedded=true &
while ! nc -z localhost 8091; do
  sleep 1
done
ADMIN=$!
npm run test-admin-ui
