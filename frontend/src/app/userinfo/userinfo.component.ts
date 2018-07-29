import { Component, OnInit } from '@angular/core';
import { UserService} from '../services/user.service';
import { ActivatedRoute } from '@angular/router';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

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

  constructor(private userService: UserService, private router: Router, private route: ActivatedRoute) { }

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    this.getUserInfo(); // ['id']);
  }

  getUserInfo() {
   this.userService.getUserInfoByUser(this.id).subscribe(res => {
   this.userinfo = res['body'];
   }, (err) => {
    this.message = 'Произошла ошибка. Личный кабинет не найден.';
   });
  }

  editUserInfo() {
    this.userService.putUserInfoByUser(this.id, this.userinfo).subscribe(res => {
    this.message = 'Информация обновлена!';
    });
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

  delete() {
    this.userService.deleteUser(this.id).subscribe(res => {
    this.message = 'Пользователь удален!';
    setTimeout(() => {
      this.router.navigate(['/home']);
      } ,
      500);
    });
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
  // очистить фото
  // удалить юзера
  // сменить пароль, почту

}
