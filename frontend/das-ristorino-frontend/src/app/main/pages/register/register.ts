import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { ClienteResource } from '../../api/resources/cliente-resource';
import { AuthService } from '../../../core/services/auth-service';
import { AppMessageService } from '../../../core/services/app-message-service';
import { ILocalidad } from '../../api/models/i-localidad';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
})
export class RegisterPage implements OnInit {

  registerForm: FormGroup;
  mostrarPassword = false;
  mostrarConfirmPassword = false;
  localidades: ILocalidad[] = [];

  private _fb = inject(FormBuilder);
  private _clienteResource = inject(ClienteResource);
  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _route = inject(ActivatedRoute);
  private _messageService = inject(AppMessageService);

  constructor() {
    this.registerForm = this._fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(120)]],
      apellido: ['', [Validators.required, Validators.maxLength(120)]],
      correo: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
      telefonos: ['', [Validators.maxLength(120)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
      confirmPassword: ['', [Validators.required]],
      nroLocalidad: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  ngOnInit(): void {
    // Si ya está autenticado, redirigir a mis reservas
    if (this._auth.isAuthenticated()) {
      this._router.navigate(['/mis-reservas']);
      return;
    }

    // Leer localidades resueltas por el resolver
    const data = this._route.snapshot.data as { localidades?: ILocalidad[] };
    if (data && Array.isArray(data.localidades)) {
      this.localidades = data.localidades;
    } else {
      // Fallback: si no hay datos resueltos, inicializar vacío
      this.localidades = [];
    }
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.marcarCamposInvalidos();
      return;
    }

    const registerData = {
      nombre: this.registerForm.value.nombre,
      apellido: this.registerForm.value.apellido,
      correo: this.registerForm.value.correo,
      telefonos: this.registerForm.value.telefonos || '',
      password: this.registerForm.value.password,
      nroLocalidad: this.registerForm.value.nroLocalidad
    };

    this._clienteResource.register(registerData).subscribe({
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
                 text: $localize`¡Bienvenido, ${response.nombre} ${response.apellido}! Tu cuenta ha sido creada exitosamente.`,
                 title: $localize`Registro exitoso`
               });

               // Redirigir a preferencias (segundo paso del registro)
               this._router.navigate(['/preferencias-registro']);
      },
      error: (err) => {
        // El error handler global manejará el error automáticamente
        console.error('Error en registro:', err);
      }
    });
  }

  private marcarCamposInvalidos(): void {
    Object.keys(this.registerForm.controls).forEach(key => {
      const control = this.registerForm.get(key);
      if (control && control.invalid) {
        control.markAsTouched();
      }
    });
  }

  toggleMostrarPassword(): void {
    this.mostrarPassword = !this.mostrarPassword;
  }

  toggleMostrarConfirmPassword(): void {
    this.mostrarConfirmPassword = !this.mostrarConfirmPassword;
  }

  getAriaLabelPassword(): string {
    return this.mostrarPassword ? $localize`Ocultar contraseña` : $localize`Mostrar contraseña`;
  }

  getAriaLabelConfirmPassword(): string {
    return this.mostrarConfirmPassword ? $localize`Ocultar contraseña` : $localize`Mostrar contraseña`;
  }

  get nombre() {
    return this.registerForm.get('nombre');
  }

  get apellido() {
    return this.registerForm.get('apellido');
  }

  get correo() {
    return this.registerForm.get('correo');
  }

  get telefonos() {
    return this.registerForm.get('telefonos');
  }

  get password() {
    return this.registerForm.get('password');
  }

  get confirmPassword() {
    return this.registerForm.get('confirmPassword');
  }

  get nroLocalidad() {
    return this.registerForm.get('nroLocalidad');
  }

}
