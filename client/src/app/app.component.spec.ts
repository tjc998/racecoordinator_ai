import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { Router } from '@angular/router';
import { DataService } from './data.service';
import { AnalyticsService } from './analytics.service';
import { of } from 'rxjs';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let mockRouter: any;
  let mockDataService: any;
  let mockAnalyticsService: any;

  beforeEach(async () => {
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockDataService = jasmine.createSpyObj('DataService', ['getServerVersion']);
    mockAnalyticsService = jasmine.createSpyObj('AnalyticsService', ['initTracking', 'updateOptOutStatus', 'trackClick']);

    mockDataService.getServerVersion.and.returnValue(of('TEST-SERVER-VERSION'));

    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: DataService, useValue: mockDataService },
        { provide: AnalyticsService, useValue: mockAnalyticsService }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });
});
