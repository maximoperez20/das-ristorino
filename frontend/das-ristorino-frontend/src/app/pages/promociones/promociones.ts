import { Component, OnInit } from '@angular/core';
import { PromocionService } from '../../services/promocion-service';
import { CommonModule} from '@angular/common';
import { IPromocion } from '../../api/models/i-promocion';
import { PromocionComponent } from '../../components/promocion/promocion';

@Component({
  selector: 'app-promociones',
  imports: [CommonModule, PromocionComponent],
  templateUrl: './promociones.html',
  styleUrls: ['./promociones.scss'],
})
export class PromocionesPage implements OnInit {

  constructor(private _promocionService: PromocionService) { }
  
    promocionesLista: IPromocion[] = [];
    agrupadas: IPromocion[][] = [];
    
    ngOnInit(): void {
      this.cargarPromociones();
    }

    // cargarPromociones(){
    //   this._promocionService.obtenerPromociones().subscribe({
    //     next: (data) => {
    //       // Aquí puedes manejar los datos recibidos
    //       console.log(data);
    //       this.promocionesLista = data;
    //     },
    //     error: (error) => {
    //       // Aquí puedes manejar errores
    //       console.error('Error al obtener promociones:', error);
    //     }
    //   });
    // }

    cargarPromociones() {
  this._promocionService.obtenerPromociones().subscribe({
    next: (data) => {
      this.promocionesLista = data;
      this.agrupadas = [];
      for (let i = 0; i < data.length; i += 3) {
        this.agrupadas.push(data.slice(i, i + 3));
      }
    },
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
