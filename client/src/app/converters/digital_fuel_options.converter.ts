import { DigitalFuelOptions } from "src/app/models/digital_fuel_options";
import { FuelUsageType } from "src/app/models/fuel_options";
import { com } from "src/app/proto/message";

export class DigitalFuelOptionsConverter {
  static fromProto(proto?: com.antigravity.IDigitalFuelOptions | null): DigitalFuelOptions {
    if (!proto) {
      return new DigitalFuelOptions();
    }

    const p = proto as any;

    let usageType = FuelUsageType.LINEAR;
    if (typeof p.usageType === 'number') {
      const types = [FuelUsageType.LINEAR, FuelUsageType.QUADRATIC, FuelUsageType.CUBIC];
      usageType = types[p.usageType] || FuelUsageType.LINEAR;
    } else if (typeof p.usageType === 'string') {
      usageType = p.usageType as FuelUsageType;
    }

    return new DigitalFuelOptions(
      proto.enabled ?? false,
      proto.resetFuelAtHeatStart ?? false,
      proto.endHeatOnOutOfFuel ?? false,
      proto.capacity ?? 100,
      usageType,
      proto.usageRate ?? 4.0,
      proto.startLevel ?? 100,
      proto.refuelRate ?? 10,
      proto.pitStopDelay ?? 2.0
    );
  }
}