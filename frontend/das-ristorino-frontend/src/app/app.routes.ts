import { Routes } from '@angular/router';
import { HomePage } from './main/pages/home/home';
import { PromocionesPage } from './main/pages/promociones/promociones';
import { DetalleRestauranteComponent } from './main/components/detalle-restaurante/detalle-restaurante';
import { RestauranteResolver } from './main/resolvers/restaurante.resolver';
import { RestaurantesPage } from './main/pages/restaurantes/restaurantes';

export const routes: Routes = [
    {path: '', component: HomePage, 
        children:[
            {path: '', component: PromocionesPage},
            {path: 'restaurantes', 
                loadComponent: () => import('./main/pages/restaurantes/restaurantes').then(m => m.RestaurantesPage)},
            {path: 'restaurantes/:nroRestaurante', component: DetalleRestauranteComponent, resolve: { restaurante: RestauranteResolver}},
            {path: '**', redirectTo: ''} // Ruta para manejar rutas no definidas dentro de home

        ],
    },
    {path: '**', redirectTo: ''} // Ruta para manejar rutas no definidas


];
