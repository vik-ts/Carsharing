import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { AdminService} from '../services/admin.service';
import { Router } from '@angular/router';

declare var ymaps: any;

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
  imagePreview: any[];
  notIndicated: string;
  MyMap: any;

  constructor(private adminService: AdminService, private router: Router, private location: Location) { }

  ngOnInit() {
    this.imagePreview = [];
    this.notIndicated = '';
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
    let i;
    this.imagePreview = [];
    this.carDetail = carItem;
    for (i = 0; i < this.carDetail['carPhotos'].length; i++) {
      this.imagePreview.push({
     'url': 'data:image/jpg;base64,' + this.carDetail['carPhotos'][i]
     });
    }

    if (this.carDetail['location'] === '') {
      this.notIndicated = '(не указано)';
      this.init('');
    } else {
      this.notIndicated = '';
      ymaps.ready(this.init(JSON.parse(this.carDetail['location'])));
    }
    return this.show('block');
  }

  init(coords) {
    let myPlacemark;
    this.MyMap = new ymaps.Map('MyMapCar', {
      center: [53.6884, 23.8258],
      zoom: 9
    }, {
          searchControlProvider: 'yandex#search'
    });

    if (coords !== '') {
      myPlacemark = createPlacemark(coords);
      this.MyMap.geoObjects.add(myPlacemark);
      getAddress(coords);
    }

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

  show(state) {
    if (state === 'none') {
      this.MyMap.destroy();
    }
    document.getElementById('window').style.display = state;
    return false;
  }

  openBox(box) {
    const display = document.getElementById(box).style.display;
    if (display === 'none') {
        document.getElementById(box).style.display = 'block';
    } else {
       document.getElementById(box).style.display = 'none';
    }
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
