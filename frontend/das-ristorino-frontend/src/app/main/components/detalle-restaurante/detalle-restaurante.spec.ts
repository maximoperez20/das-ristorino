import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalleRestauranteComponent } from './detalle-restaurante';

describe('DetalleRestaurante', () => {
  let component: DetalleRestauranteComponent;
  let fixture: ComponentFixture<DetalleRestauranteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleRestauranteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetalleRestauranteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
