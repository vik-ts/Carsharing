import { Component, OnInit } from '@angular/core';
import { Car } from '../models/car';
import { AuthService } from '../services/auth.service';
import { CarService} from '../services/car.service';
import { Location } from '@angular/common';
import { Router } from '@angular/router';

declare var ymaps: any;

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
  selectedFile: File;
  imagePreview: any[];
  id: any;
  fieldArray: Array<any> = [];
  newAttribute: any = {};
  colorMessage: boolean;

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
    ymaps.ready(this.init());
    this.imagePreview = [];
  }

  init() { // for working with maps

    let myPlacemark, MyMap, coords;
    MyMap = new ymaps.Map('MyMapCar', {
      center: [53.6884, 23.8258],
      zoom: 9
      }, {
          searchControlProvider: 'yandex#search'
    });
    // click on the map
    MyMap.events.add('click', function (e) {
      coords = e.get('coords');
      localStorage.setItem('sel_coord', JSON.stringify(coords));

      // if the label is already created, move it
      if (myPlacemark) {
          myPlacemark.geometry.setCoordinates(coords);
      } else { // if not, create
        myPlacemark = createPlacemark(coords);
        MyMap.geoObjects.add(myPlacemark);
          // end of dragging
          myPlacemark.events.add('dragend', function () {
              getAddress(myPlacemark.geometry.getCoordinates());
          });
      }
      getAddress(coords);
    });

    function createPlacemark(coord) {
      return new ymaps.Placemark(coord, {
        iconCaption: 'поиск...'
      }, {
        preset: 'islands#violetDotIconWithCaption',
        draggable: true
      });
    }

    function getAddress(coord) {
      myPlacemark.properties.set('iconCaption', 'поиск...');
      ymaps.geocode(coord).then(function (res) {
        let firstGeoObject;
        firstGeoObject = res.geoObjects.get(0);
        myPlacemark.properties
        .set({
          // form a string with data about the object
          iconCaption: [
          // The name of the settlement or higher administrative-territorial formation
          firstGeoObject.getLocalities().length ? firstGeoObject.getLocalities() : firstGeoObject.getAdministrativeAreas(),
          // We get the path to the toponym, if the method returned null, we request the name of the building
          firstGeoObject.getThoroughfare() || firstGeoObject.getPremise()
          ].filter(Boolean).join(', '),
          // set the string with the address of the object
          balloonContent: firstGeoObject.getAddressLine()
        });
      });
    }
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
    } else {
      document.getElementById('boxinput').style.display = 'none';
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

  onFileUpload (event) {
    this.selectedFile = event.target.files[0];
    this.reader = new FileReader();
    this.reader.onload = () => {
      this.imagePreview.push({
        'url': 'data:image/jpg;base64,' + this.reader.result.split(',')[1],
        'item': this.reader.result.split(',')[1]
      });
    };
    this.reader.readAsDataURL(this.selectedFile);
  }

  backClicked() {
    this.location.back();
  }

  addFieldValue() {
    let s, myDate;
    s = this.newAttribute.beginDate;
    myDate = new Date(s.valueOf().replace(/-/g, '/').replace('T', ' '));

    this.fieldArray.push( {
      'beginDate': myDate,  // this date we will write to the database
      'countDays': this.newAttribute.countDays
    });
    this.newAttribute = {};
  }

  deleteFieldValue(index) {
    this.fieldArray.splice(index, 1);
  }

  deletePhoto(index) {
    this.imagePreview.splice(index, 1);
  }

  createCar() {
   let i;
   this.car.calendar = [];
   this.car.carPhotos = [];
   this.car.fuelType = this.selectedFuelTypes;
   this.car.gearboxType = this.selectedGearboxTypes;
   this.car.bodyType = this.selectedBodyTypes;
   this.car.location = localStorage.getItem('sel_coord');

   if (this.fieldArray.length === 0) {
    this.colorMessage = false;
    this.message = 'Не заполнен календарь!';
   } else {
    for (i = 0; i < this.fieldArray.length; i++) {
      this.car.calendar.push({
     'beginDate': this.fieldArray[i].beginDate,
     'countDays': this.fieldArray[i].countDays
     });
    }

    for (i = 0; i < this.imagePreview.length; i++) {
       this.car.carPhotos.push(this.imagePreview[i].item);
    }
      this.carService.createAdCar(this.id, this.car).subscribe(res => {
        this.colorMessage = true;
        this.message = res['message'];
        setTimeout(() => {
        this.router.navigate(['/usercars']);
        },
        2000);
      }, (err) => {
        this.colorMessage = false;
        this.message = err['message'];
      });
    }
  }
}
