import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, NavigationEnd } from '@angular/router';
import { AuthService } from '../../../core/services/auth-service';
import { LanguageService } from '../../../core/services/language-service';
import { BuscadorNLPComponent } from '../buscador-nlp/buscador-nlp';
import { filter, Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  imports: [RouterLink, CommonModule, BuscadorNLPComponent],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent implements OnInit, OnDestroy {

  isAuthenticated = false;
  usuario: any = null;
  currentLanguage: string = 'es-AR';
  currentLanguageName: string = 'Español';
  availableLanguages: Array<{ codIdioma: string; nombre: string; nroIdioma: number }> = [];
  private routerSubscription?: Subscription;

  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _language = inject(LanguageService);

  ngOnInit(): void {
    this.checkAuth();
    this.loadLanguageInfo();
    
    // Escuchar cambios de navegación para actualizar el estado de autenticación
    this.routerSubscription = this._router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.checkAuth();
        this.loadLanguageInfo();
      });
  }

  loadLanguageInfo(): void {
    this.currentLanguage = this._language.getCurrentLanguage();
    this.currentLanguageName = this._language.getLanguageName();
    this.availableLanguages = this._language.getAvailableLanguages();
  }

  changeLanguage(codIdioma: string): void {
    this._language.setLanguage(codIdioma);
    // setLanguage() ya recarga la página, no necesitamos hacer nada más
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
