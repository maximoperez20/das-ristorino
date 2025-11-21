import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-buscador-nlp',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './buscador-nlp.html',
  styleUrls: ['./buscador-nlp.scss'],
})
export class BuscadorNLPComponent {

  buscarForm: FormGroup;
  
  private _fb = inject(FormBuilder);
  private _router = inject(Router);

  constructor() {
    this.buscarForm = this._fb.group({
      consulta: ['', [Validators.required, Validators.maxLength(500)]]
    });
  }

  onSubmit(): void {
    if (this.buscarForm.invalid) {
      this.buscarForm.markAllAsTouched();
      return;
    }

    const consulta = this.buscarForm.value.consulta.trim();
    if (consulta) {
      // Navegar a la página de resultados con la consulta como query param
      this._router.navigate(['/buscar'], { 
        queryParams: { q: consulta } 
      });
      
      // Limpiar el formulario
      this.buscarForm.reset();
    }
  }

  get consulta() {
    return this.buscarForm.get('consulta');
  }

}

