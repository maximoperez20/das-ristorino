import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoaderIcon } from "./core/layouts/loader/loader-icon";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, LoaderIcon],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {

  
  
}
