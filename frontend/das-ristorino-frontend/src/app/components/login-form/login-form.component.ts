import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-login-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent {
  email: string = '';
  password: string = '';

  @Output() submitLogin = new EventEmitter<{ email: string, password: string }>();

  onSubmit() {
    if (this.email && this.password) {
      this.submitLogin.emit({ email: this.email, password: this.password });
    }
  }
}