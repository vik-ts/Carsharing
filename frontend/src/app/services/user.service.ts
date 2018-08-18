import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

@Injectable()
export class UserService {

constructor(private http: HttpClient) {}

getUserInfoByUser(id) {
  return this.http.get('/userinfo/' + id).map(
    (res: Response) => {
      return res;
    }
  );
}

putUserInfoByUser(id, userinfo) {
  return this.http.put('/userinfo/' + id, userinfo).map(
    (res: Response) => {
      return res;
    }
  );
}

deleteUser(id) {
  return this.http.delete('/user/' + id).map(
    (res: Response) => {
      return res;
    }
  );
}
}
