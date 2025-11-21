import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { HeaderComponent } from '../../components/header/header';  

@Component({
  selector: 'app-home',
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './home.html',
  styleUrls: ['./home.scss'],
})
export class HomePage {

}
