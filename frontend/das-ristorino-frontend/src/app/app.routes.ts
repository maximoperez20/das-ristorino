import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home';
import { PromocionesComponent } from './components/promociones/promociones';
import { DetalleRestauranteComponent } from './components/detalle-restaurante/detalle-restaurante';
import { RestaurantesComponent } from './components/restaurantes/restaurantes';

export const routes: Routes = [
    {path: '', component: HomeComponent, 
        children:[
            {path: 'promociones', component: PromocionesComponent},
            {path: 'restaurantes', component: RestaurantesComponent},
            {path: 'restaurantes/:nroRestaurante', component: DetalleRestauranteComponent},
            {path: '**', redirectTo: '/promociones'} // Ruta para manejar rutas no definidas dentro de home
    
        ],
    },
    {path: '**', redirectTo: ''} // Ruta para manejar rutas no definidas


];
