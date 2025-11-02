import { Routes } from '@angular/router';
import { HomeComponent } from './home-component/home-component';
import { CarruselComponent } from './carrusel-component/carrusel-component';
import { DetalleRestauranteComponent } from './detalle-restaurante-component/detalle-restaurante-component';
import { RestaurantesComponent } from './restaurantes-component/restaurantes-component';

export const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'carrusel', component: CarruselComponent},
    {path: 'restaurantes', component: RestaurantesComponent},
    {path: 'restaurantes/:restauranteId', component: DetalleRestauranteComponent},
    {path: '**', redirectTo: ''} // Ruta para manejar rutas no definidas


];
