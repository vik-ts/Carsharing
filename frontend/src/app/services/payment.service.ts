import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

@Injectable()
export class PaymentService {

constructor(private http: HttpClient) {}

// return the car + create payment
returnCar(id) { // id booking
  return this.http.post('/returncar/' + id, null).map(  // date we take server, so we pass null
  (res: Response) => {
      return res;
  }
  );
}

getConfirmPayments(id) { // id user
  return this.http.get('/confirmpayment/' + id).map(
    (res: Response) => {
      return res;
  }
  );
}

putConfirmPayments(PaymentsArray) {
  return this.http.put('/confirmpayment', PaymentsArray).map(
      (res) => {
        return res;
  }
  );
}

getUnclosedPayments(id) { // id user
  return this.http.get('/activepayment/' + id).map(
    (res: Response) => {
      return res;
  }
  );
}

putUnclosedPayments(PaymentsArray) {
  return this.http.put('/activepayment', PaymentsArray).map(
    (res) => {
      return res;
  }
  );
}

getAllPayments(id) { // id user
  return this.http.get('/allpayments/' + id).map(
    (res: Response) => {
      return res;
  }
  );
}
}
