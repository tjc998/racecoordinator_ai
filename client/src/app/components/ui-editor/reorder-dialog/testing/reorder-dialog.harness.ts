import { ComponentHarness } from "@angular/cdk/testing";

import { ReorderDialogHarnessBase } from "./reorder-dialog.harness.base";

export class ReorderDialogHarness
  extends ComponentHarness
  implements ReorderDialogHarnessBase
{
  static hostSelector = ReorderDialogHarnessBase.hostSelector;

  protected getBackdrop = this.locatorForOptional(
    ReorderDialogHarnessBase.selectors.backdrop,
  );
  protected getTitleText = this.locatorFor(
    ReorderDialogHarnessBase.selectors.title,
  );
  protected getValueChips = this.locatorForAll(
    ReorderDialogHarnessBase.selectors.valueChip,
  );
  protected getSlotItems = this.locatorForAll(
    ReorderDialogHarnessBase.selectors.slotItem,
  );
  protected getSlotTitles = this.locatorForAll(
    `${ReorderDialogHarnessBase.selectors.slotItem} ${ReorderDialogHarnessBase.selectors.slotTitle}`,
  );
  protected getSlotRemoveBtns = this.locatorForAll(
    `${ReorderDialogHarnessBase.selectors.slotItem} ${ReorderDialogHarnessBase.selectors.removeBtn}`,
  );
  protected getSaveBtn = this.locatorFor(
    ReorderDialogHarnessBase.selectors.saveBtn,
  );
  protected getCancelBtn = this.locatorFor(
    ReorderDialogHarnessBase.selectors.cancelBtn,
  );

  async isVisible(): Promise<boolean> {
    const backdrop = await this.getBackdrop();
    return backdrop !== null;
  }

  async getTitle(): Promise<string> {
    const el = await this.getTitleText();
    return await el.text();
  }

  async getAvailableValues(): Promise<string[]> {
    const chips = await this.getValueChips();
    const values: string[] = [];
    for (const chip of chips) {
      values.push(await chip.text());
    }
    return values;
  }

  async getSlotCount(): Promise<number> {
    return (await this.getSlotItems()).length;
  }

  async getSlotTitle(index: number): Promise<string> {
    const titles = await this.getSlotTitles();
    if (index < titles.length) {
      return await titles[index].text();
    }
    return "";
  }

  async clickRemoveSlot(index: number): Promise<void> {
    const btns = await this.getSlotRemoveBtns();
    if (index < btns.length) {
      await btns[index].click();
    }
  }

  async clickResetDefaults(): Promise<void> {
    const btn = await this.locatorFor(
      ReorderDialogHarnessBase.selectors.resetDefaultsBtn,
    )();
    await btn.click();
  }

  async clickSave(): Promise<void> {
    const btn = await this.getSaveBtn();
    await btn.click();
  }

  async clickCancel(): Promise<void> {
    const btn = await this.getCancelBtn();
    await btn.click();
  }
}
