package org.redhat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;

@ApplicationScoped
public class MigrationService {
    @Inject
    Flyway flywayMigrator;

    public void checkMigration() {
        flywayMigrator.clean();
        flywayMigrator.baseline();
        flywayMigrator.migrate();
        System.out.println("Migrated to Database version: " + flywayMigrator.info().current().getVersion().toString());
    }
}