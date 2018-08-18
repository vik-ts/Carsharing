import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { CarService} from '../services/car.service';
import { Location } from '@angular/common';
import { Router } from '@angular/router';

declare var ymaps: any;

@Component({
  selector: 'app-search-car',
  templateUrl: './search-car.component.html',
  styleUrls: ['./search-car.component.css']
})
export class SearchCarComponent implements OnInit {
  cars: {};
  message: '';
  selectedFuelTypes: any;
  selectedGearboxTypes = '';
  selectedBodyTypes = '';
  id: any;
  currentPage; totalPages; pageSize: number;
  pages = []; // for pagination
  carDetail = {}; // for opening a car card

  mark; model; gearboxType; bodyType; drive; engineType; fuelType: string;
  lowYearIssued; highYearIssued; lowMileage; highMileage; seatsNumber; lowPrice; highPrice; rentCountDays: number;
  rentBeginDate: Date;
  imagePreview: any[];
  notIndicated: string;
  MyMap: any;

  // array for sorting
  sort = [{'column': 0, 'name': 'mark', 'count': 1, 'view': ''},
   {'column': 1, 'name': 'model', 'count': 1, 'view': ''},
   {'column': 2, 'name': 'yearIssued', 'count': 1, 'view': ''},
   {'column': 3, 'name': 'mileage', 'count': 1, 'view': ''},
   {'column': 4, 'name': 'seatsNumber', 'count': 1, 'view': ''},
   {'column': 5, 'name': 'gearboxType', 'count': 1, 'view': ''},
   {'column': 6, 'name': 'bodyType', 'count': 1, 'view': ''},
   {'column': 7, 'name': 'drive', 'count': 1, 'view': ''},
   {'column': 8, 'name': 'engineType', 'count': 1, 'view': ''},
   {'column': 9, 'name': 'fuelType', 'count': 1, 'view': ''},
   {'column': 10, 'name': 'price', 'count': 1, 'view': ''}];

  fuelTypes = ['не выбран', 'Бензин', 'Дизель', 'Газ'];
  gearboxTypes = ['не выбран', 'Механическая', 'Автоматическая', 'Роботизированная', 'Вариативная'];
  bodyTypes = ['не выбран', 'Седан', 'Хэтчбек', 'Универсал', 'Лифтбэк', 'Купе', 'Кабриолет', 'Родстер', 'Тарга',
   'Лимузин', 'Стретч', 'Внедорожник', 'Кроссовер', 'Пикап', 'Фургон', 'Минивэн', 'Микроавтобус', 'Автобус'];

  constructor(private auth: AuthService, private carService: CarService, private router: Router, private location: Location) { }

  ngOnInit() {
    this.id = this.auth.id;
    this.selectedFuelTypes = this.fuelTypes[0];
    this.selectedGearboxTypes = this.gearboxTypes[0];
    this.selectedBodyTypes = this.bodyTypes[0];

    this.mark = ''; this.model = '';
    this.drive = ''; this.engineType = '';
    this.lowYearIssued = null; this.highYearIssued = null; this.lowMileage = null; this.highMileage = null;
    this.seatsNumber = null; this.lowPrice = null; this.highPrice = null;
    this.rentBeginDate = null; this.rentCountDays = null;

    this.totalPages = 1;
    this.currentPage = 1;
    this.pageSize = 5;
    this.imagePreview = [];
    this.notIndicated = '';
    this.getCars();
  }

  getCars() {
    let str, i , strFilter;
    strFilter = '';

    // check or apply a filter
    if (this.mark !== '') {
      strFilter = strFilter + 'mark=' + this.mark + '&';
    }
    if (this.model !== '') {
      strFilter = strFilter + 'model=' + this.model + '&';
    }
    if (this.lowYearIssued !== null) {
      strFilter = strFilter + 'lowYearIssued=' + this.lowYearIssued + '&';
    }
    if (this.highYearIssued !== null) {
      strFilter = strFilter + 'highYearIssued=' + this.highYearIssued + '&';
    }
    if (this.lowMileage !== null) {
      strFilter = strFilter + 'lowMileage=' + this.lowMileage + '&';
    }
    if (this.highMileage !== null) {
      strFilter = strFilter + 'highMileage=' + this.highMileage + '&';
    }
    if (this.seatsNumber !== null) {
      strFilter = strFilter + 'seatsNumber=' + this.seatsNumber + '&';
    }
    if (this.selectedGearboxTypes !== 'не выбран') {
      strFilter = strFilter + 'gearboxType=' + this.selectedGearboxTypes + '&';
    }
    if (this.selectedBodyTypes !== 'не выбран') {
      strFilter = strFilter + 'bodyType=' + this.selectedBodyTypes + '&';
    }
    if (this.drive !== '') {
      strFilter = strFilter + 'drive=' + this.drive + '&';
    }
    if (this.engineType !== '') {
      strFilter = strFilter + 'engineType=' + this.engineType + '&';
    }
    if (this.selectedFuelTypes !== 'не выбран') {
      strFilter = strFilter + 'fuelType=' + this.selectedFuelTypes + '&';
    }
    if (this.lowPrice !== null) {
      strFilter = strFilter + 'lowPrice=' + this.lowPrice + '&';
    }
    if (this.highPrice !== null) {
      strFilter = strFilter + 'highPrice=' + this.highPrice + '&';
    }
    if ((this.rentBeginDate !== null) && (this.rentCountDays !== null)) {
      let s, myDate;
      s = this.rentBeginDate;
      myDate = new Date(s.valueOf().replace(/-/g, '/').replace('T', ' '));
      myDate = new Date(myDate.getTime() - (myDate.getTimezoneOffset() * 60000)).toJSON();
      strFilter = strFilter + 'rentBeginDate=' + myDate + '&' + 'rentCountDays=' + this.rentCountDays + '&';
    }

    // form the query string
    str = '?' + strFilter + 'page=' + (this.currentPage - 1) + '&size=' + this.pageSize;
    // we check the array with sorting
    // we use sorting in ascending order (if count is 2) or descending (if count is 3)
    for (i = 0; i <= this.sort.length - 1; i++) {
      if (this.sort[i].count === 2) {
        str = str + '&sort=' + this.sort[i].name + ',asc';
      } else if (this.sort[i].count === 3) {
        str = str + '&sort=' + this.sort[i].name + ',desc';
      }
    }

    this.carService.getSearchUsersCars(str).subscribe(res => {
    this.cars = res['body']['content'];
    this.totalPages = res['body']['totalPages'];
    this.getPages();
    }, (err) => {
      this.message = err['message'];
    });
  }

 // for pagination
  getPages() {
    this.pages.splice(0, this.pages.length);
    let num;
    for (num = 1; num <= this.totalPages; num++) {
      this.pages.push({'num': num});
    }
  }

  clickSort(column) {
    let element;
    element = this.sort.find(x => x.column === column);
    element.count = element.count + 1;
    if (element.count > 3) {
      element.count = 1;
    }
    if (element.count === 1) { element.view = '';   // do not sort
    } else if (element.count === 2) { element.view = '↑';   // sort ascending
    } else { element.view = '↓'; }   // sort in descending order
    this.getCars();
  }

  // sets the current page
  setPage(number) {
   if (number === 0) {
      number = 1;
    } else if (number === this.totalPages + 1)  {
      number = this.totalPages;
    }
    this.currentPage = number;
    this.getCars();
  }

  Filter() {
    document.getElementById('box').style.display = 'none';
    this.currentPage = 1;
    this.getCars();
  }

  getUrl(car) {
    let url;
    if (car.carPhotos.length > 0) {
      url = 'data:image/jpg;base64,' + car.carPhotos[0];
    } else {
      url = null;
    }
     return url;
   }

 // open car card
  openCard(carItem) {
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

  FuelTypesChanged(selectedFuelTypes) {
    this.selectedFuelTypes = selectedFuelTypes;
  }

  GearboxTypesChanged(selectedGearboxTypes) {
    this.selectedGearboxTypes = selectedGearboxTypes;
  }

  BodyTypesChanged(selectedBodyTypes) {
    this.selectedBodyTypes = selectedBodyTypes;
  }

  backClicked() {
    this.location.back();
  }

  goToBooking(itemCar) {
    this.router.navigate(['carbooking/' + itemCar.id], {queryParams: {mark: itemCar.mark } });
  }
}
