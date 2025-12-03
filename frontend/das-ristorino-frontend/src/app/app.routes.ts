import { Routes } from '@angular/router';
import { HomePage } from './main/pages/home/home';
import { PromocionesPage } from './main/pages/promociones/promociones';
import { DetalleRestauranteComponent } from './main/components/detalle-restaurante/detalle-restaurante';
import { restauranteResolver } from './main/resolvers/restaurante.resolver';
import { restauranteListResolver } from './main/resolvers/restaurantes-list.resolver';
import { promocionesListResolver } from './main/resolvers/promociones-list.resolver';
import { misReservasResolver } from './main/resolvers/mis-reservas.resolver';
import { localidadesResolver } from './main/resolvers/localidades.resolver';
import { preferenciasResolver } from './main/resolvers/preferencias.resolver';
import { misPreferenciasResolver } from './main/resolvers/mis-preferencias.resolver';
import { especialidadesAlimentariasResolver } from './main/resolvers/especialidades-alimentarias.resolver';
import { RestaurantesPage } from './main/pages/restaurantes/restaurantes';
import { PromocionResource } from './main/api/resources/promocion-resource';
import { RestauranteResource } from './main/api/resources/restaurante-resource';
import { ReservaResource } from './main/api/resources/reserva-resource';
import { ClienteResource } from './main/api/resources/cliente-resource';
import { LocalidadResource } from './main/api/resources/localidad-resource';
import { PreferenciaResource } from './main/api/resources/preferencia-resource';
import { resenasResolver } from './main/resolvers/resenas-obtener.resolver';
import { ResenaResource } from './main/api/resources/resena-resource';

export const routes: Routes = [
    {path: '', component: HomePage, 
        children:
        [
            {
                path: '',
                loadComponent: () =>
                import('./main/pages/promociones/promociones').then(m => m.PromocionesPage),
                resolve: { promociones: promocionesListResolver },
                providers: [PromocionResource]
            },
            {
                path: 'restaurantes',
                loadComponent: () =>
                import('./main/pages/restaurantes/restaurantes').then(m => m.RestaurantesPage),
                resolve: { restaurantes: restauranteListResolver },
                providers: [RestauranteResource]
            },
            {
                path: 'restaurantes/:nroRestaurante',
                loadComponent: () =>
                import('./main/components/detalle-restaurante/detalle-restaurante').then(
                    m => m.DetalleRestauranteComponent
                ),
                resolve: { restaurante: restauranteResolver, especialidadesAlimentarias: especialidadesAlimentariasResolver},
                providers: [RestauranteResource, PromocionResource, ReservaResource, PreferenciaResource, ResenaResource]
            },
            {
                path: 'login',
                loadComponent: () =>
                import('./main/pages/login/login').then(
                    m => m.LoginPage
                ),
                providers: [ClienteResource]

            },
            {
                path: 'register',
                loadComponent: () =>
                import('./main/pages/register/register').then(
                    m => m.RegisterPage
                ),
                resolve: { localidades: localidadesResolver },
                providers: [ClienteResource, LocalidadResource]
            },
            {
                path: 'preferencias-registro',
                loadComponent: () =>
                import('./main/pages/preferencias-registro/preferencias-registro').then(
                    m => m.PreferenciasRegistroPage
                ),
                resolve: { 
                    categorias: preferenciasResolver,
                    misPreferencias: misPreferenciasResolver
                },
                providers: [PreferenciaResource]
            },
            {
                path: 'mi-perfil',
                loadComponent: () =>
                import('./main/pages/mi-perfil/mi-perfil').then(
                    m => m.MiPerfilPage
                ),
                providers: [PreferenciaResource]
            },
            {
                path: 'mis-reservas',
                loadComponent: () =>
                import('./main/pages/mis-reservas/mis-reservas').then(
                    m => m.MisReservasPage
                ),
                resolve: { reservas: misReservasResolver },
                providers: [ReservaResource, ResenaResource]
            },
            {
                path: 'buscar',
                loadComponent: () =>
                import('./main/pages/buscar/buscar').then(
                    m => m.BuscarPage
                ),
                providers: [RestauranteResource]
            },
            { path: '**', redirectTo: '' },
            ],
    },
    {path: '**', redirectTo: ''} // Ruta para manejar rutas no definidas


];
