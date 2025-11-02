import { Component, OnInit } from '@angular/core';
import { RouterLink } from "@angular/router";
import { PromocionService } from '../promocion-service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-carrusel-component',
  imports: [RouterLink, DatePipe],
  templateUrl: './carrusel-component.html',
  styleUrl: './carrusel-component.scss',
})
export class CarruselComponent implements OnInit {

  constructor(private promocionService: PromocionService) { }

  promocionesLista: any[] = [];
  ngOnInit(): void {
    this.promocionService.obtenerPromociones().subscribe({
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

}
