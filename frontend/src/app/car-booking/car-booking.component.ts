import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { CarBooking } from '../models/carBooking';
import { ActivatedRoute, Router } from '@angular/router';
import { CarBookingService } from '../services/car-booking.service';

@Component({
  selector: 'app-car-booking',
  templateUrl: './car-booking.component.html',
  styleUrls: ['./car-booking.component.css']
})
export class CarBookingComponent implements OnInit {

  id; idCar: any;  // id User, id Car
  carBooking = new CarBooking;
  message = '';
  sub; mark: any;
  requestParams = {};

  constructor(private auth: AuthService, private location: Location, private route: ActivatedRoute,
     private carBookingService: CarBookingService, private router: Router) { }

  ngOnInit() {
    this.id = this.auth.id;  // id User

    this.sub = this.route
    .queryParams
    .subscribe(params => {
     this.mark = params['mark'];
    });
    this.idCar = this.route.snapshot.params.id;  // id Car
  }

  createBooking() {
    let s, myDate;

    s = this.carBooking.beginDate.valueOf();
    myDate = new Date(s.replace(/-/g, '/').replace('T', ' '));

    this.requestParams  = { 'idCar': this.idCar, 'idUser': this.id, 'beginDate': myDate,
    'countDays': this.carBooking.countDays};

    this.carBookingService.createBooking(this.requestParams).subscribe(res => {
      this.message = res['message'];
      setTimeout(() => {
      this.router.navigate(['/searchcar']);
      },
      2000);
    }, (err) => {
      this.message = err['message'];
    });
  }

  backClicked() {
    this.location.back();
  }
}
