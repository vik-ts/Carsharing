import { Component, OnInit } from '@angular/core';
import { UserService} from '../services/user.service';
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
  selectedFile: File;
  imagePreview: string;
  id: any;
  email: string;
  password: string;
  userUpdate = {};

  constructor(private auth: AuthService, private userService: UserService, private location: Location) { }

  ngOnInit() {
    this.id = this.auth.id;
    this.email = this.auth.email;
    this.password = '';
    this.imagePreview = '';
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

  onFileUpload (event) {
    this.selectedFile = event.target.files[0];
    this.reader = new FileReader();
    this.reader.onload = () => {
      this.imagePreview = 'data:image/jpg;base64,' + this.reader.result.split(',')[1];
    };
    this.reader.readAsDataURL(this.selectedFile);
  }

  getUserInfo() {
   this.userService.getUserInfoByUser(this.id).subscribe(res => {
    this.userinfo = res['body'];
    this.imagePreview = 'data:image/jpg;base64,' + this.userinfo['photo'];
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
    if (this.reader.result !== null) {
      this.userinfo['photo'] = this.reader.result.split(',')[1];
    }
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
