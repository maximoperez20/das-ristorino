import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface AuthResponse {
  token: string;
  nroCliente: number;
  nombre: string;
  apellido: string;
  correo: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/clientes';

  constructor(private http: HttpClient) {}

  login(correo: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { correo, password })
      .pipe(
        tap(response => {
          // Store token and user data after successful login
          localStorage.setItem('token', response.token);
          localStorage.setItem('userData', JSON.stringify({
            nroCliente: response.nroCliente,
            nombre: response.nombre,
            apellido: response.apellido,
            correo: response.correo
          }));
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('userData');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserData(): any {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
  }
}