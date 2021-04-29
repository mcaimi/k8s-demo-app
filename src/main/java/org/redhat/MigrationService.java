package org.redhat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MigrationService {
    @Inject
    Flyway flywayMigrator;

    @Inject
    Flyway flywayDefault;

    private static final Logger LOGGER = Logger.getLogger(MigrationService.class.getName());

    public void checkMigration() {
        flywayMigrator.clean();
        flywayMigrator.baseline();
        flywayMigrator.migrate();
        LOGGER.info("Migrated to Database version: " + flywayMigrator.info().current().getVersion().toString());
    }
}
