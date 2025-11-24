import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ClienteResource } from '../../api/resources/cliente-resource';
import { AuthService } from '../../../core/services/auth-service';
import { AppMessageService } from '../../../core/services/app-message-service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
})
export class LoginPage implements OnInit {

  loginForm: FormGroup;
  mostrarPassword = false;

  private _fb = inject(FormBuilder);
  private _clienteResource = inject(ClienteResource);
  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _messageService = inject(AppMessageService);

  constructor() {
    this.loginForm = this._fb.group({
      correo: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    // Si ya está autenticado, redirigir a mis reservas
    if (this._auth.isAuthenticated()) {
      this._router.navigate(['/mis-reservas']);
    }
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.marcarCamposInvalidos();
      return;
    }

    const loginData = this.loginForm.value;

    this._clienteResource.login(loginData).subscribe({
      next: (response) => {
        // Guardar datos de autenticación
        this._auth.setAuth({
          token: response.token,
          nroCliente: response.nroCliente,
          nombre: response.nombre,
          apellido: response.apellido,
          correo: response.correo
        });

        // Mostrar mensaje de éxito
        this._messageService.showMessage({
          text: $localize`Bienvenido, ${response.nombre} ${response.apellido}!`,
          title: $localize`Sesión iniciada`
        });

        // Redirigir a mis reservas
        this._router.navigate(['/mis-reservas']);
      },
      error: (err) => {
        // El error handler global manejará el error automáticamente
        console.error('Error en login:', err);
      }
    });
  }

  private marcarCamposInvalidos(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      if (control && control.invalid) {
        control.markAsTouched();
      }
    });
  }

  toggleMostrarPassword(): void {
    this.mostrarPassword = !this.mostrarPassword;
  }

  getAriaLabelPassword(): string {
    return this.mostrarPassword ? $localize`Ocultar contraseña` : $localize`Mostrar contraseña`;
  }

  get correo() {
    return this.loginForm.get('correo');
  }

  get password() {
    return this.loginForm.get('password');
  }

}

