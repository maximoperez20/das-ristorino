import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRestaurante } from '../../api/models/i-restaurante';
import { ISucursal } from '../../api/models/i-sucursal';
import { NgClass } from '@angular/common';
import { HorariosDisponiblesComponent } from '../horarios-disponibles/horarios-disponibles';
import { PromocionComponent } from "../promocion/promocion";

@Component({
  selector: 'app-detalle-restaurante',
  standalone: true,
  imports: [NgClass, HorariosDisponiblesComponent, PromocionComponent],
  templateUrl: './detalle-restaurante.html',
  styleUrls: ['./detalle-restaurante.scss'],
})
export class DetalleRestauranteComponent implements OnInit {

  restaurante?: IRestaurante | undefined;
  sucursalSeleccionada?: ISucursal;
  fechaSeleccionada: Date = new Date();
  nroRestaurante: string = '';

  private _route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.nroRestaurante = this._route.snapshot.paramMap.get('nroRestaurante') || '';
    
    this._route.data.subscribe(data => {
      this.restaurante = data?.['restaurante'];
      if (this.restaurante && !this.restaurante.nroRestaurante) {
        this.restaurante.nroRestaurante = this.nroRestaurante;
      }
      this.seleccionarPrimeraSucursal();
      console.log('Restaurante cargado:', this.restaurante);
    });
  }

  seleccionarPrimeraSucursal(): void {
    if (this.restaurante?.sucursales && this.restaurante.sucursales.length > 0) {
      this.sucursalSeleccionada = this.restaurante.sucursales[0];
      console.log('Sucursal seleccionada:', this.sucursalSeleccionada);
      console.log('nroRestaurante:', this.nroRestaurante);
    }
  }

  seleccionarSucursal(sucursal: ISucursal): void {
    this.sucursalSeleccionada = sucursal;
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
    
    if (this.restaurante.imagenes && this.restaurante.imagenes.length > 0) {
      return this.restaurante.imagenes[0];
    }
    if (this.restaurante.imagenUrl) {
      return this.restaurante.imagenUrl;
    }
    return 'https://picsum.photos/seed/food/800/400';
  }

  esSucursalSeleccionada(sucursal: ISucursal): boolean {
    return this.sucursalSeleccionada?.nroSucursal === sucursal.nroSucursal;
  }

}
