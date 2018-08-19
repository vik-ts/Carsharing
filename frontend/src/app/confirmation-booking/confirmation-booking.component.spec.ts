import { async, ComponentFixture, TestBed, ComponentFixtureAutoDetect } from '@angular/core/testing';
import { ConfirmationBookingComponent } from './confirmation-booking.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CarBookingService } from '../services/car-booking.service';
import { AuthService } from '../services/auth.service';

describe('ConfirmationBookingComponent', () => {
  let component: ConfirmationBookingComponent;
  let fixture: ComponentFixture<ConfirmationBookingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfirmationBookingComponent ],
      providers: [AuthService, CarBookingService, { provide: ComponentFixtureAutoDetect, useValue: true }],
      imports: [RouterTestingModule, HttpClientModule, FormsModule]
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
