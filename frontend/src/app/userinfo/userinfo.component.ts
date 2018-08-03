import { Component, OnInit } from '@angular/core';
import { UserService} from '../services/user.service';
import { ActivatedRoute } from '@angular/router';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-userinfo',
  templateUrl: './userinfo.component.html',
  styleUrls: ['./userinfo.component.css']
})
export class UserinfoComponent implements OnInit {

  user = {};
  userinfo = {};
  message = '';
  reader = new FileReader();
  form: FormGroup;
  file: any;
  url: any;
  id: any;
  email: string;
  password: string;
  userUpdate = {};

  constructor(private auth: AuthService, private userService: UserService, private location: Location) { }

  ngOnInit() {
    this.id = this.auth.id;
    this.email = this.auth.email;
    this.password = '';
    this.getUserInfo();
  }

  backClicked() {
    this.location.back();
  }

  openBox(id) {
    const display = document.getElementById(id).style.display;
    if (display === 'none') {
        document.getElementById(id).style.display = 'block';
    } else {
       document.getElementById(id).style.display = 'none';
    }
    return false;
  }

  onFileChange(event) {
    /*
    if (event.target.files && event.target.files.length > 0) {
      this.file = event.target.files[0];
      this.reader.readAsDataURL(this.file);
      this.reader.onload = (onLoadPhotoEvent: any) => {
        this.url = onLoadPhotoEvent.target.result;
        this.form.get('userinfo.photo').setValue({
          filename: this.file.name,
          filetype: this.file.type,
          value: this.reader.result.split(',')[1]
        });
      };
    }*/
    }

    clearFile() { /*
    this.form.get('userinfo.photo').setValue(null);
    this.file.nativeElement.value = '';*/
    }


  getUserInfo() {
   this.userService.getUserInfoByUser(this.id).subscribe(res => {
   this.userinfo = res['body'];
   }, (err) => {
    this.message = 'Произошла ошибка. Личный кабинет не найден.';
   });
  }

  editUserInfo() {
    if ((this.email !== this.auth.email) || (this.password !== '')) {
      this.userUpdate = {'email': this.email, 'password': this.password};
      this.auth.putUser(this.id, this.userUpdate).subscribe(res => {
        this.putUserInfo();
      }, (err: any) => {
        if ((err.status === 404) || (err.status === 409)) {
          this.message = err.error['message'];
        } else {
          this.message = err['message'];
        }
      });
    } else {
     this.putUserInfo();
    }
  }

  putUserInfo () {
    this.userService.putUserInfoByUser(this.id, this.userinfo).subscribe(res => {
    this.message = 'Информация обновлена!';
    }, (err) => {
    this.message = err['message'];
    });
  }

  delete() {
    if (confirm('Вы действительно хотите удалить свою учетную запись?')) {
      this.userService.deleteUser(this.id).subscribe(res => {
        this.message = 'Пользователь удален!';
        setTimeout(() => {
          this.auth.doLogout();
          } ,
          500);
        });
    }
  }
}
