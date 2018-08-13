import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { CarBookingService } from '../services/car-booking.service';
import { AuthService } from '../services/auth.service';
import { PaymentService } from '../services/payment.service';

@Component({
  selector: 'app-user-bookings',
  templateUrl: './user-bookings.component.html',
  styleUrls: ['./user-bookings.component.css']
})
export class UserBookingsComponent implements OnInit {

  bookings: {};
  message = '';
  id: any;

  constructor(private auth: AuthService, private paymentService: PaymentService,
    private location: Location, private carBookingService: CarBookingService) { }

  ngOnInit() {
    this.id = this.auth.id;
    this.getBookings();
  }

  returnCar(idBooking) {
    this.paymentService.returnCar(idBooking).subscribe(res => {
      this.message = res['message'];
      this.getBookings();
      }, (err) => {
       this.message = err['message'];
      });
  }

  getBookings() {
    this.carBookingService.getUserBookings(this.id).subscribe(res => {
      this.bookings = res['body'];
      }, (err) => {
      this.message = err['message'];
      });
  }

  backClicked() {
    this.location.back();
  }

}

