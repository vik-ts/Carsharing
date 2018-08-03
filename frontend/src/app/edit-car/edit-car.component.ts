import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { CarService} from '../services/car.service';
import { Location } from '@angular/common';
import { Car } from '../models/car';

@Component({
  selector: 'app-edit-car',
  templateUrl: './edit-car.component.html',
  styleUrls: ['./edit-car.component.css']
})
export class EditCarComponent implements OnInit {

  car = new Car;
  message = '';

  fuelTypes = ['Бензин', 'Дизель', 'Газ'];
  selectedFuelTypes = this.fuelTypes[0];

  gearboxTypes = ['Механическая', 'Автоматическая', 'Роботизированная', 'Вариативная'];
  selectedGearboxTypes = this.gearboxTypes[0];

  bodyTypes = ['Седан', 'Хэтчбек', 'Универсал', 'Лифтбэк', 'Купе', 'Кабриолет', 'Родстер', 'Тарга',
   'Лимузин', 'Стретч', 'Внедорожник', 'Кроссовер', 'Пикап', 'Фургон', 'Минивэн', 'Микроавтобус', 'Автобус'];
  selectedBodyTypes = this.bodyTypes[0];

  constructor(private router: ActivatedRoute, private rout: Router, private carService: CarService, private location: Location) { }

  ngOnInit() {
    this.getCar(this.router.snapshot.params.id);  // id car
  }

  backClicked() {
    this.location.back();
  }

  FuelTypesChanged(selectedFuelTypes) {
    this.selectedFuelTypes = selectedFuelTypes;
  }

  GearboxTypesChanged(selectedGearboxTypes) {
    this.selectedGearboxTypes = selectedGearboxTypes;
  }

  BodyTypesChanged(selectedBodyTypes) {
    this.selectedBodyTypes = selectedBodyTypes;
  }

  getCar(id) {   // id car
    this.carService.getUserCar(id, this.car).subscribe(res => {
    this.car = res['body'];
    this.selectedFuelTypes = this.car.fuelType;
    this.selectedGearboxTypes = this.car.gearboxType;
    this.selectedBodyTypes = this.car.bodyType;
    }, (err) => {
    this.message = err['message'];
    });
  }

  edit(id) {
    this.car.fuelType = this.selectedFuelTypes;
    this.car.gearboxType = this.selectedGearboxTypes;
    this.car.bodyType = this.selectedBodyTypes;
    this.carService.putUserCar(id, this.car).subscribe(res => {

    this.message = res['message'];
    setTimeout(() => {
      this.rout.navigate(['/usercars']);
    },
    2000);
    }, (err) => {
    this.message = err['message'];
    });
  }

  delete(id) {
    if (confirm('Вы действительно хотите удалить объявление?')) {
      this.carService.deleteCar(id).subscribe(res => {
        this.message = res['message'];
        setTimeout(() => {
          this.rout.navigate(['/usercars']);
        },
        2000);
        });
    }
  }
}
