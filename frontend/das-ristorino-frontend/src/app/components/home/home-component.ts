import { Component } from '@angular/core';
import { CarruselComponent } from "../carrusel-component/carrusel-component";

@Component({
  selector: 'app-home',
  imports: [CarruselComponent],
  templateUrl: './home-component.html',
  styleUrl: './home-component.scss',
})
export class HomeComponent {

}
