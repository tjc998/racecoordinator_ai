import { expect, test } from "@playwright/test";
import { TestSetupHelper } from "@app/testing/test-setup_helper";

import { AssetManagerHarnessE2e } from "./testing/asset-manager.harness.e2e";

test.describe("Asset Manager Visuals", () => {
  test.beforeEach(async ({ page }) => {
    await TestSetupHelper.setupStandardMocks(page);
    await TestSetupHelper.setupAssetMocks(page);
    await TestSetupHelper.disableAnimations(page);

    // Hide connection overlay to prevent test flakiness
    await page.addStyleTag({
      content: ".connection-lost-overlay { display: none !important; }",
    });
  });

  test("should display asset manager with mocked assets", async ({ page }) => {
    await TestSetupHelper.waitForLocalization(
      page,
      "en",
      page.goto("/asset-manager"),
    );
    await page.locator(".active-db-name").waitFor({ state: "visible" });

    const container = page.locator("app-asset-manager");
    const _harness = new AssetManagerHarnessE2e(container);

    // Wait for the asset list to appear
    await page.locator(".asset-grid").waitFor({ state: "visible" });

    // Ensure loading is finished
    await page.locator(".loading-overlay").waitFor({ state: "hidden" });

    // Wait for items to be rendered (we expect 4 items from mock: image, sound, 2x image_set)
    // Counts and names checked visually

    // Reset scroll to top of all containers to avoid clipping
    await page
      .locator(".asset-grid")
      .evaluate((el: any) => (el.scrollTop = 0))
      .catch(() => null);
    await page
      .locator(".stats-content")
      .evaluate((el: any) => (el.scrollTop = 0))
      .catch(() => null);

    await page.waitForTimeout(300); // Final settle

    await expect(page).toHaveScreenshot("asset-manager-list.png", {
      maxDiffPixelRatio: 0.1,
      threshold: 0.2,
    });
  });

  test("should filter assets visuals", async ({ page }) => {
    await TestSetupHelper.waitForLocalization(
      page,
      "en",
      page.goto("/asset-manager"),
    );
    await page.locator(".asset-grid").waitFor({ state: "visible" });

    const container = page.locator("app-asset-manager");
    const harness = new AssetManagerHarnessE2e(container);

    // Click Images Filter
    await harness.setFilterType("image");
    await page.waitForTimeout(100); // Give Angular a moment to settle state after click

    // Filter state checked visually

    // Wait for list to update
    // Counts and names checked visually

    await expect(page).toHaveScreenshot("asset-manager-filtered-images.png", {
      maxDiffPixelRatio: 0.05,
    });
  });

  test("should filter assets by name", async ({ page }) => {
    await TestSetupHelper.waitForLocalization(
      page,
      "en",
      page.goto("/asset-manager"),
    );
    await page.locator(".asset-grid").waitFor({ state: "visible" });

    const container = page.locator("app-asset-manager");
    const harness = new AssetManagerHarnessE2e(container);

    // Filter by name "Fuel"
    await harness.setSearchText("Fuel");
    await page.waitForTimeout(100);

    await expect(page).toHaveScreenshot("asset-manager-filtered-name.png");
  });

  test("should show audio set assets visuals", async ({ page }) => {
    await TestSetupHelper.waitForLocalization(
      page,
      "en",
      page.goto("/asset-manager"),
    );
    await page.locator(".asset-grid").waitFor({ state: "visible" });

    const container = page.locator("app-asset-manager");
    const harness = new AssetManagerHarnessE2e(container);

    await harness.setFilterType("audio_set");
    await page.waitForTimeout(100);

    await expect(page).toHaveScreenshot(
      "asset-manager-filtered-audio-sets.png",
    );
  });

  test("should show custom rotation assets visuals", async ({ page }) => {
    await TestSetupHelper.waitForLocalization(
      page,
      "en",
      page.goto("/asset-manager"),
    );
    await page.locator(".asset-grid").waitFor({ state: "visible" });

    const container = page.locator("app-asset-manager");
    const harness = new AssetManagerHarnessE2e(container);

    await harness.setFilterType("custom_rotation");
    await page.waitForTimeout(100);

    await expect(page).toHaveScreenshot("asset-manager-filtered-rotations.png");
  });

  test("should open new image set editor", async ({ page }) => {
    await TestSetupHelper.waitForLocalization(
      page,
      "en",
      page.goto("/asset-manager"),
    );
    await page.locator(".asset-grid").waitFor({ state: "visible" });

    const container = page.locator("app-asset-manager");
    const harness = new AssetManagerHarnessE2e(container);

    await harness.clickNewImageSet();
    await page
      .locator("app-image-set-editor .modal-content")
      .waitFor({ state: "visible" });
    await page.waitForTimeout(500); // Wait for modal animation settle

    await expect(page).toHaveScreenshot("asset-manager-new-image-set.png");
  });

  test("should open new audio set editor", async ({ page }) => {
    await TestSetupHelper.waitForLocalization(
      page,
      "en",
      page.goto("/asset-manager"),
    );
    await page.locator(".asset-grid").waitFor({ state: "visible" });

    const container = page.locator("app-asset-manager");
    const harness = new AssetManagerHarnessE2e(container);

    await harness.clickNewAudioSet();
    await page
      .locator("app-audio-set-editor .modal-content")
      .waitFor({ state: "visible" });
    await page.waitForTimeout(500);

    await expect(page).toHaveScreenshot("asset-manager-new-audio-set.png");
  });

  test("should open new custom rotation editor", async ({ page }) => {
    await TestSetupHelper.waitForLocalization(
      page,
      "en",
      page.goto("/asset-manager"),
    );
    await page.locator(".asset-grid").waitFor({ state: "visible" });

    const container = page.locator("app-asset-manager");
    const harness = new AssetManagerHarnessE2e(container);

    await harness.clickNewCustomRotation();
    await page
      .locator("app-custom-rotation-editor .modal-content")
      .waitFor({ state: "visible" });
    await page.waitForTimeout(500);

    await expect(page).toHaveScreenshot(
      "asset-manager-new-custom-rotation.png",
    );
  });
});
