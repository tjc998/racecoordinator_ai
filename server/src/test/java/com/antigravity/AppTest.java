package com.antigravity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
}
