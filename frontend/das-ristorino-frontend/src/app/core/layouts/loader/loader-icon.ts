import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Loader, LoaderService } from '../../services/loader-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loader-icon',
  imports: [
    CommonModule
  ],
  templateUrl: './loader-icon.html',  
  styleUrl: './loader-icon.css'  
})
export class LoaderIcon implements OnInit, OnDestroy {

  private _subscription!: Subscription;
  
  loaded: boolean = false;

  constructor(private _service: LoaderService) { }

  ngOnInit(): void {
    this._subscription = this._service.loader$.subscribe((ref: Loader) => {
      this.loaded = ref.loaded;
    });
  }

  ngOnDestroy(): void {
    this._subscription.unsubscribe();
  }

}
