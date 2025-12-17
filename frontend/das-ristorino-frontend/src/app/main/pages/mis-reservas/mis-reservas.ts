import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { IReserva } from '../../api/models/i-reserva';
import { AuthService } from '../../../core/services/auth-service';
import { DateUtilsService } from '../../../core/services/date-utils.service';
import { ReservaResource } from '../../api/resources/reserva-resource';
import { FormsModule } from '@angular/forms';


interface ReservaPorDia {
  fecha: Date;
  fechaKey: string; // YYYY-MM-DD para agrupar
  titulo: string; // "Hoy" o fecha formateada
  reservas: IReserva[];
}

@Component({
  selector: 'app-mis-reservas',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './mis-reservas.html',
  styleUrls: ['./mis-reservas.scss'],
})
export class MisReservasPage implements OnInit {

  reservas: IReserva[] = [];
  reservasPorDia: ReservaPorDia[] = [];
  filtroActivo: string | undefined = undefined;
  mostrarModalCancelacion: boolean = false;
  reservaSeleccionadaParaCancelar: string | null = null;
  notasCancelacion: string = '';

  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _route = inject(ActivatedRoute);
  private _dateUtils = inject(DateUtilsService);
  private _reservaResource = inject(ReservaResource);

  ngOnInit(): void {
    if (!this._auth.isAuthenticated()) {
      this._router.navigate(['/login']);
      return;
    }

    // Leer reservas resueltas por el resolver
    const data = this._route.snapshot.data as { reservas?: IReserva[] };
    if (data && Array.isArray(data.reservas)) {
      this.reservas = data.reservas;
    } else {
      // Fallback: si no hay datos resueltos, inicializar vacío
      this.reservas = [];
    }
    
    // Agrupar y ordenar reservas por día
    this.reservasPorDia = this.agruparReservasPorDia(this.reservas);
  }

  agruparReservasPorDia(reservas: IReserva[]): ReservaPorDia[] {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    // Ordenar reservas por fecha (más recientes primero)
    const reservasOrdenadas = [...reservas].sort((a, b) => {
      const fechaA = new Date(a.fecha_hora).getTime();
      const fechaB = new Date(b.fecha_hora).getTime();
      return fechaB - fechaA; // Orden descendente (más recientes primero)
    });

    // Agrupar por día
    const gruposMap = new Map<string, ReservaPorDia>();

    reservasOrdenadas.forEach(reserva => {
      if (!reserva.fecha_hora) return;

      const fechaReserva = new Date(reserva.fecha_hora);
      fechaReserva.setHours(0, 0, 0, 0);
      
      const fechaKey = fechaReserva.toISOString().split('T')[0]; // YYYY-MM-DD
      
      if (!gruposMap.has(fechaKey)) {
        const esHoy = fechaReserva.getTime() === hoy.getTime();
        const titulo = esHoy 
          ? $localize`Hoy` 
          : this._dateUtils.formatearFechaLegible(fechaReserva);

        gruposMap.set(fechaKey, {
          fecha: fechaReserva,
          fechaKey: fechaKey,
          titulo: titulo,
          reservas: []
        });
      }

      gruposMap.get(fechaKey)!.reservas.push(reserva);
    });

    // Ordenar grupos: "Hoy" primero, luego por fecha descendente (más recientes/futuras primero)
    const grupos = Array.from(gruposMap.values()).sort((a, b) => {
      const esAHoy = a.titulo === $localize`Hoy`;
      const esBHoy = b.titulo === $localize`Hoy`;
      
      // Si uno es "Hoy" y el otro no, "Hoy" va primero
      if (esAHoy && !esBHoy) return -1;
      if (!esAHoy && esBHoy) return 1;
      
      // Si ambos son "Hoy" o ninguno es "Hoy", ordenar por fecha descendente
      return b.fecha.getTime() - a.fecha.getTime();
    });

    // Ordenar reservas dentro de cada grupo por hora (más recientes primero)
    grupos.forEach(grupo => {
      grupo.reservas.sort((a, b) => {
        const fechaA = new Date(a.fecha_hora).getTime();
        const fechaB = new Date(b.fecha_hora).getTime();
        return fechaB - fechaA; // Orden descendente (más recientes primero)
      });
    });

    return grupos;
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return $localize`No disponible`;
    try {
      const date = new Date(fecha);
      return this._dateUtils.formatearFechaHora(date);
    } catch {
      return fecha;
    }
  }

  formatearHora(fecha: string): string {
    if (!fecha) return $localize`No disponible`;
    try {
      const date = new Date(fecha);
      return date.toLocaleTimeString('es-AR', {
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return fecha;
    }
  }

  obtenerBadgeEstado(estado: string): string {
    const estadoNormalizado = this.normalizarEstado(estado);
    
    switch (estadoNormalizado) {
      case 'CANCELADA':
        return 'bg-danger';
      case 'CONFIRMADA':
        return 'bg-success';
      case 'PENDIENTE':
        return 'bg-warning';
      case 'FINALIZADA':
        return 'bg-secondary';
      case 'EN_CURSO':
        return 'bg-info';
      default:
        return 'bg-secondary';
    }
  }

  esReservaPasada(fecha: string): boolean {
    if (!fecha) return false;
    try {
      const fechaReserva = new Date(fecha);
      const ahora = new Date();
      return fechaReserva < ahora;
    } catch {
      return false;
    }
  }

  /**
   * Normaliza un estado de reserva a un valor canónico para comparación
   * Mapea estados en español e inglés al mismo valor canónico
   */
  private normalizarEstado(estado: string | null | undefined): string {
    if (!estado) return '';
    const estadoLower = estado.toLowerCase().trim();
    
    // Mapeo de estados en ambos idiomas a valores canónicos
    if (estadoLower.includes('confirmada') || estadoLower.includes('confirmed')) {
      return 'CONFIRMADA';
    }
    if (estadoLower.includes('cancelada') || estadoLower.includes('cancelled')) {
      return 'CANCELADA';
    }
    if (estadoLower.includes('pendiente') || estadoLower.includes('pending')) {
      return 'PENDIENTE';
    }
    if (estadoLower.includes('finalizada') || estadoLower.includes('completed')) {
      return 'FINALIZADA';
    }
    if (estadoLower.includes('en curso') || estadoLower.includes('in progress')) {
      return 'EN_CURSO';
    }
    
    return estado.toUpperCase();
  }

  /**
   * Verifica si un estado de reserva coincide con el filtro activo
   */
  private estadoCoincideConFiltro(estadoReserva: string | null | undefined, filtro: string | undefined): boolean {
    if (!filtro) return true; // Sin filtro, mostrar todas
    
    const estadoNormalizado = this.normalizarEstado(estadoReserva);
    const filtroNormalizado = this.normalizarEstado(filtro);
    
    return estadoNormalizado === filtroNormalizado;
  }

  filtrarPorEstado(estadoFiltrado: string | undefined): void {
    this.filtroActivo = estadoFiltrado;
    
    // Si no hay filtro, mostrar todas las reservas
    const reservasFiltradas: IReserva[] = estadoFiltrado 
      ? this.reservas.filter((item) => this.estadoCoincideConFiltro(item.estado, estadoFiltrado))
      : this.reservas;
    
    this.reservasPorDia = this.agruparReservasPorDia(reservasFiltradas);
  }

  formatearCantidadPersonas(reserva: IReserva): string {
    // Si tenemos información detallada de adultos y menores, mostrarla
    if (reserva.cant_adultos !== null && reserva.cant_adultos !== undefined &&
        reserva.cant_menores !== null && reserva.cant_menores !== undefined) {
      const partes: string[] = [];
      
      if (reserva.cant_adultos > 0) {
        if (reserva.cant_adultos === 1) {
          partes.push($localize`1 adulto`);
        } else {
          partes.push(`${reserva.cant_adultos} ${$localize`adultos`}`);
        }
      }
      
      if (reserva.cant_menores > 0) {
        if (reserva.cant_menores === 1) {
          partes.push($localize`1 menor`);
        } else {
          partes.push(`${reserva.cant_menores} ${$localize`menores`}`);
        }
      }
      
      if (partes.length > 0) {
        return partes.join(', ');
      }
    }
    
    // Fallback: mostrar total si no hay información detallada
    const cantidad = (reserva.cant_adultos ?? 0) + (reserva.cant_menores ?? 0);
    if (!cantidad || cantidad <= 0) return '';
    if (cantidad === 1) {
      return $localize`1 persona`;
    }
    return `${cantidad} ${$localize`personas`}`;
  }

  /**
   * Verifica si un filtro está activo comparando estados normalizados
   */
  esFiltroActivo(filtro: string | undefined): boolean {
    // Si ambos son undefined, el botón "Todas" está activo
    if (!this.filtroActivo && !filtro) {
      return true;
    }
    // Si uno es undefined y el otro no, no coinciden
    if (!this.filtroActivo || !filtro) {
      return false;
    }
    // Comparar normalizando ambos estados
    const filtroActivoNormalizado = this.normalizarEstado(this.filtroActivo);
    const filtroNormalizado = this.normalizarEstado(filtro);
    return filtroActivoNormalizado === filtroNormalizado;
  }

  /**
   * Define si una reserva es cancelable considerando estado y fecha
   */
  puedeCancelar(reserva: IReserva): boolean {
    if (!reserva) return false;
    const estado = this.normalizarEstado(reserva.estado);
    const yaPaso = this.esReservaPasada(reserva.fecha_hora);
    return !yaPaso && estado !== 'CANCELADA' && estado !== 'FINALIZADA';
  }
  /**
   * Cancelar una reserva
   */
  onCancelarReservaClick(nroReserva: string): void {
    // this._reservaResource.cancelarReserva({ codReserva: nroReserva }).subscribe({
    //   next: () => {
    //     // Refrescar la página para ver los cambios
    //     window.location.reload();
    //   },
    //   error: (err) => {
    //     console.error('Error al cancelar la reserva:', err);
    //     alert($localize`Ocurrió un error al cancelar la reserva. Por favor, intenta nuevamente más tarde.`);
    //   }
    // });
    this.reservaSeleccionadaParaCancelar = nroReserva;
    this.mostrarModalCancelacion = true;
  }

  cerrarModalCancelacion(): void {
    this.mostrarModalCancelacion = false;
    this.reservaSeleccionadaParaCancelar = null;
  }

  confirmarCancelacion(): void {
    if (!this.reservaSeleccionadaParaCancelar) return;
    this.mostrarModalCancelacion = false;

    this._reservaResource.cancelarReserva({ nroReserva: this.reservaSeleccionadaParaCancelar, motivoCancelacion: this.notasCancelacion }).subscribe({
      next: () => {
        // Refrescar la página para ver los cambios
        window.location.reload();
      },
      error: (err) => {
        console.error('Error al cancelar la reserva:', err);
        alert($localize`Ocurrió un error al cancelar la reserva. Por favor, intenta nuevamente más tarde.`);
      }
    });

  
  }
}
