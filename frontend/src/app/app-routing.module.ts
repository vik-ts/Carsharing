import { HomeComponent } from './home/home.component';
import { SignupComponent} from './signup/signup.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  // {path: '', redirectTo: 'user', pathMatch: 'full'},
  {path: 'home', component: HomeComponent},
  {path: 'signup', component: SignupComponent},
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  // {path: 'findbylastname', component: SearchCustomersComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {}
