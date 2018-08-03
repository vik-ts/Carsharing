import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

@Injectable()
export class AdminService {

constructor(private http: HttpClient) {}

getInactiveCars() {
    return this.http.get('/inactivecars').map(
        (res: Response) => {
          return res;
    }
    );
}

putInactiveCars(CarsArray) {
    return this.http.put('/inactivecars', CarsArray).map(
        (res) => {
          return res;
    }
    );
}
}
