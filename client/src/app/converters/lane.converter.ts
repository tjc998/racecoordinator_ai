import { Lane } from "src/app/models/lane";

import { ConverterCache } from "./converter_cache";

import { ILaneModel } from "src/app/proto/antigravity";

export class LaneConverter {
  private static cache = new ConverterCache<Lane>();

  static clearCache() {
    this.cache.clear();
  }

  static fromProto(proto: ILaneModel): Lane {
    const objectId = proto.objectId;
    const isReference =
      !proto.foregroundColor &&
      !proto.backgroundColor &&
      proto.length === undefined;

    return this.cache.process(objectId, isReference, () => {
      return new Lane(
        objectId || "unset",
        proto.foregroundColor || "",
        proto.backgroundColor || "",
        proto.length || 0,
      );
    });
  }
}
