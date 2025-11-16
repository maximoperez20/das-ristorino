import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRestaurante } from '../../api/models/i-restaurante';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-detalle-restaurante',
  standalone: true,
  imports: [NgClass],
  templateUrl: './detalle-restaurante.html',
  styleUrls: ['./detalle-restaurante.scss'],
})
export class DetalleRestauranteComponent implements OnInit {

  restaurante?: IRestaurante | undefined;
  private _route = inject(ActivatedRoute);

  ngOnInit(): void {
    // Leer el restaurante resuelto por el RestauranteResolver (route.data.restaurante)
    this._route.data.subscribe(data => {
      this.restaurante = data?.['restaurante'];
      console.log('Restaurante resuelto en componente:', this.restaurante);
    });
  }

  formatearHorario(horario: string | null): string {
    if (!horario) return 'No disponible';
    const partes = horario.split(':');
    if (partes.length >= 2) {
      return `${partes[0]}:${partes[1]}`;
    }
    return horario;
  }

  obtenerImagenPrincipal(): string {
    if (!this.restaurante) return 'https://picsum.photos/seed/food/800/400';
    
    // Priorizar imagenes array, luego imagenUrl, luego placeholder
    if (this.restaurante.imagenes && this.restaurante.imagenes.length > 0) {
      return this.restaurante.imagenes[0];
    }
    if (this.restaurante.imagenUrl) {
      return this.restaurante.imagenUrl;
    }
    return 'https://picsum.photos/seed/food/800/400';
  }

}

