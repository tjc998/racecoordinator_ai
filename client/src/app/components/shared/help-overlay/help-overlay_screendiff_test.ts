import { test, expect, Page } from '@playwright/test';
import { TestSetupHelper } from '../../../testing/test-setup_helper';
import { HelpOverlayHarnessE2e } from './testing/help-overlay.harness.e2e';

test.describe('Help Overlay Visuals', () => {
  test.beforeEach(async ({ page }) => {
    // Setup standard mocks including races and drivers so the main page loads populated
    await TestSetupHelper.setupStandardMocks(page);
    await TestSetupHelper.disableAnimations(page);
    await TestSetupHelper.setupRaceMocks(page);

    // Ensure we don't auto-trigger help from "first run" logic by presetting settings
    await TestSetupHelper.setupLocalStorage(page, { 
      racedaySetupWalkthroughSeen: true,
      shareAnalytics: true // Ensure analytics button is in a known state (enabled)
    });

    // Skip splash screen
    await TestSetupHelper.setupSessionStorage(page, { skipIntro: 'true' });
  });

  async function setupHelp(page: Page) {
    // 1. Load Page
    await TestSetupHelper.waitForLocalization(page, 'en', page.goto('/'));

    // Wait for main content to be visible
    await expect(page.locator('.logo-text')).toBeVisible();

    // 2. Click Help Icon
    const helpIcon = page.locator('.toolbar-btn.help');
    await expect(helpIcon).toBeVisible();
    await helpIcon.click();
    await page.mouse.move(0, 0); // Clear hover states
    await page.evaluate(() => (document.activeElement as HTMLElement)?.blur()); // Clear focus rings

    // Wait for overlay to appear
    const overlay = page.locator('app-help-overlay');
    const harness = new HelpOverlayHarnessE2e(overlay, page);
    await harness.waitForStable();
    return harness;
  }

  test('should show help step 1: Welcome', async ({ page }) => {
    const harness = await setupHelp(page);

    // Verify Step 1 (Welcome - general modal, centered)
    await expect(async () => {
      expect(await harness.getContent()).toContain('configure and start your races');
    }).toPass({ timeout: 10000 });

    // Capture Step 1
    await expect(async () => {
      expect(await harness.getStepCounter()).toContain('1');
    }).toPass();
    await page.waitForTimeout(200); // Allow focus/render to settle
    await expect(page).toHaveScreenshot('help-step-1-welcome.png', { maxDiffPixels: 1000 });
  });

  test('should show help step 2: Walkthrough Icon', async ({ page }) => {
    const harness = await setupHelp(page);

    // Move to Step 2
    await harness.clickNext();
    await page.mouse.move(0, 0); // Clear hover states
    await page.evaluate(() => (document.activeElement as HTMLElement)?.blur()); // Clear focus rings

    // Verify Step 2 (Walkthrough - targets help icon)
    await expect(async () => {
      expect(await harness.getStepCounter()).toContain('2');
      expect(await harness.getContent()).toContain('walkthrough');
    }).toPass();
    await harness.waitForStable();
    await expect(async () => {
      expect(await harness.hasHighlightMask()).toBe(true);
    }).toPass();

    // Capture Step 2
    await page.waitForTimeout(200); // Allow focus/render to settle
    await expect(page).toHaveScreenshot('help-step-2-icon-target.png', { maxDiffPixels: 1000 });
  });

  test('should show help step 3: Analytics', async ({ page }) => {
    const harness = await setupHelp(page);

    // Move to Step 3
    await harness.clickNext();
    await harness.clickNext();
    await page.mouse.move(0, 0); // Clear hover states
    await page.evaluate(() => (document.activeElement as HTMLElement)?.blur()); // Clear focus rings

    // Verify Step 3 (Analytics - targets analytics icon)
    await expect(async () => {
      expect(await harness.getStepCounter()).toContain('3');
      expect(await harness.getContent()).toContain('report usage data');
    }).toPass();
    await harness.waitForStable();

    // Capture Step 3
    await page.waitForTimeout(200); // Allow focus/render to settle
    await expect(page).toHaveScreenshot('help-step-3-analytics.png', { maxDiffPixels: 1000 });
  });

  test('should show help step 4: Driver Selection', async ({ page }) => {
    const harness = await setupHelp(page);

    // Move to Step 4
    await harness.clickNext();
    await harness.clickNext();
    await harness.clickNext();
    await page.mouse.move(0, 0); // Clear hover states
    await page.evaluate(() => (document.activeElement as HTMLElement)?.blur()); // Clear focus rings

    // Verify Step 4 (Driver Selection - targets driver panel)
    await expect(async () => {
      expect(await harness.getStepCounter()).toContain('4');
      expect(await harness.getContent()).toContain('select who will be racing');
    }).toPass();
    await harness.waitForStable();

    // Capture Step 4
    await page.waitForTimeout(200); // Allow focus/render to settle
    await expect(page).toHaveScreenshot('help-step-4-driver-panel.png', { maxDiffPixels: 1000 });
  });

  test('should navigate back to Analytics step correctly', async ({ page }) => {
    const harness = await setupHelp(page);

    // Move to Step 4, then back to 3
    await harness.clickNext();
    await harness.clickNext();
    await harness.clickNext();
    await harness.clickPrevious();
    await page.mouse.move(0, 0); // Clear hover states
    await page.evaluate(() => (document.activeElement as HTMLElement)?.blur()); // Clear focus rings

    // Should be back at Step 3 (Analytics)
    await expect(async () => {
      expect(await harness.getStepCounter()).toContain('3');
      expect(await harness.getContent()).toContain('report usage data');
    }).toPass();
    await harness.waitForStable();

    // Verify visual match
    await page.waitForTimeout(500); // Allow focus/render to settle
    await expect(page).toHaveScreenshot('help-step-3-analytics.png', { maxDiffPixels: 1000 });
  });

  test('should close the help guide when the close button is clicked', async ({ page }) => {
    const harness = await setupHelp(page);

    // Click Close (x) button in header
    await harness.clickClose();

    await expect(async () => {
      expect(await harness.isVisible()).toBe(false);
    }).toPass();
    await expect(async () => {
      expect(await harness.hasHighlightMask()).toBe(false);
    }).toPass();
  });
});
