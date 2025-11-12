import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClienteService, Cliente, CrearClienteDto } from './cliente.service';
import { AuthService } from './auth.service';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClienteService, AuthService]
    });
    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register new client', () => {
    const mockCliente: CrearClienteDto = {
      nombre: 'John',
      apellido: 'Doe',
      correo: 'john@example.com',
      clave: 'password123'
    };

    const mockResponse: Cliente = {
      nroCliente: 1,
      nombre: 'John',
      apellido: 'Doe',
      correo: 'john@example.com',
      habilitado: true
    };

    service.registrar(mockCliente).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/clientes/register');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should get client details', () => {
    spyOn(authService, 'getToken').and.returnValue('fake-token');

    const mockCliente: Cliente = {
      nroCliente: 1,
      nombre: 'John',
      apellido: 'Doe',
      correo: 'john@example.com',
      habilitado: true
    };

    service.getCliente(1).subscribe(response => {
      expect(response).toEqual(mockCliente);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/clientes/1');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');
    expect(req.request.method).toBe('GET');
    req.flush(mockCliente);
  });
});