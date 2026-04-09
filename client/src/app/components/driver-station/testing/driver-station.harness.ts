import { ComponentHarness } from "@angular/cdk/testing";

import { DriverStationHarnessBase } from "./driver-station.harness.base";

export class DriverStationHarness
  extends ComponentHarness
  implements DriverStationHarnessBase
{
  static hostSelector = DriverStationHarnessBase.hostSelector;

  protected getDataRows = this.locatorForAll(
    DriverStationHarnessBase.selectors.dataRow,
  );
  protected getDataRowLabels = this.locatorForAll(
    `${DriverStationHarnessBase.selectors.dataRow} ${DriverStationHarnessBase.selectors.label}`,
  );
  protected getDataRowValues = this.locatorForAll(
    `${DriverStationHarnessBase.selectors.dataRow} ${DriverStationHarnessBase.selectors.value}`,
  );
  protected getThermometer = this.locatorForAll(
    DriverStationHarnessBase.selectors.thermometer,
  );
  protected getFuelColumn = this.locatorForOptional(".fuel-column");

  async getDataRowCount(): Promise<number> {
    return (await this.getDataRows()).length;
  }

  async getLabelText(index: number): Promise<string> {
    const labels = await this.getDataRowLabels();
    if (index < labels.length) {
      return await labels[index].text();
    }
    return "";
  }

  async getValueText(index: number): Promise<string> {
    const values = await this.getDataRowValues();
    if (index < values.length) {
      return await values[index].text();
    }
    return "";
  }

  async getThermometerFillHeight(): Promise<string | null> {
    const thermo = await this.getThermometer();
    if (thermo.length > 0) {
      return await thermo[0].getAttribute("style");
    }
    return null;
  }

  async hasFuelColumn(): Promise<boolean> {
    const col = await this.getFuelColumn();
    return col !== null;
  }
}
