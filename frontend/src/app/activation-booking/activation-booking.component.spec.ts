import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivationBookingComponent } from './activation-booking.component';

describe('ActivationBookingComponent', () => {
  let component: ActivationBookingComponent;
  let fixture: ComponentFixture<ActivationBookingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActivationBookingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActivationBookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
