import { Routes } from '@angular/router';
import { HomePage } from './main/pages/home/home';
import { PromocionesPage } from './main/pages/promociones/promociones';
import { DetalleRestauranteComponent } from './main/components/detalle-restaurante/detalle-restaurante';
import { RestauranteResolver } from './main/resolvers/restaurante.resolver';
import { RestaurantesListResolver } from './main/resolvers/restaurantes-list.resolver';
import { PromocionesListResolver } from './main/resolvers/promociones-list.resolver';
import { RestaurantesPage } from './main/pages/restaurantes/restaurantes';

export const routes: Routes = [
    {path: '', component: HomePage, 
        children:
        [
            {
                path: '',
                loadComponent: () =>
                import('./main/pages/promociones/promociones').then(m => m.PromocionesPage),
                resolve: { promociones: PromocionesListResolver }
            },
            {
                path: 'restaurantes',
                loadComponent: () =>
                import('./main/pages/restaurantes/restaurantes').then(m => m.RestaurantesPage),
                resolve: { restaurantes: RestaurantesListResolver }
            },
            {
                path: 'restaurantes/:nroRestaurante',
                loadComponent: () =>
                import('./main/components/detalle-restaurante/detalle-restaurante').then(
                    m => m.DetalleRestauranteComponent
                ),
                resolve: { restaurante: RestauranteResolver },
            },
            { path: '**', redirectTo: '' },
            ],
    },
    {path: '**', redirectTo: ''} // Ruta para manejar rutas no definidas


];
