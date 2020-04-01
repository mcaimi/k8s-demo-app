#!/bin/bash
export DBKIND=postgresql
export DBHOST=localhost
export DBPORT=5432
export DBNAME=hibernate_db

./mvnw clean test

unset DBKIND
unset DBHOST
unset DBPORT
unset DBNAME

