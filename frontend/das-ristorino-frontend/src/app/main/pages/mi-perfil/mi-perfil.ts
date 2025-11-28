import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { IPreferenciaCliente } from '../../api/models/i-preferencia-cliente';
import { AuthService } from '../../../core/services/auth-service';
import { PreferenciaResource } from '../../api/resources/preferencia-resource';

@Component({
  selector: 'app-mi-perfil',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mi-perfil.html',
  styleUrls: ['./mi-perfil.scss'],
})
export class MiPerfilPage implements OnInit {

  preferencias: IPreferenciaCliente[] = [];
  preferenciasAgrupadas: Map<string, IPreferenciaCliente[]> = new Map();
  cargandoPreferencias = false;
  usuario: any = null;

  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _preferenciaResource = inject(PreferenciaResource);

  ngOnInit(): void {
    this.usuario = this._auth.getUser();
    
    if (!this._auth.isAuthenticated()) {
      this._router.navigate(['/login']);
      return;
    }

    // Cargar preferencias
    this.cargarPreferencias();
  }

  cargarPreferencias(): void {
    this.cargandoPreferencias = true;
    this._preferenciaResource.obtenerMisPreferencias().subscribe({
      next: (preferencias) => {
        this.preferencias = preferencias;
        this.agruparPreferencias();
        this.cargandoPreferencias = false;
      },
      error: (err) => {
        console.error('Error al cargar preferencias:', err);
        this.cargandoPreferencias = false;
      }
    });
  }

  agruparPreferencias(): void {
    this.preferenciasAgrupadas.clear();
    this.preferencias.forEach(pref => {
      const categoria = pref.nombreCategoria;
      if (!this.preferenciasAgrupadas.has(categoria)) {
        this.preferenciasAgrupadas.set(categoria, []);
      }
      this.preferenciasAgrupadas.get(categoria)!.push(pref);
    });
  }

  obtenerCategorias(): string[] {
    return Array.from(this.preferenciasAgrupadas.keys());
  }

  obtenerPreferenciasPorCategoria(categoria: string): IPreferenciaCliente[] {
    return this.preferenciasAgrupadas.get(categoria) || [];
  }

  obtenerTextoCliente(): string {
    if (!this.usuario?.nroCliente) return '';
    const nroClienteCorto = this.usuario.nroCliente.substring(0, 8);
    return $localize`:@@mi-perfil.cliente.numero:Cliente #` + nroClienteCorto;
  }

}

