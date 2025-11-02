import { Component, OnInit } from '@angular/core';
import { RouterLink } from "@angular/router";
import { PromocionService } from '../promocion-service';
import { DatePipe } from '@angular/common';
import { Promocion } from '../data/promociones.mock';

@Component({
  selector: 'app-carrusel-component',
  imports: [RouterLink, DatePipe],
  templateUrl: './carrusel-component.html',
  styleUrl: './carrusel-component.scss',
})
export class CarruselComponent implements OnInit {

  constructor(private _promocionService: PromocionService) { }

  promocionesLista: any[] = [];
  ngOnInit(): void {
    this._promocionService.obtenerPromociones().subscribe({
      next: (data) => {
        // Aquí puedes manejar los datos recibidos
        console.log(data);
        this.promocionesLista = data;
      },
      error: (error) => {
        // Aquí puedes manejar errores
        console.error('Error al obtener promociones:', error);
      }
    });
  }

  registrarClickPromocion(promocion: Promocion) {
  this._promocionService
    .registrarClickPromocion(
      promocion.nroRestaurante,
      promocion.nroIdioma,
      promocion.nroContenido
    )
    .subscribe({
      next: () => console.log('Click registrado correctamente'),
      error: (err) => console.error('Error registrando click', err)
    });
}
}
