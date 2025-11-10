import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { appHttpInterceptor } from './core/interceptors/app-http-interceptor';

import { routes } from './app.routes';
import { ResourceHandlerHttpClient } from '@ngx-resource/handler-ngx-http';
import { ResourceHandler } from '@ngx-resource/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
  provideHttpClient(withInterceptors([appHttpInterceptor])),
    { provide: ResourceHandler, useClass: ResourceHandlerHttpClient }
  ]
};
