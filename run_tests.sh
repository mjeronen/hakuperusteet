#!/usr/bin/env bash

set -e

function finish {
  if [ -n "$PID" ]; then
  echo "kill ui $PID";
    kill -9 $PID;
  fi
  if [ -n "$ADMIN" ]; then
    echo "kill admin";
    kill -9 $ADMIN;
  fi
}

trap finish EXIT

./sbt test

./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer" &
PID=$!
while ! nc -z localhost 8081; do
  sleep 1
done
npm run test-ui

./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetAdminTestServer" &
while ! nc -z localhost 8091; do
  sleep 1
done
ADMIN=$!
npm run test-admin-ui
