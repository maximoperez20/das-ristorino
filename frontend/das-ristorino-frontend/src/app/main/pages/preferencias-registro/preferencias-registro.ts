import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { PreferenciaResource } from '../../api/resources/preferencia-resource';
import { AppMessageService } from '../../../core/services/app-message-service';
import { AuthService } from '../../../core/services/auth-service';
import { ICategoriaConDominios } from '../../api/models/i-categoria-con-dominios';
import { IPreferenciaItem } from '../../api/models/i-preferencia-item';
import { IPreferenciaCliente } from '../../api/models/i-preferencia-cliente';

@Component({
  selector: 'app-preferencias-registro',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './preferencias-registro.html',
  styleUrls: ['./preferencias-registro.scss'],
})
export class PreferenciasRegistroPage implements OnInit {

  categorias: ICategoriaConDominios[] = [];
  preferenciasSeleccionadas: Map<string, Set<number>> = new Map(); // codCategoria -> Set<nroValorDominio>
  cargando = false;

  private _preferenciaResource = inject(PreferenciaResource);
  private _router = inject(Router);
  private _route = inject(ActivatedRoute);
  private _messageService = inject(AppMessageService);
  private _auth = inject(AuthService);

  ngOnInit(): void {
    // Verificar autenticación
    if (!this._auth.isAuthenticated()) {
      this._router.navigate(['/login']);
      return;
    }

    // Leer categorías y preferencias resueltas por los resolvers
    const data = this._route.snapshot.data as { 
      categorias?: ICategoriaConDominios[];
      misPreferencias?: IPreferenciaCliente[];
    };
    
    if (data && Array.isArray(data.categorias)) {
      this.categorias = data.categorias;
      // Inicializar el mapa de selecciones
      this.categorias.forEach(cat => {
        this.preferenciasSeleccionadas.set(cat.codCategoria, new Set());
      });
      
      // Marcar las preferencias existentes como seleccionadas
      if (data.misPreferencias && Array.isArray(data.misPreferencias)) {
        this.marcarPreferenciasExistentes(data.misPreferencias);
      }
    } else {
      // Fallback: cargar directamente si no hay datos resueltos
      this.cargarCategorias();
    }
  }

  private marcarPreferenciasExistentes(preferencias: IPreferenciaCliente[]): void {
    preferencias.forEach(pref => {
      const seleccionados = this.preferenciasSeleccionadas.get(pref.codCategoria);
      if (seleccionados) {
        seleccionados.add(pref.nroValorDominio);
      }
    });
  }

  private cargarCategorias(): void {
    this.cargando = true;
    this._preferenciaResource.obtenerCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        // Inicializar el mapa de selecciones
        this.categorias.forEach(cat => {
          this.preferenciasSeleccionadas.set(cat.codCategoria, new Set());
        });
        
        // Cargar preferencias existentes y marcarlas
        this._preferenciaResource.obtenerMisPreferencias().subscribe({
          next: (preferencias) => {
            this.marcarPreferenciasExistentes(preferencias);
            this.cargando = false;
          },
          error: (err) => {
            console.error('Error al cargar preferencias existentes:', err);
            this.cargando = false;
          }
        });
      },
      error: (err) => {
        console.error('Error al cargar categorías:', err);
        this.cargando = false;
      }
    });
  }

  togglePreferencia(codCategoria: string, nroValorDominio: number): void {
    const seleccionados = this.preferenciasSeleccionadas.get(codCategoria);
    if (seleccionados) {
      if (seleccionados.has(nroValorDominio)) {
        seleccionados.delete(nroValorDominio);
      } else {
        seleccionados.add(nroValorDominio);
      }
    }
  }

  estaSeleccionado(codCategoria: string, nroValorDominio: number): boolean {
    const seleccionados = this.preferenciasSeleccionadas.get(codCategoria);
    return seleccionados ? seleccionados.has(nroValorDominio) : false;
  }

  tieneSelecciones(codCategoria: string): boolean {
    const seleccionados = this.preferenciasSeleccionadas.get(codCategoria);
    return seleccionados ? seleccionados.size > 0 : false;
  }

  totalSelecciones(): number {
    let total = 0;
    this.preferenciasSeleccionadas.forEach(seleccionados => {
      total += seleccionados.size;
    });
    return total;
  }

  getTextoPreferencias(): string {
    const total = this.totalSelecciones();
    return total === 1 ? $localize`preferencia` : $localize`preferencias`;
  }


  guardarPreferencias(): void {
    // Convertir las selecciones a formato IPreferenciaItem[]
    const preferencias: IPreferenciaItem[] = [];
    
    this.preferenciasSeleccionadas.forEach((seleccionados, codCategoria) => {
      seleccionados.forEach(nroValorDominio => {
        preferencias.push({
          codCategoria,
          nroValorDominio,
          observaciones: undefined
        });
      });
    });

    if (preferencias.length === 0) {
      // Si no hay selecciones, simplemente continuar
      this._messageService.showMessage({
        text: $localize`No se seleccionaron preferencias. Puedes continuar.`,
        title: $localize`Sin preferencias`
      });
      this._router.navigate(['/mi-perfil']);
      return;
    }

    this.cargando = true;
    this._preferenciaResource.guardarPreferencias({ preferencias }).subscribe({
      next: (response) => {
        this._messageService.showMessage({
          text: $localize`Tus preferencias gastronómicas han sido guardadas exitosamente.`,
          title: $localize`Preferencias guardadas`
        });
        this._router.navigate(['/mi-perfil']);
      },
      error: (err) => {
        console.error('Error al guardar preferencias:', err);
        // El error handler global manejará el error automáticamente
        this.cargando = false;
      }
    });
  }

  omitir(): void {
    this._router.navigate(['/mi-perfil']);
  }

}

