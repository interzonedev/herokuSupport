package com.interzonedev.herokusupport.data.migration.operation;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interzonedev.herokusupport.data.migration.MigrationOperationException;
import com.interzonedev.herokusupport.data.migration.MigrationService;
import com.interzonedev.herokusupport.data.migration.result.MigrationResult;

@Named("statusOperation")
public class StatusOperation implements MigrationOperation {
    private Logger log = (Logger) LoggerFactory.getLogger(getClass());

    @Override
    public MigrationResult doOperation(MigrationService migrationService) throws MigrationOperationException {

        log.info("doOperation: start");

        MigrationResult migrationResult = migrationService.getStatus();

        log.debug("doOperation: migrationResult = " + migrationResult);

        log.info("doOperation: end");

        return migrationResult;

    }

}
