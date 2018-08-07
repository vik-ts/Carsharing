import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { UserService } from './services/user.service';
import { CarService } from './services/car.service';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { SignupComponent } from './signup/signup.component';
import { UserinfoComponent } from './userinfo/userinfo.component';
import { LoginComponent } from './login/login.component';
import { CreateCarComponent } from './create-car/create-car.component';
import { AuthService } from './services/auth.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './auth/token.interceptor';
import { ActivationComponent } from './activation/activation.component';
import { HomeUserComponent } from './home-user/home-user.component';
import { UserCarsComponent } from './user-cars/user-cars.component';
import { EditCarComponent } from './edit-car/edit-car.component';
import { AdminService } from './services/admin.service';
import { SearchCarComponent } from './search-car/search-car.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    SignupComponent,
    UserinfoComponent,
    LoginComponent,
    CreateCarComponent,
    ActivationComponent,
    HomeUserComponent,
    UserCarsComponent,
    EditCarComponent,
    SearchCarComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [UserService, CarService, AdminService,
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
