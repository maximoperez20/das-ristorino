import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Prueba {

  nombre?: string;

  setNombre(nombre: string) {
    this.nombre = nombre;
  }

  getNombre(): string {
    return this.nombre || 'Sin nombre';
  }


  constructor() { }

  
}
