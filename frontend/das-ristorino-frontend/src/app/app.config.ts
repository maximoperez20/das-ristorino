import { ApplicationConfig, ErrorHandler, importProvidersFrom, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { appHttpInterceptor } from './core/interceptors/app-http-interceptor';
import { ResourceHandlerHttpClient } from '@ngx-resource/handler-ngx-http';
import { ResourceHandler } from '@ngx-resource/core';

import { routes } from './app.routes';
import { CoreModule } from './core/core-module';
import { AppErrorHandler } from './core/handlers/app-error-handler';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
  provideHttpClient(
    withInterceptors([appHttpInterceptor])
  ),
  importProvidersFrom(CoreModule),
    { provide: ResourceHandler, useClass: ResourceHandlerHttpClient },
    { provide: ErrorHandler, useClass: AppErrorHandler },
  ]
};
