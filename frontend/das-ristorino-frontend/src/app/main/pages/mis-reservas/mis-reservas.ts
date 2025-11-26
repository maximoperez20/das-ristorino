import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { IReserva } from '../../api/models/i-reserva';
import { AuthService } from '../../../core/services/auth-service';
import { DateUtilsService } from '../../../core/services/date-utils.service';
import { TranslateBdPipe } from '../../../core/pipes/translate-bd.pipe';

interface ReservaPorDia {
  fecha: Date;
  fechaKey: string; // YYYY-MM-DD para agrupar
  titulo: string; // "Hoy" o fecha formateada
  reservas: IReserva[];
}

@Component({
  selector: 'app-mis-reservas',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslateBdPipe],
  templateUrl: './mis-reservas.html',
  styleUrls: ['./mis-reservas.scss'],
})
export class MisReservasPage implements OnInit {

  reservas: IReserva[] = [];
  reservasPorDia: ReservaPorDia[] = [];
  filtroActivo: string | undefined = undefined;

  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _route = inject(ActivatedRoute);
  private _dateUtils = inject(DateUtilsService);

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
    const estadoLower = estado?.toLowerCase() || '';
    if (estadoLower.includes('cancelada')) {
      return 'bg-danger';
    } else if (estadoLower.includes('confirmada') || estadoLower.includes('activa')) {
      return 'bg-success';
    } else if (estadoLower.includes('pendiente')) {
      return 'bg-warning';
    }
    return 'bg-secondary';
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

  filtrarPorEstado(estadoFiltrado: string | undefined): void {
    this.filtroActivo = estadoFiltrado;
    
    // Si no hay filtro, mostrar todas las reservas
    const reservasFiltradas: IReserva[] = estadoFiltrado 
      ? this.reservas.filter((item) => item.estado === estadoFiltrado)
      : this.reservas;
    
    this.reservasPorDia = this.agruparReservasPorDia(reservasFiltradas);
  }

}
