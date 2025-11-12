import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoaderService } from './services/loader-service';
import { AppMessageService } from './services/app-message-service';

@NgModule({
  declarations: [ ],
  imports: [
    CommonModule
  ],
  providers: [
    LoaderService,
    AppMessageService
  ],
  exports: [ ]
})
export class CoreModule { }
