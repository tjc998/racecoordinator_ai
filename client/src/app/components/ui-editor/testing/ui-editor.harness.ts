import { ComponentHarness } from "@angular/cdk/testing";

import { ReorderDialogHarness } from "..//reorder-dialog/testing/reorder-dialog.harness";
import { UIEditorHarnessBase } from "./ui-editor.harness.base";

export class UIEditorHarness
  extends ComponentHarness
  implements UIEditorHarnessBase
{
  static hostSelector = UIEditorHarnessBase.hostSelector;

  protected getReorderBtn = this.locatorFor(
    UIEditorHarnessBase.selectors.reorderBtn,
  );
  protected getReorderDialog = this.locatorFor(ReorderDialogHarness);
  protected getImageSelectorsPreviews = this.locatorForAll(
    `${UIEditorHarnessBase.selectors.imageSelector} ${UIEditorHarnessBase.selectors.imagePreview}`,
  );

  async clickReorderColumns(): Promise<void> {
    const btn = await this.getReorderBtn();
    await btn.click();
  }

  async getReorderDialogHarness(): Promise<ReorderDialogHarness> {
    return await this.getReorderDialog();
  }

  async clickImageSelector(index: number): Promise<void> {
    const previews = await this.getImageSelectorsPreviews();
    if (index < previews.length) {
      await previews[index].click();
    }
  }
}
