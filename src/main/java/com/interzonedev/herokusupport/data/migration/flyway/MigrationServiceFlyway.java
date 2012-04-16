package com.interzonedev.herokusupport.data.migration.flyway;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.init.InitException;
import com.googlecode.flyway.core.metadatatable.MetaDataTableRow;
import com.interzonedev.herokusupport.data.migration.MigrationOperationException;
import com.interzonedev.herokusupport.data.migration.MigrationService;
import com.interzonedev.herokusupport.data.migration.result.MigrationHistory;
import com.interzonedev.herokusupport.data.migration.result.MigrationResult;
import com.interzonedev.herokusupport.data.migration.result.MigrationStatus;

public class MigrationServiceFlyway extends Flyway implements MigrationService {

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public MigrationStatus doInit() throws MigrationOperationException {
		log.debug("doInit: start");

		MigrationStatus status = MigrationStatus.INIT_SUCCEEDED;

		try {
			log.debug("doInit: init schema");
			init();
		} catch (InitException ie) {
			log.info("doInit: Schema already initialized");
		} catch (Throwable t) {
			log.error("doInit: Error migrating schema", t);
			status = MigrationStatus.INIT_FAILED;
		}

		log.debug("doInit: end - status = " + status);

		return status;
	}

	@Override
	public MigrationStatus doMigration() throws MigrationOperationException {
		log.debug("doMigration: start");

		MigrationStatus status = doInit();

		if (MigrationStatus.INIT_SUCCEEDED.equals(status)) {
			status = MigrationStatus.MIGRATION_FAILED;
			try {
				log.debug("doMigration: migrate schema");
				migrate();
				status = MigrationStatus.MIGRATION_SUCCEEDED;
			} catch (Throwable t) {
				log.error("doMigration: Error migrating schema", t);
			}
		}

		log.debug("doMigration: end - status = " + status);

		return status;
	}

	@Override
	public void doClean() throws MigrationOperationException {
		log.debug("doClean: start");

		try {
			clean();
		} catch (Throwable t) {
			log.error("doClean: Error cleaning schema", t);
		}

		log.debug("doClean: end");
	}

	@Override
	public MigrationResult getStatus() throws MigrationOperationException {
		log.debug("getStatus: start");

		MigrationResult result = null;

		try {
			MetaDataTableRow status = status();
			String statusToString = metaDataTableRowToString(status);
			result = new MigrationResult(statusToString);
		} catch (Throwable t) {
			log.error("getHistory: Error getting schema history", t);
		}

		return result;
	}

	@Override
	public List<MigrationHistory> getHistory() throws MigrationOperationException {
		log.debug("getHistory: start");

		List<MigrationHistory> wrappedHistory = new ArrayList<MigrationHistory>();

		try {
			List<MetaDataTableRow> history = history();
			for (MetaDataTableRow historyItem : history) {
				String historyToString = metaDataTableRowToString(historyItem);
				wrappedHistory.add(new MigrationHistory(historyToString));
			}
		} catch (Throwable t) {
			log.error("getHistory: Error getting schema history", t);
		}

		log.debug("getHistory: end");

		return wrappedHistory;
	}

	private String metaDataTableRowToString(MetaDataTableRow metaDataTableRow) {
		StringBuilder out = new StringBuilder();
		out.append(metaDataTableRow.getVersion()).append(" - ");
		out.append(metaDataTableRow.getDescription()).append(" - ");
		out.append(metaDataTableRow.getMigrationType()).append(" - ");
		out.append(metaDataTableRow.getState());
		return out.toString();
	}
}
