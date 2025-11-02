import { Injectable } from '@angular/core';
import { Restaurante } from './data/restaurantes.mock';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private appiUrl = 'http://localhost:8080/api/restaurantes';

  constructor(private http: HttpClient) { }

  obtenerRestaurantes(): Observable<Restaurante[]> {
    return this.http.get<Restaurante[]>(this.appiUrl);
  }
  
  
}
