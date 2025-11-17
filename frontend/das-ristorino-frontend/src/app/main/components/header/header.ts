import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, NavigationEnd } from '@angular/router';
import { AuthService } from '../../../core/services/auth-service';
import { filter, Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  imports: [RouterLink, CommonModule],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent implements OnInit, OnDestroy {

  isAuthenticated = false;
  usuario: any = null;
  private routerSubscription?: Subscription;

  private _auth = inject(AuthService);
  private _router = inject(Router);

  ngOnInit(): void {
    this.checkAuth();
    
    // Escuchar cambios de navegación para actualizar el estado de autenticación
    this.routerSubscription = this._router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.checkAuth();
      });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  checkAuth(): void {
    this.isAuthenticated = this._auth.isAuthenticated();
    this.usuario = this._auth.getUser();
  }

  logout(): void {
    this._auth.logout();
    this.checkAuth();
    this._router.navigate(['/']);
  }

}
