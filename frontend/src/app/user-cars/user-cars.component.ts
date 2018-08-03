import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { CarService} from '../services/car.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-cars',
  templateUrl: './user-cars.component.html',
  styleUrls: ['./user-cars.component.css']
})
export class UserCarsComponent implements OnInit {

  cars: any;
  id: any;
  message = '';

  constructor(private auth: AuthService, private carService: CarService, private router: Router) { }

  ngOnInit() {
    this.id = this.auth.id;
    this.getCars();
  }

  getCars() {  // id user
    this.carService.getUserCars(this.id).subscribe(res => {
    this.cars = res['body'];
    }, (err) => {
    this.message = err['message'];
    });
  }

  goToEdit(idCar) {  // id car
    this.router.navigate(['editcar/' + idCar]);
  }
}
