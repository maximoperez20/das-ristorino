export const restaurantesLista: IRestaurante[] = [
  {
        "id": "16DEE5C3-9355-4F64-9355-FC79BD28DA63",
        "nombre": "Los Aroza SRL",
        "direccion": "Av. Colón 950, Centro",
        "telefono": "351-555-1234",
        "email": null,
        "capacidad": 140,
        "horarioApertura": "16:00:00",
        "horarioCierre": "22:00:00",
        "descripcion": null,
        "categoria": null,
        "calificacion": 4.0,
        "activo": true,
        "imagenUrl": null,
        "diasAtencion": null
    }
]

export interface IRestaurante {
  id: string;
  nombre: string;
  direccion: string;
  telefono: string | null;
  email: string | null;
  capacidad: number;
  horarioApertura: string;
  horarioCierre: string;
  descripcion: string | null;
  categoria: string | null;
  calificacion: number;
  activo: boolean;
  imagenUrl: string | null;
  diasAtencion: string | null;
}
