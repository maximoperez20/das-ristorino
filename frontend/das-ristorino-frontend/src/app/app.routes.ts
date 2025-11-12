import { Routes } from '@angular/router';
import { HomePage } from './main/pages/home/home';
import { PromocionesPage } from './main/pages/promociones/promociones';
import { DetalleRestauranteComponent } from './main/components/detalle-restaurante/detalle-restaurante';
import { restauranteResolver } from './main/resolvers/restaurante.resolver';
import { restauranteListResolver } from './main/resolvers/restaurantes-list.resolver';
import { promocionesListResolver } from './main/resolvers/promociones-list.resolver';
import { RestaurantesPage } from './main/pages/restaurantes/restaurantes';

export const routes: Routes = [
    {path: '', component: HomePage, 
        children:
        [
            {
                path: '',
                loadComponent: () =>
                import('./main/pages/promociones/promociones').then(m => m.PromocionesPage),
                resolve: { promociones: promocionesListResolver }
            },
            {
                path: 'restaurantes',
                loadComponent: () =>
                import('./main/pages/restaurantes/restaurantes').then(m => m.RestaurantesPage),
                resolve: { restaurantes: restauranteListResolver }
            },
            {
                path: 'restaurantes/:nroRestaurante',
                loadComponent: () =>
                import('./main/components/detalle-restaurante/detalle-restaurante').then(
                    m => m.DetalleRestauranteComponent
                ),
                resolve: { restaurante: restauranteResolver },
            },
            { path: '**', redirectTo: '' },
            ],
    },
    {path: '**', redirectTo: ''} // Ruta para manejar rutas no definidas


];
