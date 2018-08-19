import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { PaymentService } from '../services/payment.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirmation-payment',
  templateUrl: './confirmation-payment.component.html',
  styleUrls: ['./confirmation-payment.component.css']
})
export class ConfirmationPaymentComponent implements OnInit {

  payments: {};
  message = '';
  unconfirmedPayments = [];
  element: any;
  id: any;
  paymentDetail = {};

  constructor(private auth: AuthService, private location: Location, private paymentService: PaymentService, private router: Router) { }

  ngOnInit() {
    this.id = this.auth.id;  // id User
    this.getPayments();
  }

  getPayments() {
    this.paymentService.getConfirmPayments(this.id).subscribe(res => {
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

  changeConfirmed(payment, event) {
    payment.confirmed = event.target.checked;

    if (event.target.checked) {
      this.pushPaymentArray(payment.id, payment.addAmountToPay,
        payment.comment, payment.paymentRequisites);

    } else {
      if (!event.target.checked) {
        this.DeletePaymentArray(payment.id);
      }
    }
  }

  updateRequisites(payment) {
    this.element = this.unconfirmedPayments.find(x => x.id === payment.id);
    if (this.element) {
      this.element.addAmountToPay = payment.addAmountToPay;
      this.element.comment = payment.comment;
      this.element.paymentRequisites = payment.paymentRequisites;
    }
  }

  DeletePaymentArray(id) {
    this.element = this.unconfirmedPayments.find(x => x.id === id);
    this.unconfirmedPayments.splice(this.element, 1);
  }

  pushPaymentArray(id, addAmountToPay, comment, paymentRequisites)  {
    return this.unconfirmedPayments.push({
      'id': id,
      'addAmountToPay': addAmountToPay,
      'comment': comment,
      'paymentRequisites': paymentRequisites
    });
  }

  backClicked() {
    this.location.back();
  }

  putPayments() {
    if (this.unconfirmedPayments.length === 0) {
      this.message = 'Не выбрано ни одной оплаты для подтверждения';
    } else {
      this.paymentService.putConfirmPayments(this.unconfirmedPayments).subscribe(res => {
        this.message = res['message'];
        setTimeout(() => {
          this.router.navigate(['/homeuser']);
        },
       2000);
      }, (err) => {
      this.message = err['message'];
      });
    }
  }
}
