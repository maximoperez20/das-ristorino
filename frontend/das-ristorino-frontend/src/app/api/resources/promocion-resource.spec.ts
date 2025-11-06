import { TestBed } from '@angular/core/testing';

import { PromocionResource } from './promocion-resource';

describe('PromocionResource', () => {
  let service: PromocionResource;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PromocionResource);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
