#!/bin/bash
export DBKIND=postgresql
export DBHOST=localhost
export DBPORT=5432
export DBNAME=hibernate_db
export DBUSER=hibernate
export DBPASS=hibernate
export DEFAULT_SCHEMA="dev_schema"
export HIBERNATE_LOAD_FILE="no-file"
export DB_GENERATION="drop-and-create"
export ORM_LOG=true
export JDBC_MAX_SIZE=8
export JDBC_MIN_SIZE=2
export FLYWAY_MIGRATE_AT_START=true
export FLYWAY_BASELINE_VERSION="1.0.0"
export FLYWAY_BASELINE_DESCRIPTION="Initial"
export FLYWAY_CONNECTION_RETRIES=100
export FLYWAY_MIGRATION_TABLE="dev_history"
export FLYWAY_MIGRATIONS_LOCATION="db/migrations/dev"
export FLYWAY_BASELINE_ON_MIGRATE=true


./mvnw clean install -DskipTests=true
./mvnw clean package -DskipTests=true

unset DBKIND
unset DBHOST
unset DBPORT
unset DBNAME
unset DEFAULT_SCHEMA
unset HIBERNATE_LOAD_FILE
unset DB_GENERATION
unset ORM_LOG
unset JDBC_MAX_SIZE
unset JDBC_MIN_SIZE
unset FLYWAY_MIGRATE_AT_START
unset FLYWAY_BASELINE_VERSION
unset FLYWAY_BASELINE_DESCRIPTION
unset FLYWAY_CONNECTION_RETRIES
unset FLYWAY_MIGRATION_TABLE
unset FLYWAY_MIGRATIONS_LOCATION
unset FLYWAY_BASELINE_ON_MIGRATE

