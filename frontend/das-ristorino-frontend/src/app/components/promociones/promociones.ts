import { Component, OnInit } from '@angular/core';
import { RouterLink } from "@angular/router";
import { PromocionService } from '../../services/promocion-service';
import { DatePipe } from '@angular/common';
import { IPromocion } from '../../api/models/i-promocion';

@Component({
  selector: 'app-promociones',
  imports: [DatePipe, RouterLink],
  templateUrl: './promociones.html',
  styleUrls: ['./promociones.scss'],
})
export class PromocionesComponent implements OnInit {

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
    
    registrarClickPromocion(promocion: IPromocion) {
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
