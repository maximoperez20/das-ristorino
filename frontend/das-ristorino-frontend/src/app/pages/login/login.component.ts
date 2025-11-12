import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { LoginFormComponent } from '../../components/login-form/login-form.component';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, LoginFormComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  error: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLoginSubmit(credentials: { email: string, password: string }) {
    this.error = null;
    this.authService.login(credentials.email, credentials.password)
      .subscribe({
        next: () => {
          // Navigate to home page after successful login
          this.router.navigate(['/']);
        },
        error: () => {
          this.error = 'Credenciales inválidas. Por favor intente de nuevo.';
        }
      });
  }
}