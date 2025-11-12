import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService, AuthResponse } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login and store token', () => {
    const mockResponse: AuthResponse = {
      token: 'fake-jwt-token',
      nroCliente: 1,
      nombre: 'John',
      apellido: 'Doe',
      correo: 'john@example.com'
    };

    service.login('john@example.com', 'password123').subscribe(response => {
      expect(response).toEqual(mockResponse);
      expect(localStorage.getItem('token')).toBe(mockResponse.token);
      expect(localStorage.getItem('userData')).toBeTruthy();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/clientes/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should remove token on logout', () => {
    localStorage.setItem('token', 'fake-token');
    localStorage.setItem('userData', '{"name":"John"}');
    
    service.logout();
    
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('userData')).toBeNull();
  });

  it('should check authentication status', () => {
    expect(service.isLoggedIn()).toBeFalse();
    
    localStorage.setItem('token', 'fake-token');
    expect(service.isLoggedIn()).toBeTrue();
  });
});