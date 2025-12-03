import { Component, inject, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IResena } from '../../api/models/i-resena';
import { ResenaResource } from '../../api/resources/resena-resource';
import { IResenaInsertar } from '../../api/models/i-resena-insertar';

@Component({
  selector: 'app-resena',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './resena.html',
  styleUrl: './resena.scss',
})
export class ResenaComponent implements OnChanges {
@Input() nroRestaurante?: string;
  @Input() nroSucursal?: string;

  resenas: IResena[] = [];
  cargandoResenas = false;
  
  private _resenaResource = inject(ResenaResource);

  ngOnChanges(changes: SimpleChanges): void {
    // Detectar cuando cambian los inputs y recargar reseñas
    if ((changes['nroRestaurante'] || changes['nroSucursal']) && 
        this.nroRestaurante && this.nroSucursal) {
      this.cargarResenas();
    }
  }

  cargarResenas(): void {
    if (!this.nroRestaurante || !this.nroSucursal) return;

    this.cargandoResenas = true;
    this._resenaResource.obtenerResenas({
      nroRestaurante: this.nroRestaurante,
      nroSucursal: this.nroSucursal
    }).subscribe({
      next: (resenas) => {
        this.resenas = resenas;
        this.cargandoResenas = false;
      },
      error: (err) => {
        console.error('Error al cargar reseñas', err);
        this.cargandoResenas = false;
      }
    });
  }

  agregarResena(datos: IResenaInsertar): void {
    this._resenaResource.insertarResena(datos).subscribe({
      next: () => {
        this.cargarResenas(); // Recargar lista
      },
      error: (err) => console.error('Error al insertar reseña', err)
    });
  }
}
