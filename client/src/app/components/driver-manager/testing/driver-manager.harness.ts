import { ComponentHarness } from "@angular/cdk/testing";

import { ManagerHeaderHarness } from "../../shared/manager-header/testing/manager-header.harness";
import { DriverManagerHarnessBase } from "./driver-manager.harness.base";

export class DriverManagerHarness
  extends ComponentHarness
  implements DriverManagerHarnessBase
{
  static hostSelector = DriverManagerHarnessBase.hostSelector;

  protected getSearchInput = this.locatorFor(
    DriverManagerHarnessBase.selectors.searchInput,
  );
  protected getDriverRows = this.locatorForAll(
    DriverManagerHarnessBase.selectors.driverRow,
  );
  protected getDriverNameCells = this.locatorForAll(
    `${DriverManagerHarnessBase.selectors.driverRow} ${DriverManagerHarnessBase.selectors.nameCell}`,
  );
  protected getDriverNicknameCells = this.locatorForAll(
    `${DriverManagerHarnessBase.selectors.driverRow} ${DriverManagerHarnessBase.selectors.nicknameCell}`,
  );
  protected getConfigNameInput = this.locatorFor(
    DriverManagerHarnessBase.selectors.configNameInput,
  );

  async getDriverCount(): Promise<number> {
    return (await this.getDriverRows()).length;
  }

  async getDriverName(index: number): Promise<string> {
    const cells = await this.getDriverNameCells();
    if (index < cells.length) {
      return await cells[index].text();
    }
    return "";
  }

  async getDriverNickname(index: number): Promise<string> {
    const cells = await this.getDriverNicknameCells();
    if (index < cells.length) {
      return await cells[index].text();
    }
    return "";
  }

  async selectDriver(index: number): Promise<void> {
    const rows = await this.getDriverRows();
    if (index < rows.length) {
      await rows[index].click();
    }
  }

  async setSearchQuery(query: string): Promise<void> {
    const input = await this.getSearchInput();
    await input.clear();
    await input.sendKeys(query);
  }

  async getSelectedDriverName(): Promise<string> {
    const input = await this.getConfigNameInput();
    return await input.getProperty("value");
  }

  async clickEdit(): Promise<void> {
    const header = await this.locatorFor(ManagerHeaderHarness)();
    const toolbar = await header.getToolbar();
    await toolbar.clickEdit();
  }

  async clickDelete(): Promise<void> {
    const header = await this.locatorFor(ManagerHeaderHarness)();
    const toolbar = await header.getToolbar();
    await toolbar.clickDelete();
  }
}
