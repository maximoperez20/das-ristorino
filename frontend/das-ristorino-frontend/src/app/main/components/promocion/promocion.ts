import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterLink } from '@angular/router';
import type { IPromocion } from '../../api/models/i-promocion';
import { CommonModule, DatePipe } from '@angular/common';

@Component({
  selector: 'app-promocion',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './promocion.html',
  styleUrls: ['./promocion.scss'],
})
export class PromocionComponent {
  @Input() promocion?: IPromocion;
  @Output() verPromocion = new EventEmitter<IPromocion>();

  onVerPromocion() {
    if (this.promocion) {
      this.verPromocion.emit(this.promocion);
    }
  }

}
