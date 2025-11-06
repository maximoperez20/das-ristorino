import { TestBed } from '@angular/core/testing';

import { RestauranteResource } from './restaurante-resource';

describe('RestauranteResource', () => {
  let service: RestauranteResource;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RestauranteResource);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
