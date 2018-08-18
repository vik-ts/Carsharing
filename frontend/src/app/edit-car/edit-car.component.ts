import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CarService} from '../services/car.service';
import { Location } from '@angular/common';
import { Car } from '../models/car';

declare var ymaps: any;

@Component({
  selector: 'app-edit-car',
  templateUrl: './edit-car.component.html',
  styleUrls: ['./edit-car.component.css']
})
export class EditCarComponent implements OnInit {

  car = new Car;
  message = '';
  fieldArray: Array<any> = [];
  newAttribute: any = {};
  reader = new FileReader();
  coords: any;
  colorMessage: boolean;
  selectedFile: File;
  imagePreview: any[];
  notIndicated: string;

  fuelTypes = ['Бензин', 'Дизель', 'Газ'];
  selectedFuelTypes = this.fuelTypes[0];

  gearboxTypes = ['Механическая', 'Автоматическая', 'Роботизированная', 'Вариативная'];
  selectedGearboxTypes = this.gearboxTypes[0];

  bodyTypes = ['Седан', 'Хэтчбек', 'Универсал', 'Лифтбэк', 'Купе', 'Кабриолет', 'Родстер', 'Тарга',
   'Лимузин', 'Стретч', 'Внедорожник', 'Кроссовер', 'Пикап', 'Фургон', 'Минивэн', 'Микроавтобус', 'Автобус'];
  selectedBodyTypes = this.bodyTypes[0];

  constructor(private router: ActivatedRoute, private rout: Router, private carService: CarService, private location: Location) { }

  ngOnInit() {
    this.fieldArray = [];
    this.imagePreview = [];
    this.notIndicated = '';
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
    let i;
    this.carService.getUserCar(id, this.car).subscribe(res => {
    this.car = res['body'];

    for (i = 0; i < this.car.carPhotos.length; i++) {
      this.imagePreview.push({
     'url': 'data:image/jpg;base64,' + this.car.carPhotos[i],
     'item': this.car.carPhotos[i]
     });
    }

    if (this.car.location === '') {
      this.notIndicated = '(не указано)';
      this.init('');
    } else {
      this.notIndicated = '';
      this.coords = JSON.parse(this.car.location);
      ymaps.ready(this.init(this.coords));
    }

    this.selectedFuelTypes = this.car.fuelType;
    this.selectedGearboxTypes = this.car.gearboxType;
    this.selectedBodyTypes = this.car.bodyType;

      for (i = 0; i < this.car.calendar.length; i++) {
       this.fieldArray.push({
      'beginDate': this.car.calendar[i].beginDate,
      'countDays': this.car.calendar[i].countDays
      });
      }
    }, (err) => {
    this.message = err['message'];
    });
  }

  init(coords) { // for working with maps

    let myPlacemark, MyMap;
    MyMap = new ymaps.Map('MyMapCar', {
      center: [53.6884, 23.8258],
      zoom: 9
      }, {
          searchControlProvider: 'yandex#search'
    });

    // before editing
    if (coords !== '') {
      myPlacemark = createPlacemark(coords);
      MyMap.geoObjects.add(myPlacemark);
      getAddress(coords);
      localStorage.setItem('sel_coord', JSON.stringify(coords));
    }

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

  openBox(id) {
    const display = document.getElementById(id).style.display;
    if (display === 'none') {
        document.getElementById(id).style.display = 'block';
    } else {
       document.getElementById(id).style.display = 'none';
    }
    return false;
  }

  deleteFieldValue(index) {
    this.fieldArray.splice(index, 1);
  }

  deletePhoto(index) {
    this.imagePreview.splice(index, 1);
  }

  edit(id) {
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
      this.carService.putUserCar(id, this.car).subscribe(res => {
      this.colorMessage = true;
      this.message = res['message'];
        setTimeout(() => {
          this.rout.navigate(['/usercars']);
        },
        2000);
      }, (err) => {
        this.colorMessage = false;
        this.message = err['message'];
      });
    }
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
