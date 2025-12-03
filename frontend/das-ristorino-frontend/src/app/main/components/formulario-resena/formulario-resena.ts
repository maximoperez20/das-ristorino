import { Component, EventEmitter, Input, Output, OnInit, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { IResenaInsertar } from '../../api/models/i-resena-insertar';
import { ResenaResource } from '../../api/resources/resena-resource';
import { AuthService } from '../../../core/services/auth-service';

@Component({
  selector: 'app-formulario-resena',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './formulario-resena.html',
  styleUrls: ['./formulario-resena.scss']
})
export class FormularioResenaComponent implements OnInit, OnChanges {
  @Input() visible = false;
  @Input() nroRestaurante?: string;
  @Input() nroSucursal?: string;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() resenaEnviada = new EventEmitter<void>();

  resenaForm!: FormGroup;
  enviando = false;
  estrellas = [1, 2, 3, 4, 5];
  errorMsg: string | null = null;
  
  private _fb = inject(FormBuilder);
  private _resenaResource = inject(ResenaResource);
  private _auth = inject(AuthService);

  ngOnInit(): void {
    this.inicializarFormulario();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.resenaForm?.reset({ calificacion: 5 });
      this.errorMsg = null;
    }
  }

  inicializarFormulario(): void {
    this.resenaForm = this._fb.group({
      calificacion: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comentario: ['', [Validators.required, Validators.maxLength(1000)]]
    });
  }

  seleccionarCalificacion(calificacion: number): void {
    this.resenaForm.patchValue({ calificacion });
  }

  cerrarModal(): void {
    this.visible = false;
    this.visibleChange.emit(false);
    this.resenaForm.reset({ calificacion: 5 });
    this.errorMsg = null;
  }

  enviarResena(): void {
    // Forzar visualización de errores del formulario
    if (this.resenaForm.invalid) {
      this.resenaForm.markAllAsTouched();
      this.errorMsg = $localize`Por favor completa los campos requeridos`;
      return;
    }

    // Validar IDs necesarios
    if (!this.nroRestaurante || !this.nroSucursal) {
      this.errorMsg = $localize`No se pudo identificar el restaurante o la sucursal`;
      return;
    }

    const nroCliente = this._auth.getUser()?.nroCliente;
    if (!nroCliente) {
      this.errorMsg = $localize`No se pudo identificar al cliente. Inicia sesión nuevamente.`;
      return;
    }

    this.enviando = true;
    this.errorMsg = null;
    const datos: IResenaInsertar = {
      nroRestaurante: this.nroRestaurante,
      nroSucursal: this.nroSucursal,
      nroCliente: nroCliente,
      calificacion: this.resenaForm.value.calificacion,
      comentario: this.resenaForm.value.comentario
    };

    this._resenaResource.insertarResena(datos).subscribe({
      next: () => {
        this.enviando = false;
        this.resenaEnviada.emit();
        this.cerrarModal();
      },
      error: (err) => {
        console.error('Error al enviar reseña', err);
        this.errorMsg = $localize`Ocurrió un error al enviar la reseña. Intenta nuevamente.`;
        this.enviando = false;
      }
    });
  }
}