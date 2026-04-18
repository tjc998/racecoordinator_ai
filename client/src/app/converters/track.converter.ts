import { Track } from "src/app/models/track";
import { com } from "src/app/proto/message";

import { ArduinoConfigConverter } from "./arduino_config.converter";
import { ConverterCache } from "./converter_cache";
import { LaneConverter } from "./lane.converter";

export class TrackConverter {
  private static cache = new ConverterCache<Track>();

  static clearCache() {
    this.cache.clear();
  }

  static fromProto(proto: com.antigravity.ITrackModel): Track {
    if (!proto) {
      return new Track("", "Unknown Track", [], false, []);
    }
    const objectId = proto.model?.entityId || "";
    const isReference =
      (!proto.lanes || proto.lanes.length === 0) && !!objectId;

    if (isReference) {
      const cached = this.cache.get(objectId);
      if (cached) return cached;
      // If reference fails, we fall through to process which might return a fallback
    }

    return this.cache.process(
      objectId,
      isReference,
      () => {
        const lanes = (proto.lanes || []).map((l) =>
          LaneConverter.fromProto(l),
        );
        return new Track(
          objectId,
          proto.name || "Unknown Track",
          lanes,
          proto.hasDigitalFuel ?? false,
          (proto.arduinoConfigs || []).map((ac) =>
            ArduinoConfigConverter.fromProto(ac),
          ),
        );
      },
      () => {
        if (!proto.lanes && !isReference) {
          throw new Error(
            "TrackConverter: proto.lanes is missing for full Track",
          );
        }
      },
    );
  }
}
