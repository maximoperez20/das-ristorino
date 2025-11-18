import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { IReserva } from '../../api/models/i-reserva';
import { AuthService } from '../../../core/services/auth-service';

@Component({
  selector: 'app-mis-reservas',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mis-reservas.html',
  styleUrls: ['./mis-reservas.scss'],
})
export class MisReservasPage implements OnInit {

  reservas: IReserva[] = [];

  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _route = inject(ActivatedRoute);

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
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return 'No disponible';
    try {
      const date = new Date(fecha);
      return date.toLocaleDateString('es-AR', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
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

}
