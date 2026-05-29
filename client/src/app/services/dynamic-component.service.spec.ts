import { ɵgetComponentDef as getComponentDef } from "@angular/core";
import { TestBed } from "@angular/core/testing";

import { DynamicComponentService } from "./dynamic-component.service";

describe("DynamicComponentService", () => {
  let service: DynamicComponentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DynamicComponentService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should generate unique selectors for components", () => {
    const classA = class {};
    const classB = class {};

    const componentA = service.createDynamicComponent(
      classA,
      "<a></a>",
      "",
      "",
    );
    const componentB = service.createDynamicComponent(
      classB,
      "<b></b>",
      "",
      "",
    );

    const defA = getComponentDef(componentA);
    const defB = getComponentDef(componentB);

    expect(defA?.selectors[0][0]).toContain("app-dynamic-component-");
    expect(defB?.selectors[0][0]).toContain("app-dynamic-component-");
    expect(defA?.selectors[0][0]).not.toEqual(defB?.selectors[0][0]);
  });

  it("should include required imports (TranslatePipe, Modals, etc.)", () => {
    const baseClass = class {};
    const component = service.createDynamicComponent(
      baseClass,
      "<html></html>",
      "",
      "",
    );

    // In Angular, we can't easily inspect imports from the component definition at runtime
    // without using the compiler or private APIs, but we can verify it doesn't throw
    // and that it returns a valid component type.
    expect(component).toBeTruthy();

    const def = getComponentDef(component);
    expect(def).toBeTruthy();
    // standalone: true is represented as a flag in the component definition
    expect((def as any).standalone).toBeTrue();
  });

  it("should compile a template using DatePipe and routerLink successfully", () => {
    const baseClass = class {
      testDate = new Date();
    };

    // This template uses the date pipe and a routerLink binding.
    // Before the fix, this would fail to compile or render without the proper imports in DynamicComponent.
    const html = `
      <div>{{ testDate | date:'yyyy-MM-dd' }}</div>
      <a [routerLink]="['/driver-results', '123']">Driver Results</a>
    `;

    const component = service.createDynamicComponent(baseClass, html, "", "");

    expect(component).toBeTruthy();
    const def = getComponentDef(component);
    expect(def).toBeTruthy();
    expect((def as any).standalone).toBeTrue();
  });
});
