package com.antigravity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class AppTest {

  @Test
  public void testGetLocalIpAddress() {
    String ip = App.getLocalIpAddress();
    assertNotNull(ip);
    assertFalse(ip.isEmpty());
    // Verify it is either "Unknown" or matches IP pattern
    assertTrue(ip.equals("Unknown") || ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"));
  }

  @Test
  public void testBackupIncompatibleDatabase() throws IOException {
    // 1. Create a temp directory to simulate appDataDir
    Path tempDir = Files.createTempDirectory("rc_ai_test_dir");
    try {
      // 2. Create the incompatible mongodb_data folder inside it
      Path mongoDataPath = tempDir.resolve("mongodb_data");
      Files.createDirectories(mongoDataPath);

      // 3. Create dummy db files inside mongodb_data to verify they get backed up
      Path dummyFile = mongoDataPath.resolve("some_db_file.wt");
      Files.write(dummyFile, "dummy data".getBytes());

      // Verify files exist in mongodb_data
      assertTrue(Files.exists(mongoDataPath));
      assertTrue(Files.exists(dummyFile));

      // 4. Run the backupIncompatibleDatabase method
      String timestamp = "123456789";
      Path backupPath = App.backupIncompatibleDatabase(tempDir.toString(), timestamp);

      // 5. Verify results
      assertNotNull(backupPath);

      // Original folder should be moved (gone)
      assertFalse(Files.exists(mongoDataPath));

      // Backup folder should exist with the timestamp in its name
      assertTrue(Files.exists(backupPath));
      assertTrue(backupPath.getFileName().toString().contains(timestamp));

      // The dummy files should have been moved to the backup folder
      Path backedUpDummyFile = backupPath.resolve("some_db_file.wt");
      assertTrue(Files.exists(backedUpDummyFile));
      assertEquals("dummy data", new String(Files.readAllBytes(backedUpDummyFile)));

    } finally {
      // Cleanup files and folders
      Path backupPath = tempDir.resolve("mongodb_data_backup_4.4_123456789");
      if (Files.exists(backupPath.resolve("some_db_file.wt"))) {
        Files.delete(backupPath.resolve("some_db_file.wt"));
      }
      if (Files.exists(backupPath)) {
        Files.delete(backupPath);
      }
      Path mongoDataPath = tempDir.resolve("mongodb_data");
      if (Files.exists(mongoDataPath.resolve("some_db_file.wt"))) {
        Files.delete(mongoDataPath.resolve("some_db_file.wt"));
      }
      if (Files.exists(mongoDataPath)) {
        Files.delete(mongoDataPath);
      }
      Files.delete(tempDir);
    }
  }

  @Test
  public void testBackupIncompatibleDatabaseNotPresent() throws IOException {
    Path tempDir = Files.createTempDirectory("rc_ai_test_dir_empty");
    try {
      Path backupPath = App.backupIncompatibleDatabase(tempDir.toString(), "987654321");
      // Since mongodb_data does not exist, it should return null and make no backup folder
      assertNull(backupPath);
      assertFalse(Files.exists(tempDir.resolve("mongodb_data_backup_4.4_987654321")));
    } finally {
      Files.delete(tempDir);
    }
  }
}
