package io.zino.mystore.storageEngine.fileStorageEngine;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.storageEngine.StorageEntry;
import io.zino.mystore.storageEngine.memoryStorageEngine.MemoryStorageEngine;

// TODO: Auto-generated Javadoc
/**
 * The Class FileStorageEngineOptimizer.
 */
final public class FileStorageEngineOptimizer {

	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(FileStorageEngineOptimizer.class);

	/** The instance. */
	private static FileStorageEngineOptimizer instance = new FileStorageEngineOptimizer();

	/**
	 * Gets the single instance of FileStorageEngineOptimizer.
	 *
	 * @return single instance of FileStorageEngineOptimizer
	 */
	public static FileStorageEngineOptimizer getInstance() {
		return instance;
	}

	/** The file storage engine. */
	private FileStorageEngine fileStorageEngine;

	/**
	 * Instantiates a new file storage engine optimizer.
	 */
	private FileStorageEngineOptimizer() {
		this.fileStorageEngine = FileStorageEngine.getInstance();

		new Thread(() -> evalForUpgrade()).start();

		System.out.println("FileStorageEngineOptimizer Started! " + System.currentTimeMillis());
	}

	/**
	 * Eval for upgrade.
	 */
	private void evalForUpgrade() {
		long sleepDuration = 10000;
		long MAX_SLEEP_DURATION = 15 * 60 * 1000;
		double agingThreshold = Double
				.parseDouble(ConfigMgr.getInstance().get("FileStorageEngine.upgradeAgingThreshold"));
		while (true) {
			try {
				Thread.sleep(sleepDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean decSleppDuration = false;

			List<Long> upgradeList = new ArrayList<>();
			for (Long indexEntry : this.fileStorageEngine.indexFileEngine.getIndexEntries()) {
				StorageEntry entry = this.fileStorageEngine.dbFileEngine.loadEntry(indexEntry);
				long idealTime = System.currentTimeMillis() - entry.getLastAccess();
				double aging = entry.getTouchCount() / (idealTime + 1);
				if (aging >= agingThreshold) {
					upgradeList.add(indexEntry);
				}
			}

			for (Long indexEntry : upgradeList) {
				StorageEntry entry = this.fileStorageEngine.dbFileEngine.loadEntry(indexEntry);
				this.upgrade(entry);
				this.fileStorageEngine.delete(entry);
				
				decSleppDuration = true;
			}
			
			sleepDuration = (long) (decSleppDuration ? sleepDuration * (0.8) : sleepDuration * (1.2));
			sleepDuration = (MAX_SLEEP_DURATION < sleepDuration) ? MAX_SLEEP_DURATION : sleepDuration;
		}
	}

	/**
	 * Upgrade.
	 *
	 * @param storageEntry
	 *            the storage entry
	 */
	private void upgrade(StorageEntry storageEntry) {
		StorageEntry insert = MemoryStorageEngine.getInstance().insert(storageEntry);
		if (insert == null) {
			logger.error("Insert faild in upgradeing: " + storageEntry.toString());
		}else{
			logger.debug("Entry upgradeing to Memory: "+storageEntry.toString());
		}
	}

}