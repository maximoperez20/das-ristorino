import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoaderService } from './services/loader-service';

@NgModule({
  declarations: [ ],
  imports: [
    CommonModule
  ],
  providers: [
    LoaderService
  ],
  exports: [ ]
})
export class CoreModule { }
