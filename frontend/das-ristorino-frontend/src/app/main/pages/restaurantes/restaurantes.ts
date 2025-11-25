import { Component, inject, OnInit } from '@angular/core';
import { IRestaurante } from '../../api/models/i-restaurante';
import { Router, ActivatedRoute } from '@angular/router';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-restaurantes',
  imports: [],
  templateUrl: './restaurantes.html',
  styleUrls: ['./restaurantes.scss'],
})
export class RestaurantesPage implements OnInit {

  restaurantesLista: IRestaurante[] = [];

  private _route = inject(ActivatedRoute);
  private _router = inject(Router);

  ngOnInit(): void {
    // Leer restaurantes resueltos por el RestaurantesListResolver
    this._route.data.subscribe(data => {
      if (data && data['restaurantes']) {
        this.restaurantesLista = data['restaurantes'];
      } else {
        // Fallback: si no hay datos resueltos, podríamos cargar directamente (opcional)
        this.restaurantesLista = [];
      }
    });
  }

  formatearHorario(horario: string | null): string {
    if (!horario) return $localize`No disponible`;
    // Formato esperado: "HH:mm:ss" -> convertir a "HH:mm"
    const partes = horario.split(':');
    if (partes.length >= 2) {
      return `${partes[0]}:${partes[1]}`;
    }
    return horario;
  }

  obtenerEstrellas(calificacion: number): string {
    const estrellasLlenas = Math.floor(calificacion);
    const tieneMedia = calificacion % 1 >= 0.5;
    let resultado = '★'.repeat(estrellasLlenas);
    if (tieneMedia) {
      resultado += '½';
    }
    return resultado;
  }

  obtenerImagen(restaurante: IRestaurante): string | null {
    // Priorizar imagenes array, luego imagenUrl
    if (restaurante.imagenes && restaurante.imagenes.length > 0) {
      return restaurante.imagenes[0];
    }
    if (restaurante.imagenUrl) {
      return restaurante.imagenUrl;
    }
    return null;
  }

  redirigirADetalleRestaurante(restaurante: IRestaurante){
    this._router.navigate(['/restaurantes', restaurante.nroRestaurante])
  }
  
}
