import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { Car } from '../models/car';
import { FormGroup } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { CarService} from '../services/car.service';
import { Location } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-car',
  templateUrl: './create-car.component.html',
  styleUrls: ['./create-car.component.css']
})

export class CreateCarComponent implements OnInit {

  car = new Car;
  message = '';
  videoSource: string;
  reader = new FileReader();
  form: FormGroup;
  file: any;
  url: any;
  id: any;

  @ViewChild ('fileInput') fileInput: ElementRef;

  fuelTypes = ['Бензин', 'Дизель', 'Газ'];
  selectedFuelTypes = this.fuelTypes[0];

  gearboxTypes = ['Механическая', 'Автоматическая', 'Роботизированная', 'Вариативная'];
  selectedGearboxTypes = this.gearboxTypes[0];

  bodyTypes = ['Седан', 'Хэтчбек', 'Универсал', 'Лифтбэк', 'Купе', 'Кабриолет', 'Родстер', 'Тарга',
   'Лимузин', 'Стретч', 'Внедорожник', 'Кроссовер', 'Пикап', 'Фургон', 'Минивэн', 'Микроавтобус', 'Автобус'];
  selectedBodyTypes = this.bodyTypes[0];

  constructor(private auth: AuthService, private carService: CarService, private location: Location, private router: Router) {}

  ngOnInit() {
    this.id = this.auth.id;
  }

  openBox(id) {
    const display = document.getElementById(id).style.display;
    if (display === 'none') {
        document.getElementById(id).style.display = 'block';
    } else {
       document.getElementById(id).style.display = 'none';
    }
    return false;
  }

  OpenAddCar (checked) {
    if (checked) {
      document.getElementById('boxinput').style.display = 'block';
      document.getElementById('box').style.display = 'none';
     // document.getElementById('buttonAdd').style.display = 'black';

    } else {
      document.getElementById('boxinput').style.display = 'none';
     // document.getElementById('buttonAdd').style.display = 'none';
    }
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

  onFileChange(event) {
   /* if (event.target.files && event.target.files.length > 0) {
      this.file = event.target.files[0];
      this.reader.readAsDataURL(this.file);
      this.reader.onload = (onLoadPhotoEvent: any) => {
        this.url = onLoadPhotoEvent.target.result;
        this.form.get('photo').setValue({
          filename: this.file.name,
          filetype: this.file.type,
          value: this.reader.result.split(',')[0]
        });
      };
     }*/
/*
    if (event.target.files && event.target.files.length > 0) {
      this.file = event.target.files[0];
      this.reader.readAsDataURL(this.file);
      this.reader.onload = () => {
        this.form.get('car.photo').setValue({
          filename: this.file.name,
          filetype: this.file.type,
          value: this.reader.result.split(',')[0]
        });
      };
    }*/
 // alert(this.form.get('photo'));
  }

  clearFile() {
   // alert(this.form.get('photo'));
   // this.form.get('photo').setValue(null);
   // this.fileInput.nativeElement.value = '';
  }

  backClicked() {
    this.location.back();
  }

  createCar() {
   this.car.fuelType = this.selectedFuelTypes;
   this.car.gearboxType = this.selectedGearboxTypes;
   this.car.bodyType = this.selectedBodyTypes;

    this.carService.createAdCar(this.id, this.car).subscribe(res => {
      this.message = res['message'];
      setTimeout(() => {
        this.router.navigate(['/usercars']);
      },
      2000);
      }, (err) => {
       this.message = err['message'];
      });
    }
}

