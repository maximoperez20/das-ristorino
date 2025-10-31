import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ComponenteHijo } from './componente-hijo/componente-hijo';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ComponenteHijo ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {

  reciboMensaje: string = '';

  recibirMensaje($event: string) {
    this.reciboMensaje = $event;
  }




//Contador
  valorNumerico: number= 0;
  incrementar() {
    this.valorNumerico++;
  }

  decrementar(){
    this.valorNumerico--;
  }


  
}
