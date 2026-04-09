import { ComponentHarness } from "@angular/cdk/testing";

import { ArduinoEditorHarness } from "..//arduino-editor/testing/arduino-editor.harness";
import { TrackEditorHarnessBase } from "./track-editor.harness.base";

export class TrackEditorHarness
  extends ComponentHarness
  implements TrackEditorHarnessBase
{
  static hostSelector = TrackEditorHarnessBase.hostSelector;

  protected getNameInput = this.locatorFor(
    TrackEditorHarnessBase.selectors.nameInput,
  );
  protected getLaneItems = this.locatorForAll(
    TrackEditorHarnessBase.selectors.laneItem,
  );
  protected getRemoveLaneButtons = this.locatorForAll(
    `${TrackEditorHarnessBase.selectors.laneItem} ${TrackEditorHarnessBase.selectors.removeLaneButton}`,
  );
  protected getLaneLengthInputs = this.locatorForAll(
    `${TrackEditorHarnessBase.selectors.laneItem} ${TrackEditorHarnessBase.selectors.laneLengthInput}`,
  );
  protected getAddLaneButton = this.locatorFor(
    TrackEditorHarnessBase.selectors.addLaneButton,
  );
  protected getAddInterfaceButton = this.locatorFor(
    TrackEditorHarnessBase.selectors.addInterfaceButton,
  );
  protected getArduinoEditors = this.locatorForAll(ArduinoEditorHarness);
  protected getDuplicateButton = this.locatorFor(
    TrackEditorHarnessBase.selectors.duplicateButton,
  );

  async getTrackName(): Promise<string> {
    const input = await this.getNameInput();
    return await input.getProperty("value");
  }

  async setTrackName(name: string): Promise<void> {
    const input = await this.getNameInput();
    await input.clear();
    await input.sendKeys(name);
  }

  async getLaneCount(): Promise<number> {
    const items = await this.getLaneItems();
    return items.length;
  }

  async addLane(): Promise<void> {
    const btn = await this.getAddLaneButton();
    await btn.click();
  }

  async removeLane(index: number): Promise<void> {
    const btns = await this.getRemoveLaneButtons();
    if (index < btns.length) {
      await btns[index].click();
    }
  }

  async getLaneLength(index: number): Promise<number> {
    const inputs = await this.getLaneLengthInputs();
    if (index < inputs.length) {
      return parseFloat(await inputs[index].getProperty("value"));
    }
    return 0;
  }

  async setLaneLength(index: number, length: number): Promise<void> {
    const inputs = await this.getLaneLengthInputs();
    if (index < inputs.length) {
      await inputs[index].clear();
      await inputs[index].sendKeys(length.toString());
    }
  }

  async addInterface(): Promise<void> {
    const btn = await this.getAddInterfaceButton();
    await btn.click();
  }

  async getArduinoEditorHarnesses(): Promise<ArduinoEditorHarness[]> {
    return await this.getArduinoEditors();
  }

  async clickSaveAsNew(): Promise<void> {
    const btn = await this.getDuplicateButton();
    await btn.click();
  }

  async isNameInvalid(): Promise<boolean> {
    const section = await this.locatorFor(
      TrackEditorHarnessBase.selectors.nameSection,
    )();
    return await section.hasClass("invalid");
  }
}
