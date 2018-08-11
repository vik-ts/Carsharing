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

getConfirmBookings(id) {
  return this.http.get('/confirmbooking/' + id).map(
    (res: Response) => {
      return res;
  }
  );
}

putConfirmBookings(BookingsArray) {
  return this.http.put('/confirmbooking', BookingsArray).map(
      (res) => {
        return res;
  }
  );
}

getUserBookings(id) {
  return this.http.get('/userbookings/' + id).map(
    (res: Response) => {
      return res;
  }
  );
}

getCarUserBookings(id) {
  return this.http.get('/caruserbookings/' + id).map(
    (res: Response) => {
      return res;
  }
  );
}

}
