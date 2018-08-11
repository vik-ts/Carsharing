import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { AdminService} from '../services/admin.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-activation',
  templateUrl: './activation.component.html',
  styleUrls: ['./activation.component.css']
})
export class ActivationComponent implements OnInit {

  cars: {};
  inactivCars = [];
  element: any;
  message = '';
  carDetail = {};

  constructor(private adminService: AdminService, private router: Router, private location: Location) { }

  ngOnInit() {
    this.getCars();
  }

  // 1. prepare an array to save (activated or rejected items)
  // 2. work with checkboxes: if activated is true, then rejected = false and on the contrary
  changeActiveReject(car, event, checkbox) {

    // press the flag, set to car true or false, the second flag is not set
    if (checkbox === 1) {
      car.activated = event.target.checked;
    } else if (checkbox === 2) {
      car.rejected = event.target.checked;
    }

    if (event.target.checked) {
      // if one flag is set, and another is set, then the first one is taken off,
      // we write down the changes in the car
      if ((checkbox === 1) && (car.rejected === true)) {
        car.rejected = false;
        this.UpdateCarArray(car.id, car.activated, car.rejected, car.comment);
      } else if ((checkbox === 2) && (car.activated === true)) {
        car.activated = false;
        this.UpdateCarArray(car.id, car.activated, car.rejected, car.comment);
      } else {
        this.pushCarArray(car.id, car.activated, car.rejected, car.comment);
      }
    } else {
      if (!event.target.checked) {
        this.DeleteCarArray(car.id);
      }
    }
  }

  updateComment(car) {
    alert(JSON.stringify(car.comment));
    this.UpdateCarArray(car.id, car.activated, car.rejected, car.comment);
  }

  UpdateCarArray(id, activated, rejected, comment) {

    this.element = this.inactivCars.find(x => x.id === id);
    if (this.element) {
      this.element.activated = activated;
      this.element.rejected = rejected;
      this.element.comment = comment;
    }
  }

  DeleteCarArray(id) {
    this.element = this.inactivCars.find(x => x.id === id);
    this.inactivCars.splice(this.element, 1);
  }

  pushCarArray(id, activated, rejected, comment) {
    return this.inactivCars.push({
      'id':     id,
      'activated': activated,
      'rejected': rejected,
      'comment': comment
    });
  }

  backClicked() {
    this.location.back();
  }

  goToDetail(carItem) {
    this.carDetail = carItem;
    return this.show('block');
  }

  show(state) {
    document.getElementById('window').style.display = state;
    return false;
  }

  getCars() {
    this.adminService.getInactiveCars().subscribe(res => {
    this.cars = res['body'];
    }, (err) => {
    this.message = err['message'];
    });
  }

  putCars() {
    if (this.inactivCars.length === 0) {
      this.message = 'Не выбрано ни одного объявления для активации / отклонения.';
    } else {
      this.adminService.putInactiveCars(this.inactivCars).subscribe(res => {
      this.message = res['message'];
      }, (err) => {
      this.message = err['message'];
      });
    }
  }
}
