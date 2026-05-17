import { Team } from "@app/models/team";
import { ITeamModel } from "@app/proto/antigravity";

import { ConverterCache } from "./converter_cache";

export class TeamConverter {
  private static cache = new ConverterCache<Team>();

  static clearCache() {
    this.cache.clear();
  }

  static get(id: string): Team | undefined {
    return this.cache.get(id);
  }

  static fromProto(proto: ITeamModel): Team {
    const objectId = proto.model?.entityId;

    // Is Reference if name is falsey (undefined, null, empty string)
    const isReference = !proto.name;

    if (objectId) {
      const cached = this.cache.get(objectId);
      if (cached) {
        if (isReference) {
          return cached;
        }
        // Update in place to preserve references
        cached.name = proto.name || "";
        cached.avatarUrl = proto.avatarUrl || undefined;
        cached.driverIds = proto.driverIds || [];
        return cached;
      }
    }

    return this.cache.process(objectId, isReference, () => {
      return new Team(
        objectId || "",
        proto.name || "",
        proto.avatarUrl || undefined,
        proto.driverIds || [],
      );
    });
  }
}
