import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoaderIcon } from './loader-icon';

describe('LoaderIcon', () => {
  let component: LoaderIcon;
  let fixture: ComponentFixture<LoaderIcon>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoaderIcon]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoaderIcon);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
