import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaResource } from '../../api/resources/reserva-resource';
import { AuthService } from '../../../core/services/auth-service';
import { Router } from '@angular/router';
import { AppMessageService } from '../../../core/services/app-message-service';
import { DateUtilsService } from '../../../core/services/date-utils.service';
import type { HorarioSeleccionado } from '../horarios-disponibles/horarios-disponibles';
import type { IDominioPreferencia } from '../../api/models/i-dominio-preferencia';
import type { IHorariosDisponiblesResponse } from '../../api/models/i-horario-disponible';
import type { IHorario } from '../../api/models/i-horario-disponible';
import type { IReserva } from '../../api/models/i-reserva';
import { PreferenciaResource } from '../../api/resources/preferencia-resource';
import { RestauranteResource } from '../../api/resources/restaurante-resource';
import type { IModificarReservaRequest } from '../../api/models/i-modificar-reserva-request';

@Component({
  selector: 'app-formulario-modificar-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formulario-modificar-reserva.html',
  styleUrls: ['./formulario-modificar-reserva.scss'],
})
export class FormularioModificarReservaComponent implements OnInit, OnChanges {
  // Propiedades para almacenar los datos
  especialidadesAlimentariasDisponibles: IDominioPreferencia[] = [];
  disponibilidadRestaurante: IHorariosDisponiblesResponse | null = null;
  
  @Input() reservaSeleccionada!: IReserva;

  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() reservaModificada = new EventEmitter<void>();
  
  zonasDisponibles: {codZona: string, nomZona: string}[] = [];
  horarios: IHorario[] = [];

  zonaSeleccionada: string | null = null;
  horaSeleccionada: string | null = null; // ✅ Agregar esta propiedad
  fechaSeleccionada: string | null = null;

  cantAdultos: number = 0;
  cantMenores: number = 0;
  loading = false;
  error: string | null = null;
  especialidadesAlimentariasSeleccionadas: number[] = [];

  private _reservaResource = inject(ReservaResource);
  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _messageService = inject(AppMessageService);
  private _cdr = inject(ChangeDetectorRef);
  private _dateUtils = inject(DateUtilsService);
  private _restauranteResource = inject(RestauranteResource);
  private _preferenciaResource = inject(PreferenciaResource);

  ngOnInit(): void {
    // Cargar datos cuando el componente se inicializa
    if (this.reservaSeleccionada) {
      this.obtenerHorariosDisponibles(this.reservaSeleccionada);
      this.obtenerPreferenciasAlimentarias(this.reservaSeleccionada);
    }

    // Inicializar propiedades separadas desde la reserva
    this.cantAdultos = this.reservaSeleccionada.cant_adultos ?? 1;
    this.cantMenores = this.reservaSeleccionada.cant_menores ?? 0;
    
    // ✅ Inicializar la hora seleccionada desde la reserva
    const fechaHora = this.reservaSeleccionada.fecha_hora.split('T');
    this.fechaSeleccionada = fechaHora[0];
    if (fechaHora[1]) {
      this.horaSeleccionada = fechaHora[1];
    }
    
    // ✅ Inicializar la zona seleccionada
    this.zonaSeleccionada = this.reservaSeleccionada.codZona;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']) {
      // Forzar detección de cambios
      this._cdr.detectChanges();
    }
  }

  onZonaSeleccionada(codZona: string): void {
    this.zonaSeleccionada = codZona;
    this.horarios = this.disponibilidadRestaurante?.zonas.find(zona => zona.codZona === codZona)?.horarios ?? [];
    // ✅ Resetear la hora seleccionada cuando cambia la zona
    if (this.horarios.length > 0) {
      this.horaSeleccionada = this.horarios[0].horaDesde;
    } else {
      this.horaSeleccionada = null;
    }
  }

  getHoraSeleccionada(): string {
    return this.reservaSeleccionada.fecha_hora.split('T')[1] ?? '';
  }
  
  cerrar(): void {
    this.visible = false;
    this.visibleChange.emit(false);
    this.error = null;
  }

  /**
   * Método para confirmar la modificación de la reserva
   * Lee todos los datos del formulario y los envía al API
   */
  confirmarModificacionReserva(): void {
    // 1. Validar autenticación
    if (!this._auth.isAuthenticated()) {
      this._router.navigate(['/login'], { 
        queryParams: { returnUrl: this._router.url } 
      });
      return;
    }

    // 2. Validar datos requeridos
    if (this.cantAdultos < 1) {
      this.error = $localize`Debe haber al menos 1 adulto`;
      return;
    }

    if (this.cantMenores < 0) {
      this.error = $localize`La cantidad de menores no puede ser negativa`;
      return;
    }

    if (!this.zonaSeleccionada) {
      this.error = $localize`Debe seleccionar una zona`;
      return;
    }

    if (!this.horaSeleccionada) {
      this.error = $localize`Debe seleccionar un horario`;
      return;
    }

    // 3. Preparar los datos del formulario
    const datosModificacion: IModificarReservaRequest = {
      nroReserva: this.reservaSeleccionada.id,
      nroRestaurante: this.reservaSeleccionada.nroRestaurante,
      nroSucursal: this.reservaSeleccionada.nroSucursal,
      codZona: this.zonaSeleccionada,
      fechaReserva: this.fechaSeleccionada ?? '',
      horaDesde: this.horaSeleccionada,
      cantAdultos: this.cantAdultos,
      cantMenores: this.cantMenores,
      preferenciasReserva: Array.isArray(this.especialidadesAlimentariasSeleccionadas) 
        ? this.especialidadesAlimentariasSeleccionadas 
        : []
    };

    // 5. Enviar la solicitud
    this.loading = true;
    this.error = null;
    

    this._reservaResource.modificarReserva({
      ...datosModificacion
    }).subscribe({
      next: (response: boolean) => {
        this.loading = false;
        if (response) {
          this._messageService.showSuccess($localize`Reserva modificada exitosamente`);
          this.reservaModificada.emit();
          this.cerrar();
        } else {
          this.error = $localize`Error al modificar la reserva`;
        }
      },
      error: (err) => {
        this.loading = false;
        const errorResponse = err.body || err.error || err;
        
        // Manejar errores con horarios actualizados
        const horarios = errorResponse?.horarios;
        const tieneHorarios = horarios && (
          (horarios.zonas && Array.isArray(horarios.zonas) && horarios.zonas.length > 0) ||
          Array.isArray(horarios)
        );
        
        if (tieneHorarios) {
          const mensajeError = errorResponse.error || errorResponse.message || 
            $localize`El horario seleccionado ya no está disponible. Por favor, seleccione otro horario.`;
          
          // this.cerrar();
          
          this._messageService.showMessage({
            text: mensajeError,
            title: $localize`Horario no disponible`,
            type: 'warning'
          });
        } else {
          this.error = errorResponse?.error || errorResponse?.message || 
            $localize`Error al modificar la reserva`;
        }
      }
    });
  }

  formatearFecha(fecha: Date): string {
    return this._dateUtils.formatearFechaISO(fecha);
  }

  formatearFechaLegible(fecha: Date): string {
    return this._dateUtils.formatearFechaLegible(fecha);
  }

  // get totalPersonas(): number {
  //   return this.cantAdultos + this.cantMenores;
  // }

  
  /**
   * Obtiene los horarios disponibles para la reserva
   * Asigna el resultado a this.disponibilidadRestaurante
   */
  obtenerHorariosDisponibles(reserva: IReserva): void {
    const [fecha] = reserva.fecha_hora.split('T'); // Extraer solo la fecha

    this._restauranteResource.obtenerHorariosDisponibles({
      nroRestaurante: reserva.nroRestaurante,
      nroSucursal: reserva.nroSucursal,
      fecha: fecha,
    }).subscribe({
      next: (response: IHorariosDisponiblesResponse) => {
        // ✅ ASIGNAR el resultado a la propiedad del componente
        this.disponibilidadRestaurante = response;
        
        // Ahora puedes procesar los datos
        this.zonasDisponibles = response.zonas?.map(zona => ({
          codZona: zona.codZona,
          nomZona: zona.nomZona,
        })) ?? [];
        
        // Seleccionar la zona de la reserva actual
        this.zonaSeleccionada = reserva.codZona;
        
        // Cargar los horarios de la zona seleccionada
        this.horarios = response.zonas?.find(zona => zona.codZona === this.zonaSeleccionada)?.horarios ?? [];
        
        console.log('Horarios disponibles cargados:', response);
      },
      error: (err) => {
        console.error('Error al obtener horarios disponibles:', err);
        this.disponibilidadRestaurante = null;
        this.zonasDisponibles = [];
        this.horarios = [];
        // Opcional: mostrar mensaje de error
        // this._messageService.showError('Error al cargar horarios disponibles');
      }
    });
  }

  /**
   * Obtiene las especialidades alimentarias disponibles
   * Asigna el resultado a this.especialidadesAlimentariasDisponibles
   */
  obtenerPreferenciasAlimentarias(reserva: IReserva): void {
    // Validar que nroRestaurante existe
    if (!reserva.nroRestaurante || reserva.nroRestaurante.trim() === '') {
      console.error('nroRestaurante no está disponible');
      this.especialidadesAlimentariasDisponibles = [];
      return;
    }

    this._preferenciaResource.obtenerEspecialidadesAlimentariasPorRestaurante({
      nroRestaurante: reserva.nroRestaurante,
    }).subscribe({
      next: (response: IDominioPreferencia[]) => {
        this.especialidadesAlimentariasDisponibles = response;
        
        // Cargar las preferencias ya seleccionadas de la reserva
        if (reserva.preferenciasValores && Array.isArray(reserva.preferenciasValores)) {
          this.especialidadesAlimentariasSeleccionadas = [...reserva.preferenciasValores];
        }
        
        console.log('Especialidades alimentarias cargadas:', response);
      },
      error: (err) => {
        console.error('Error al obtener especialidades alimentarias:', err);
        this.especialidadesAlimentariasDisponibles = [];
      }
    });
  }

  formatearHora(hora: string | null | undefined): string {
    if (!hora) return '';
    try {
      const horaFormateada = hora.split(':').slice(0, 2).join(':');
      return horaFormateada;
    } catch {
      return hora;
    }
  }

  onCambioEspecialidadAlimentaria(event: Event, especialidad: number): void {
    if (!Array.isArray(this.especialidadesAlimentariasSeleccionadas)) {
      this.especialidadesAlimentariasSeleccionadas = [];
    }
    
    const checkbox = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.especialidadesAlimentariasSeleccionadas.push(especialidad);
    } else {
      this.especialidadesAlimentariasSeleccionadas = this.especialidadesAlimentariasSeleccionadas.filter(e => e !== especialidad);
    }
  }
}

