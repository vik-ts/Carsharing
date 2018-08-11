import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CarUserBookingsComponent } from './car-user-bookings.component';

describe('CarUserBookingsComponent', () => {
  let component: CarUserBookingsComponent;
  let fixture: ComponentFixture<CarUserBookingsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CarUserBookingsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CarUserBookingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
