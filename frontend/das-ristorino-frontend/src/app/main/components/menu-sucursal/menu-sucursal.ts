import { CommonModule } from "@angular/common";
import { Component, Input, OnInit, SimpleChanges, OnChanges, inject } from "@angular/core";
import { IMenu } from "../../api/models/i-menu";
import { RestauranteResource } from "../../api/resources/restaurante-resource";

@Component({
  selector: 'app-menu-sucursal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './menu-sucursal.html',
  styleUrls: ['./menu-sucursal.scss'],
})

export class MenuSucursalComponent implements OnInit, OnChanges {
  @Input() nroRestaurante!: string;
  @Input() nroSucursal!: string;
  menus: IMenu[] = [];

  private _restauranteResource = inject(RestauranteResource);
  
  ngOnInit(): void {
    this.cargarMenus();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['nroRestaurante'] || changes['nroSucursal']) {
      this.cargarMenus();
    }
  }

  cargarMenus(): void {
    this._restauranteResource.obtenerMenusPorSucursal({
      nroRestaurante: this.nroRestaurante,
      nroSucursal: this.nroSucursal
    }).subscribe((menus: IMenu[]) => {
      this.menus = menus;
    });
  }
}