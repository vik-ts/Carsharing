import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { AdminService} from '../services/admin.service';
import { UserService} from '../services/user.service';

@Component({
  selector: 'app-activation-booking',
  templateUrl: './activation-booking.component.html',
  styleUrls: ['./activation-booking.component.css']
})
export class ActivationBookingComponent implements OnInit {

  bookings: {};
  userinfo: {};
  message = '';
  inactivBookings = [];
  element: any;

  constructor(private adminService: AdminService, private userService: UserService, private location: Location) { }

  ngOnInit() {
    this.getBookings();
  }

  // 1. prepare an array to save (activated or rejected items)
  // 2. work with checkboxes: if activated is true, then rejected = false and on the contrary
  changeActiveReject(booking, event, checkbox) {

    // press the flag, set to booking true or false, the second flag is not set
    if (checkbox === 1) {
      booking.activated = event.target.checked;
    } else if (checkbox === 2) {
      booking.rejected = event.target.checked;
    }

    if (event.target.checked) {
      // if one flag is set, and another is set, then the first one is taken off,
      // we write down the changes in the booking
      if ((checkbox === 1) && (booking.rejected === true)) {
        booking.rejected = false;
        this.UpdateBookingArray(booking.idCarBooking, booking.activated, booking.rejected, booking.comment);
      } else if ((checkbox === 2) && (booking.activated === true)) {
        booking.activated = false;
        this.UpdateBookingArray(booking.idCarBooking, booking.activated, booking.rejected, booking.comment);
      } else {
        this.pushBookingArray(booking.idCarBooking, booking.activated, booking.rejected, booking.comment);
      }
    } else {
      if (!event.target.checked) {
        this.DeleteBookingArray(booking.idCarBooking);
      }
    }
  }

  updateComment(booking) {
    this.UpdateBookingArray(booking.id, booking.activated, booking.rejected, booking.comment);
  }

  UpdateBookingArray(id, activated, rejected, comment) {
    this.element = this.inactivBookings.find(x => x.id === id);
    if (this.element) {
      this.element.activated = activated;
      this.element.rejected = rejected;
      this.element.comment = comment;
    }
  }

  DeleteBookingArray(id) {
    this.element = this.inactivBookings.find(x => x.id === id);
    this.inactivBookings.splice(this.element, 1);
  }

  pushBookingArray(id, activated, rejected, comment) {
    return this.inactivBookings.push({
      'id':     id,
      'activated': activated,
      'rejected': rejected,
      'comment': comment
    });
  }

  goToUserInfo(idUser) {
    this.userService.getUserInfoByUser(idUser).subscribe(res => {
    this.userinfo = res['body'];
      }, (err) => {
       this.message = 'Произошла ошибка. Личный кабинет не найден.';
      });
    return this.show('block');
  }

  show(state) {
    document.getElementById('window').style.display = state;
    return false;
  }

  backClicked() {
    this.location.back();
  }

  getBookings() {
    this.adminService.getInactiveCarBookings().subscribe(res => {
      this.bookings = res['body'];
      }, (err) => {
      this.message = err['message'];
      });
  }

  putBookings() {

    if (this.inactivBookings.length === 0) {
      this.message = 'Не выбрано ни одной брони для активации / отклонения.';
    } else {
      this.adminService.putInactiveCarBookings(this.inactivBookings).subscribe(res => {
      this.message = res['message'];
      }, (err) => {
      this.message = err['message'];
      });
    }
  }
}
