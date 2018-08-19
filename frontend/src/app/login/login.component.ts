import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {

  user = {};
  message = '';
  colorMessage: boolean;  // to set the color of the message

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
  }

  findUser() {

    this.auth.doLogin(this.user)
      .subscribe(res => {
        this.colorMessage = true;
        this.message = res['message'];
        setTimeout(() => {
          this.router.navigate(['/homeuser']);
        },
        1000);
        }, (err) => {
        this.colorMessage = false;
        if (err['status'] === 404) {
          this.message = 'Пользователь с данным email не найден. Зарегистрируйтесь.';
          setTimeout(() => {
            this.router.navigate(['/signup']);
          },
          2000);
        } else {
          this.message = 'Не удалось войти в систему. Повторите попытку.';
         }
        }
      );
  }
}

