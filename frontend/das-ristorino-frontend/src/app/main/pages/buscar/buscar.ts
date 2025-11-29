import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RestauranteResource } from '../../api/resources/restaurante-resource';
import { IRestaurante } from '../../api/models/i-restaurante';
import { BuscadorNLPComponent } from '../../components/buscador-nlp/buscador-nlp';

@Component({
  selector: 'app-buscar',
  standalone: true,
  imports: [CommonModule, BuscadorNLPComponent],
  templateUrl: './buscar.html',
  styleUrls: ['./buscar.scss'],
})
export class BuscarPage implements OnInit {

  consulta: string = '';
  resultadosExactos: IRestaurante[] = [];
  sugerencias: IRestaurante[] = [];
  cargando = false;
  error: string | null = null;

  private _route = inject(ActivatedRoute);
  private _router = inject(Router);
  private _restauranteResource = inject(RestauranteResource);

  ngOnInit(): void {
    // Leer consulta de query params
    this._route.queryParams.subscribe(params => {
      const query = params['q'];
      if (query && query.trim()) {
        this.consulta = query.trim();
        this.buscarRestaurantes(this.consulta);
      }
    });
  }

  buscarRestaurantes(consulta: string): void {
    if (!consulta || !consulta.trim()) {
      this.error = 'Por favor ingresa una búsqueda';
      return;
    }

    this.cargando = true;
    this.error = null;
    this.resultadosExactos = [];
    this.sugerencias = [];

    this._restauranteResource.buscarRestaurantesPorNLP({ consulta: consulta.trim() }).subscribe({
      next: (resultado) => {
        this.resultadosExactos = resultado?.resultadosExactos || [];
        this.sugerencias = resultado?.sugerencias || [];
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error en búsqueda NLP:', err);
        this.error = err.error?.error || 'Error al realizar la búsqueda. Por favor intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  obtenerImagen(restaurante: IRestaurante): string {
    if (restaurante.imagenes && restaurante.imagenes.length > 0) {
      return restaurante.imagenes[0];
    }
    if (restaurante.imagenUrl) {
      return restaurante.imagenUrl;
    }
    return 'https://picsum.photos/seed/food/400/300';
  }

  navegarADetalle(nroRestaurante: string): void {
    this._router.navigate(['/restaurantes', nroRestaurante]);
  }

}

