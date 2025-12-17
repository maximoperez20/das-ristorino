// resenas.component.ts (extracto)
import { Component, Input, OnInit, OnChanges, SimpleChanges, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DateUtilsService } from '../../../core/services/date-utils.service';
import { IResena } from '../../api/models/i-resenas';
import { ResenasResource } from '../../api/resources/resenas-resource';

@Component({
  selector: 'app-resenas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './resenas.html',
  styleUrls: ['./resenas.scss'] // o .css
})

export class ResenasComponent implements OnInit, OnChanges {
    
  @Input() nroRestaurante!: string;
  @Input() nroSucursal!: string;

  resenas: IResena[] = [];
  loading = false;
  error: string | null = null;

  private _resenasResource = inject(ResenasResource);
  
  ngOnInit(): void {
    
    if (this.nroRestaurante && this.nroSucursal) {
      //this.cargarResenas();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['nroRestaurante'] || changes['nroSucursal']) && this.nroRestaurante && this.nroSucursal) {
      this.cargarResenas();
    }
  }
 
    cargarResenas(): void {
      // No intentar cargar si falta alguno de los identificadores
      if (!this.nroRestaurante || !this.nroSucursal) {
        console.debug('ResenasComponent: faltan parametros para cargar reseñas', { nroRestaurante: this.nroRestaurante, nroSucursal: this.nroSucursal });
        return;
      }
        // Lógica para cargar reseñas
        this.loading = true;
        this.error = null;
        
        this._resenasResource.obtenerResenasPorSucursal({ 
                nroRestaurante: this.nroRestaurante, 
                nroSucursal: this.nroSucursal 
            }).subscribe({
            next: (data) => {
              if (Array.isArray(data)) {
                this.resenas = data;
              } else {
                this.resenas = [];
              }
              this.loading = false;
            },
            error: (err) => {
                this.error = 'Error al cargar reseñas';
                this.loading = false;
            }
        });
    }

}