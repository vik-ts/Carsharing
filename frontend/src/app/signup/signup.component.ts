import { Component, OnInit } from '@angular/core';
import { User} from '../models/user';
import { UserService} from '../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  user = new User;
  message = '';
  success: boolean;

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit() {
  }

  saveUser() {

    this.userService.createUser(this.user.email).subscribe(res => {
    this.success = res['success'];   // to set the color of the message
    if (this.success) {
        this.show('block');
        this.message = '';
      } else {
        this.message = res['message'];
    }
    },
    (err: any) => {
      this.success = false;
      try {
        this.message = err.error['message'];
      } catch (ce) {
        this.message = err['message'];
      }
    }
  );
  }

  show(state) {
    document.getElementById('window').style.display = state;

    if (state === 'none') {
      setTimeout(() => {
        this.router.navigate(['/home']);
        } ,
        1000);
    }
    return false;
  }
}
