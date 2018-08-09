import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

@Injectable()
export class CarBookingService {

constructor(private http: HttpClient) {}

createBooking(params) {
  return this.http.post('/booking/', params).map(
  (res: Response) => {
      return res;
  }
  );
}

}
