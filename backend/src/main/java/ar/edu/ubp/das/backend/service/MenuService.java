package ar.edu.ubp.das.backend.service;

import ar.edu.ubp.das.backend.client.RestauranteClientFactory;
import ar.edu.ubp.das.backend.dto.restaurante.ObtenerMenuResponse;
import ar.edu.ubp.das.backend.repository.RestauranteRepository;
import ar.edu.ubp.das.backend.dto.restaurante.ObtenerMenuRequest;




import org.springframework.stereotype.Service;

@Service
public class MenuService {

    private final RestauranteRepository restauranteRepository;
    private final RestauranteClientFactory restauranteClientFactory;

    public MenuService(RestauranteClientFactory restauranteClientFactory, RestauranteRepository restauranteRepository) {        
        this.restauranteClientFactory = restauranteClientFactory;
        this.restauranteRepository = restauranteRepository;
    }   

    public ObtenerMenuResponse obtenerMenuSucursal( String nroRestaurante,String nroSucursal) {
        var restauranteClient = restauranteClientFactory.getClient(nroRestaurante);
        ObtenerMenuRequest obtenerMenuRequest = new ObtenerMenuRequest();
        String codSucursalRestaurante = restauranteRepository.obtenerCodSucursalRestaurante(nroRestaurante, nroSucursal);

        if(nroRestaurante==null || nroSucursal==null){
            throw new IllegalArgumentException("nroRestaurante y nroSucursal no pueden ser nulos");
        }

        obtenerMenuRequest.setNroRestaurante(nroRestaurante);
        obtenerMenuRequest.setNroSucursal(codSucursalRestaurante);

        return restauranteClient.obtenerMenu(obtenerMenuRequest);
    }

    
}
