import { Routes } from '@angular/router';
import { HomePage } from './pages/home/home';
import { PromocionesPage } from './pages/promociones/promociones';
import { DetalleRestauranteComponent } from './components/detalle-restaurante/detalle-restaurante';
import { RestaurantesPage } from './pages/restaurantes/restaurantes';

export const routes: Routes = [
    {path: '', component: HomePage, 
        children:[
            {path: '', component: PromocionesPage},
            {path: 'restaurantes', component: RestaurantesPage},
            {path: 'restaurantes/:nroRestaurante', component: DetalleRestauranteComponent},
            {path: '**', redirectTo: ''} // Ruta para manejar rutas no definidas dentro de home

        ],
    },
    {path: '**', redirectTo: ''} // Ruta para manejar rutas no definidas


];
