export abstract class RaceEditorHarnessBase {
  static readonly hostSelector = "app-race-editor";

  static readonly selectors = {
    nameInput: "#race-name-input",
    copyBtn: "#copy-item-btn",
    driverCountInput: ".driver-count-inline input",
    rotationSelect: ".editor-section:nth-child(2) select", // Rotation type selector
    trackSelect: "#track-select",
    heatTimesThroughInput: "#heat-times-through-input",
    reverseHeatsCheckbox: "#reverse-heats-checkbox",
  };

  abstract getName(): Promise<string>;
  abstract setName(name: string): Promise<void>;
  abstract clickCopy(): Promise<void>;
  abstract getDriverCount(): Promise<number>;
  abstract setDriverCount(count: number): Promise<void>;
  abstract getTrack(): Promise<string>;
  abstract setTrack(trackId: string): Promise<void>;
  abstract getHeatTimesThrough(): Promise<number>;
  abstract setHeatTimesThrough(count: number): Promise<void>;
  abstract getReverseHeats(): Promise<boolean>;
  abstract setReverseHeats(reverse: boolean): Promise<void>;
}
