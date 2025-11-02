import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Promocion } from './data/promociones.mock';

@Injectable({
  providedIn: 'root'
})
export class PromocionService {

  private apiUrl = 'http://localhost:8080/api/promociones';

  constructor(private http: HttpClient) { }

  obtenerPromociones(): Observable<Promocion[]> {
    return this.http.get<Promocion[]>(this.apiUrl);
  }
    
  
}
