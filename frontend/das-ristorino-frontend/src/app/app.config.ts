import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { appHttpInterceptor } from './core/interceptors/app-http-interceptor';

import { routes } from './app.routes';
import { ResourceHandlerHttpClient } from '@ngx-resource/handler-ngx-http';
import { ResourceHandler } from '@ngx-resource/core';
import { RestauranteResolver } from './main/resolvers/restaurante.resolver';
import { PromocionResource } from './main/api/resources/promocion-resource';
import { RestauranteResource } from './main/api/resources/restaurante-resource';
import { PromocionesListResolver } from './main/resolvers/promociones-list.resolver';
import { RestaurantesListResolver } from './main/resolvers/restaurantes-list.resolver';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
  provideHttpClient(withInterceptors([appHttpInterceptor])),
    { provide: ResourceHandler, useClass: ResourceHandlerHttpClient },
    PromocionResource,
    RestauranteResource,
    RestauranteResolver,
    RestaurantesListResolver,
    PromocionesListResolver,

  ]
};
