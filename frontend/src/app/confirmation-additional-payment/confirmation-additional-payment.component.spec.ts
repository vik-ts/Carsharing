import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmationAdditionalPaymentComponent } from './confirmation-additional-payment.component';

describe('ConfirmationAdditionalPaymentComponent', () => {
  let component: ConfirmationAdditionalPaymentComponent;
  let fixture: ComponentFixture<ConfirmationAdditionalPaymentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfirmationAdditionalPaymentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmationAdditionalPaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
