import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

@Injectable()
export class CarService {

constructor(private http: HttpClient) {}

createAdCar(id, car) {
  return this.http.post('/car/' + id, car).map(
  (res: Response) => {
      return res;
  }
  );
}

getUserCars(id) {
  return this.http.get('/usercars/' + id).map(
    (res: Response) => {
      return res;
  }
  );
}

getUserCar(idCar, car) {
  return this.http.get('/car/' + idCar, car).map(
    (res) => {
      return res;
  }
  );
}

getSearchUsersCars(str) {
  return this.http.get('/searchcars' + str).map(
      (res: Response) => {
        return res;
  }
  );
}

putUserCar(idCar, car) {
  return this.http.put('/car/' + idCar, car).map(
    (res: Response) => {
      return res;
  }
  );
}

deleteCar(idCar) {
  return this.http.delete('/car/' + idCar).map(
    (res: Response) => {
      return res;
  }
  );
}
}
