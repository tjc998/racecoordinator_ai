import { Team } from "src/app/models/team";

import { ConverterCache } from "./converter_cache";

import { ITeamModel } from "src/app/proto/antigravity";

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
