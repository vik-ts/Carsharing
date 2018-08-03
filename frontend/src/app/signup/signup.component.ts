import { Component, OnInit } from '@angular/core';
import { User} from '../models/user';
import { AuthService} from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  user = new User;
  message = '';
  colorMessage: boolean;

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
  }

  saveUser() {
    this.authService.registerUser(this.user.email).subscribe(res => {
      this.colorMessage = true;   // to set the color of the message
      this.show('block');
      this.message = '';
      },
       (err: any) => {
        this.colorMessage = false;
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
