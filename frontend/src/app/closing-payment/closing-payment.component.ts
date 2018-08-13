import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { PaymentService } from '../services/payment.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-closing-payment',
  templateUrl: './closing-payment.component.html',
  styleUrls: ['./closing-payment.component.css']
})
export class ClosingPaymentComponent implements OnInit {

  payments: {};
  message = '';
  unclosedPayments = [];
  element: any;
  id: any;
  paymentDetail = {};

  constructor(private auth: AuthService, private location: Location, private paymentService: PaymentService) { }

  ngOnInit() {
    this.id = this.auth.id;  // id User
    this.getPayments();
  }

  getPayments() {
    this.paymentService.getUnclosedPayments(this.id).subscribe(res => {
      this.payments = res['body'];
      }, (err) => {
      this.message = err['message'];
      });
  }

  goToDetail(paymentItem) {
    this.paymentDetail = paymentItem;
    return this.show('block');
  }

  show(state) {
    document.getElementById('window').style.display = state;
    return false;
  }

  changeClosed(payment, event) {
    payment.closed = event.target.checked;

    if (event.target.checked) {
      this.pushPaymentArray(payment.id);

    } else {
      if (!event.target.checked) {
        this.DeletePaymentArray(payment.id);
      }
    }
  }

  DeletePaymentArray(id) {
    this.element = this.unclosedPayments.find(x => x.id === id);
    this.unclosedPayments.splice(this.element, 1);
  }

  pushPaymentArray(id)  {
    return this.unclosedPayments.push({
      'id': id
    });
  }

  backClicked() {
    this.location.back();
  }

  putPayments() {
    if (this.unclosedPayments.length === 0) {
      this.message = 'Не выбрано ни одной оплаты';
    } else {
      this.paymentService.putUnclosedPayments(this.unclosedPayments).subscribe(res => {
      this.message = res['message'];
      }, (err) => {
      this.message = err['message'];
      });
    }
  }
}
