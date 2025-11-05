import { Injectable } from '@angular/core';
import { Restaurante } from '../api/models/i-restaurante';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private apiUrl = 'http://localhost:8080/api/restaurantes';

  constructor(private http: HttpClient) { }

  obtenerRestaurantes(): Observable<Restaurante[]> {
    return this.http.get<Restaurante[]>(this.apiUrl);
  }

  obtenerRestaurantePorId(id: string): Observable<Restaurante | undefined> {
    return this.http.get<Restaurante | undefined>(`${this.apiUrl}/${id}`);
  }
  
  
}
