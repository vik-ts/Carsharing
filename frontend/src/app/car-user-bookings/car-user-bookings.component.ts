import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { CarBookingService } from '../services/car-booking.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-car-user-bookings',
  templateUrl: './car-user-bookings.component.html',
  styleUrls: ['./car-user-bookings.component.css']
})
export class CarUserBookingsComponent implements OnInit {

  bookings: {};
  message = '';
  id: any;

  constructor(private auth: AuthService, private location: Location, private carBookingService: CarBookingService) { }

  ngOnInit() {
    this.id = this.auth.id;
    this.getBookings();
  }

  getBookings() {
    this.carBookingService.getCarUserBookings(this.id).subscribe(res => {
      this.bookings = res['body'];
      }, (err) => {
      this.message = err['message'];
      });
  }

  backClicked() {
    this.location.back();
  }
}
