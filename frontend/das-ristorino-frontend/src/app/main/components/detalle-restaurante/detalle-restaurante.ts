import { Component, inject, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IRestaurante } from '../../api/models/i-restaurante';
import { ISucursal } from '../../api/models/i-sucursal';
import { NgClass } from '@angular/common';
import { HorariosDisponiblesComponent, HorarioSeleccionado } from '../horarios-disponibles/horarios-disponibles';
import { ResenasComponent } from '../resenas-component/resenas';
import { PromocionComponent } from "../promocion/promocion";
import { FormularioReservaComponent } from '../formulario-reserva/formulario-reserva';
import { AuthService } from '../../../core/services/auth-service';
import { IDominioPreferencia } from '../../api/models/i-dominio-preferencia';
import { IHorariosDisponiblesResponse } from '../../api/models/i-horario-disponible';
import { MenuResource } from '../../api/resources/menu-resource';
import { IMenu } from '../../api/models/i-menu';

@Component({
  selector: 'app-detalle-restaurante',
  standalone: true,
  imports: [NgClass, HorariosDisponiblesComponent, PromocionComponent, FormularioReservaComponent, ResenasComponent],
  templateUrl: './detalle-restaurante.html',
  styleUrls: ['./detalle-restaurante.scss'],
})
export class DetalleRestauranteComponent implements OnInit {

  @ViewChild('horariosComponent') horariosComponent?: HorariosDisponiblesComponent;

  restaurante?: IRestaurante | undefined;
  especialidadesAlimentarias: IDominioPreferencia[] = [];
  sucursalSeleccionada?: ISucursal;
  fechaSeleccionada: Date = new Date();
  nroRestaurante: string = '';
  horarioSeleccionado?: HorarioSeleccionado;
  mostrarFormularioReserva: boolean = false;

  private _route = inject(ActivatedRoute);
  private _router = inject(Router);
  private _auth = inject(AuthService);
  private _cdr = inject(ChangeDetectorRef);
  private _menuResource = inject(MenuResource);

  ngOnInit(): void {
    this.nroRestaurante = this._route.snapshot.paramMap.get('nroRestaurante') || '';
    
    this._route.data.subscribe(data => {
      this.restaurante = data?.['restaurante'];
      if (this.restaurante && !this.restaurante.nroRestaurante) {
        this.restaurante.nroRestaurante = this.nroRestaurante;
      }
      this.especialidadesAlimentarias = data?.['especialidadesAlimentarias'] || [];
      this.seleccionarPrimeraSucursal();
    });
  }

  seleccionarPrimeraSucursal(): void {
    if (this.restaurante?.sucursales && this.restaurante.sucursales.length > 0) {
      this.sucursalSeleccionada = this.restaurante.sucursales[0];
    }
  }

  seleccionarSucursal(sucursal: ISucursal): void {
    this.sucursalSeleccionada = sucursal;
  }

  formatearHorario(horario: string | null): string {
    if (!horario) return $localize`No disponible`;
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

  onHorarioSeleccionado(horario: HorarioSeleccionado): void {
    // Verificar autenticación antes de abrir el modal
    if (!this._auth.isAuthenticated()) {
      this._router.navigate(['/login'], { 
        queryParams: { returnUrl: this._router.url } 
      });
      return;
    }
    
    this.horarioSeleccionado = horario;
    this.mostrarFormularioReserva = true;
  }

  onReservaConfirmada(): void {
    this.mostrarFormularioReserva = false;
    this.horarioSeleccionado = undefined;
  }

  onModalVisibleChange(visible: boolean): void {
    this.mostrarFormularioReserva = visible;
    if (!visible) {
      this.horarioSeleccionado = undefined;
      if (this.horariosComponent) {
        this.horariosComponent.limpiarSeleccion();
      }
    }
  }

  onActualizarHorariosDisponibles(horarios: IHorariosDisponiblesResponse): void {
    this.mostrarFormularioReserva = false;
    this.horarioSeleccionado = undefined;
    this._cdr.detectChanges();
    
    if (this.horariosComponent) {
      this.horariosComponent.actualizarHorarios(horarios);
      this.horariosComponent.limpiarSeleccion();
    } else {
      setTimeout(() => {
        this._cdr.detectChanges();
        if (this.horariosComponent) {
          this.horariosComponent.actualizarHorarios(horarios);
          this.horariosComponent.limpiarSeleccion();
        }
      }, 200);
    }
  }

  verMenu(): void {
    if (!this.nroRestaurante || !this.sucursalSeleccionada?.nroSucursal) {
      return;
    }

    this._menuResource.obtenerMenu({
      nroRestaurante: this.nroRestaurante,
      nroSucursal: this.sucursalSeleccionada.nroSucursal
    }).subscribe({
      next: (menu: IMenu) => {
        if (!menu || !menu.datosArchivoBase64 || !menu.tipoMime) {
          return;
        }
        try {
          const byteCharacters = atob(menu.datosArchivoBase64);
          const byteNumbers = new Array(byteCharacters.length);
          for (let i = 0; i < byteCharacters.length; i++) {
            byteNumbers[i] = byteCharacters.charCodeAt(i);
          }
          const byteArray = new Uint8Array(byteNumbers);
          const blob = new Blob([byteArray], { type: menu.tipoMime });
          const url = URL.createObjectURL(blob);
          window.open(url, '_blank');
          // Liberar URL cuando el navegador cierre la pestaña no es trivial; si se usa modal, revocar al cerrar
          // setTimeout(() => URL.revokeObjectURL(url), 60_000);
        } catch (e) {
          // noop: podrías loguear o mostrar mensaje si tenés un message service
        }
      },
      error: () => {
        // noop: opcionalmente mostrar mensaje de error
      }
    });
  }
}
