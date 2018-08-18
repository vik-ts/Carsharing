import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { AdminService} from '../services/admin.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirmation-additional-payment',
  templateUrl: './confirmation-additional-payment.component.html',
  styleUrls: ['./confirmation-additional-payment.component.css']
})
export class ConfirmationAdditionalPaymentComponent implements OnInit {

  payments: {};
  message = '';
  unconfirmedPayments = [];
  element: any;
  paymentDetail = {};

  constructor(private adminService: AdminService, private location: Location, private router: Router) { }

  ngOnInit() {
    this.getPayments();
  }

  getPayments() {
    this.adminService.getUnconfirmedPayments().subscribe(res => {
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

  // 1. prepare an array to save (confirmedAdmin or rejectedAdmin items)
  // 2. work with checkboxes: if confirmedAdmin is true, then rejectedAdmin = false and on the contrary
  changeConfirmedRejected(payment, event, checkbox) {

    // press the flag, set to booking true or false, the second flag is not set
    if (checkbox === 1) {
      payment.confirmedAdmin = event.target.checked;
    } else if (checkbox === 2) {
      payment.rejectedAdmin = event.target.checked;
    }

    if (event.target.checked) {
      // if one flag is set, and another is set, then the first one is taken off,
      // we write down the changes in the payment
      if ((checkbox === 1) && (payment.rejectedAdmin === true)) {
        payment.rejectedAdmin = false;
        this.UpdatePaymentArray(payment.id, payment.confirmedAdmin, payment.rejectedAdmin, payment.comment);
      } else if ((checkbox === 2) && (payment.confirmedAdmin === true)) {
        payment.confirmedAdmin = false;
        this.UpdatePaymentArray(payment.id, payment.confirmedAdmin, payment.rejectedAdmin, payment.comment);
      } else {
        this.pushPaymentArray(payment.id, payment.confirmedAdmin, payment.rejectedAdmin, payment.comment);
      }
    } else {
      if (!event.target.checked) {
        this.DeletePaymentArray(payment.id);
      }
    }
  }

  updateComment(payment) {
    this.UpdatePaymentArray(payment.id, payment.confirmedAdmin, payment.rejectedAdmin, payment.comment);
  }

  UpdatePaymentArray(id, confirmedAdmin, rejectedAdmin, comment) {
    this.element = this.unconfirmedPayments.find(x => x.id === id);
    if (this.element) {
      this.element.confirmedAdmin = confirmedAdmin;
      this.element.rejectedAdmin = rejectedAdmin;
      this.element.comment = comment;
    }
  }

  DeletePaymentArray(id) {
    this.element = this.unconfirmedPayments.find(x => x.id === id);
    this.unconfirmedPayments.splice(this.element, 1);
  }

  pushPaymentArray(id, confirmedAdmin, rejectedAdmin, comment) {
    return this.unconfirmedPayments.push({
      'id': id,
      'confirmedAdmin': confirmedAdmin,
      'rejectedAdmin': rejectedAdmin,
      'comment': comment
    });
  }

  putPayments() {
    if (this.unconfirmedPayments.length === 0) {
      this.message = 'Не выбрано ни одной доп. оплаты для подтверждения / отклонения.';
    } else {
      this.adminService.putUnconfirmedPayments(this.unconfirmedPayments).subscribe(res => {
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
