import { ChangeDetectorRef, ViewContainerRef } from "@angular/core";
import {
  ComponentFixture,
  discardPeriodicTasks,
  fakeAsync,
  TestBed,
  tick,
} from "@angular/core/testing";
import { DynamicComponentService } from "@app/services/dynamic-component.service";
import { FileSystemService } from "@app/services/file-system.service";
import { LoggerService } from "@app/services/logger.service";

import { DefaultDriverResultsComponent } from "./default-driver-results.component";
import { DriverResultsComponent } from "./driver-results.component";

describe("DriverResultsComponent", () => {
  let component: DriverResultsComponent;
  let fixture: ComponentFixture<DriverResultsComponent>;
  let mockFileSystemService: jasmine.SpyObj<FileSystemService>;
  let mockContainer: jasmine.SpyObj<ViewContainerRef>;
  let mockDynamicComponentService: jasmine.SpyObj<DynamicComponentService>;
  let mockLoggerService: jasmine.SpyObj<LoggerService>;
  let mockCdr: jasmine.SpyObj<ChangeDetectorRef>;

  beforeEach(async () => {
    mockFileSystemService = jasmine.createSpyObj("FileSystemService", [
      "hasCustomFiles",
      "getCustomFile",
    ]);
    mockContainer = jasmine.createSpyObj("ViewContainerRef", [
      "clear",
      "createComponent",
    ]);
    mockDynamicComponentService = jasmine.createSpyObj(
      "DynamicComponentService",
      ["createDynamicComponent"],
    );
    mockLoggerService = jasmine.createSpyObj("LoggerService", [
      "debug",
      "info",
      "warn",
      "error",
      "log",
    ]);
    mockCdr = jasmine.createSpyObj("ChangeDetectorRef", ["detectChanges"]);

    await TestBed.configureTestingModule({
      providers: [
        { provide: FileSystemService, useValue: mockFileSystemService },
        {
          provide: DynamicComponentService,
          useValue: mockDynamicComponentService,
        },
        { provide: LoggerService, useValue: mockLoggerService },
        { provide: ChangeDetectorRef, useValue: mockCdr },
      ],
      imports: [DriverResultsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DriverResultsComponent);
    component = fixture.componentInstance;
    component.container = mockContainer;
  });

  afterEach(() => {
    fixture.destroy();
    try {
      discardPeriodicTasks();
    } catch (e) {}
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  describe("Custom UI Loading Logic", () => {
    it("should try to load from 'driver-results' subfolder first", fakeAsync(() => {
      mockFileSystemService.hasCustomFiles.and.callFake(
        (file?: string, subfolder?: string) => {
          if (subfolder === "driver-results") return Promise.resolve(true);
          return Promise.resolve(false);
        },
      );
      mockFileSystemService.getCustomFile.and.returnValue(
        Promise.resolve("<html></html>"),
      );
      mockDynamicComponentService.createDynamicComponent.and.returnValue(
        class {},
      );
      mockContainer.createComponent.and.returnValue({ instance: {} } as any);

      component.ngOnInit();
      tick();

      expect(mockFileSystemService.hasCustomFiles).toHaveBeenCalledWith(
        "driver-results.component.html",
        "driver-results",
      );
      expect(mockFileSystemService.getCustomFile).toHaveBeenCalledWith(
        "driver-results.component.html",
        "driver-results",
      );
    }));

    it("should fall back to root custom folder if subfolder missing", fakeAsync(() => {
      mockFileSystemService.hasCustomFiles.and.callFake(
        (file?: string, subfolder?: string) => {
          if (subfolder === "driver-results") return Promise.resolve(false);
          if (!subfolder) return Promise.resolve(true);
          return Promise.resolve(false);
        },
      );
      mockFileSystemService.getCustomFile.and.returnValue(
        Promise.resolve("<html></html>"),
      );
      mockDynamicComponentService.createDynamicComponent.and.returnValue(
        class {},
      );
      mockContainer.createComponent.and.returnValue({ instance: {} } as any);

      component.ngOnInit();
      tick();

      expect(mockFileSystemService.hasCustomFiles).toHaveBeenCalledWith(
        "driver-results.component.html",
        "driver-results",
      );
      expect(mockFileSystemService.hasCustomFiles).toHaveBeenCalledWith(
        "driver-results.component.html",
      );
      expect(mockFileSystemService.getCustomFile).toHaveBeenCalledWith(
        "driver-results.component.html",
        undefined,
      );
    }));

    it("should load default component if both custom locations missing", fakeAsync(() => {
      mockFileSystemService.hasCustomFiles.and.returnValue(
        Promise.resolve(false),
      );
      mockContainer.createComponent.and.returnValue({
        instance: {},
      } as any);

      component.ngOnInit();
      tick();

      expect(mockFileSystemService.hasCustomFiles).toHaveBeenCalledWith(
        "driver-results.component.html",
        "driver-results",
      );
      expect(mockFileSystemService.hasCustomFiles).toHaveBeenCalledWith(
        "driver-results.component.html",
      );
      expect(mockContainer.createComponent).toHaveBeenCalledWith(
        DefaultDriverResultsComponent as any,
      );
    }));
  });
});
