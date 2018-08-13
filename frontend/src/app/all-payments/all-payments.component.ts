import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { PaymentService } from '../services/payment.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-all-payments',
  templateUrl: './all-payments.component.html',
  styleUrls: ['./all-payments.component.css']
})
export class AllPaymentsComponent implements OnInit {

  payments: {};
  message = '';
  id: any;
  email: string;
  paymentDetail = {};

  constructor(private auth: AuthService, private location: Location, private paymentService: PaymentService) { }

  ngOnInit() {
    this.id = this.auth.id;  // id User
    this.email = this.auth.email; // email user
    this.getPayments();
  }

  getPayments() {
    this.paymentService.getAllPayments(this.id).subscribe(res => {
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

  backClicked() {
    this.location.back();
  }
}
