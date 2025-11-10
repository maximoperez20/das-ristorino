import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PromocionComponent } from './promocion';

describe('PromocionComponent', () => {
  let component: PromocionComponent;
  let fixture: ComponentFixture<PromocionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PromocionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PromocionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
