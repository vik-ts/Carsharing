import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClosingPaymentComponent } from './closing-payment.component';

describe('ClosingPaymentComponent', () => {
  let component: ClosingPaymentComponent;
  let fixture: ComponentFixture<ClosingPaymentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ClosingPaymentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClosingPaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
