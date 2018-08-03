import { HomeComponent } from './home/home.component';
import { SignupComponent} from './signup/signup.component';
import { UserinfoComponent } from './userinfo/userinfo.component';
import { LoginComponent } from './login/login.component';
import { CreateCarComponent } from './create-car/create-car.component';
import { ActivationComponent } from './activation/activation.component';
import { HomeUserComponent } from './home-user/home-user.component';
import { UserCarsComponent } from './user-cars/user-cars.component';
import { EditCarComponent } from './edit-car/edit-car.component';
import { DetailCarComponent } from './detail-car/detail-car.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: 'signup', component: SignupComponent},
  {path: 'userinfo', component: UserinfoComponent},
  {path: 'login', component: LoginComponent},
  {path: 'createcar', component: CreateCarComponent},
  {path: 'activation', component: ActivationComponent},
  {path: 'homeuser', component: HomeUserComponent},
  {path: 'usercars', component: UserCarsComponent},
  {path: 'editcar/:id', component: EditCarComponent},
  {path: 'detailcar/:id', component: DetailCarComponent},
  {path: '', redirectTo: '/home', pathMatch: 'full'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes,
  { enableTracing: true })],
  exports: [RouterModule]
})

export class AppRoutingModule {}
