import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Cliente {
  nroCliente: number;
  nombre: string;
  apellido: string;
  correo: string;
  clave?: string;
  habilitado: boolean;
}

export interface CrearClienteDto {
  nombre: string;
  apellido: string;
  correo: string;
  clave: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private apiUrl = 'http://localhost:8080/api/clientes';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  registrar(cliente: CrearClienteDto): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.apiUrl}/register`, cliente);
  }

  getCliente(nroCliente: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${nroCliente}`, {
      headers: this.getHeaders()
    });
  }

  actualizarCliente(nroCliente: number, cliente: Partial<Cliente>): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${nroCliente}`, cliente, {
      headers: this.getHeaders()
    });
  }
}