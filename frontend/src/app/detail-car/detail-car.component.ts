import { Component, OnInit } from '@angular/core';
import { AdminService } from '../services/admin.service';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-detail-car',
  templateUrl: './detail-car.component.html',
  styleUrls: ['./detail-car.component.css']
})
export class DetailCarComponent implements OnInit {

  car: {};
  message: '';

  constructor(private adminService: AdminService, private router: ActivatedRoute) { }

  ngOnInit() {
    // this.getDetailCar(this.router.snapshot.params.id); // id Car
  }

}

