import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { JwtHelper } from 'angular2-jwt';

import 'rxjs/add/operator/map';

@Injectable()
export class AuthService {

  loggedIn = false;
  id: Number;
  email: string;
  isAdmin = false;
  token: string;

  jwtHelper: JwtHelper = new JwtHelper();

  constructor(private http: HttpClient, private router: Router) {
    const body = JSON.parse(localStorage.getItem('body'));
    if (body) {
      this.loggedIn = true;
      this.id = body['idUser'];
      this.isAdmin =  body['isAdmin'];
      this.token = body['token'];
      this.email = body['email'];
      this.router.navigate(['/homeuser']);
    }
  }

  registerUser(email) {
    return this.http.post('/registration', email).map(
    (res: Response) => {
        return res;
    }
    );
  }

  doLogin(user) {
    return this.http.post('/login', user).map(res => {
      localStorage.setItem('body', JSON.stringify(res['body']));
      this.loggedIn = true;
      this.id = res['body'].idUser;
      this.isAdmin = res['body'].isAdmin;
      this.token = res['body'].token;
      this.email = res['body'].email;
      return res;
    });
  }

  doLogout() {
    this.loggedIn = false;
    this.token = '';
    localStorage.removeItem('body');
    this.router.navigate(['/home']);
  }

  mustLogin() {
    localStorage.removeItem('body');
    this.router.navigate(['/login']);
  }

  getToken() {
    return this.token;
  }

  putUser (id, userUpdate) {
    return this.http.put('/login/' + id, userUpdate).map(
      (res: Response) => {
        return res;
    }
    );
  }
}
