import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmationBookingComponent } from './confirmation-booking.component';

describe('ConfirmationBookingComponent', () => {
  let component: ConfirmationBookingComponent;
  let fixture: ComponentFixture<ConfirmationBookingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfirmationBookingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmationBookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
