import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { CarBookingService } from '../services/car-booking.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirmation-booking',
  templateUrl: './confirmation-booking.component.html',
  styleUrls: ['./confirmation-booking.component.css']
})
export class ConfirmationBookingComponent implements OnInit {

  bookings: {};
  message = '';
  unconfirmedBookings = [];
  element: any;
  id: any;

  constructor(private auth: AuthService, private location: Location, private carBookingService: CarBookingService,
    private router: Router) { }

  ngOnInit() {
    this.id = this.auth.id;  // id User
    this.getBookings();
  }

  getBookings() {
    this.carBookingService.getConfirmBookings(this.id).subscribe(res => {
      this.bookings = res['body'];
      }, (err) => {
      this.message = err['message'];
      });
  }

  // 1. prepare an array to save (confirmed or rejected items)
  // 2. work with checkboxes: if confirmed is true, then rejected = false and on the contrary
  changeConfirmedRejected(booking, event, checkbox) {

    // press the flag, set to booking true or false, the second flag is not set
    if (checkbox === 1) {
      booking.confirmed = event.target.checked;
    } else if (checkbox === 2) {
      booking.rejected = event.target.checked;
    }

    if (event.target.checked) {
      // if one flag is set, and another is set, then the first one is taken off,
      // we write down the changes in the booking
      if ((checkbox === 1) && (booking.rejected === true)) {
        booking.rejected = false;
        this.UpdateBookingArray(booking.idCarBooking, booking.confirmed, booking.rejected, booking.comment);
      } else if ((checkbox === 2) && (booking.confirmed === true)) {
        booking.confirmed = false;
        this.UpdateBookingArray(booking.idCarBooking, booking.confirmed, booking.rejected, booking.comment);
      } else {
        this.pushBookingArray(booking.idCarBooking, booking.confirmed, booking.rejected, booking.comment);
      }
    } else {
      if (!event.target.checked) {
        this.DeleteBookingArray(booking.idCarBooking);
      }
    }
  }

  updateComment(booking) {
    this.UpdateBookingArray(booking.idCarBooking, booking.confirmed, booking.rejected, booking.comment);
  }

  UpdateBookingArray(id, confirmed, rejected, comment) {
    this.element = this.unconfirmedBookings.find(x => x.id === id);
    if (this.element) {
      this.element.confirmed = confirmed;
      this.element.rejected = rejected;
      this.element.comment = comment;
    }
  }

  DeleteBookingArray(id) {
    this.element = this.unconfirmedBookings.find(x => x.id === id);
    this.unconfirmedBookings.splice(this.element, 1);
  }

  pushBookingArray(id, confirmed, rejected, comment) {
    return this.unconfirmedBookings.push({
      'id':     id,
      'confirmed': confirmed,
      'rejected': rejected,
      'comment': comment
    });
  }

  backClicked() {
    this.location.back();
  }

  putBookings() {
    if (this.unconfirmedBookings.length === 0) {
      this.message = 'Не выбрано ни одной брони для подтверждения / отклонения.';
    } else {
      this.carBookingService.putConfirmBookings(this.unconfirmedBookings).subscribe(res => {
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
