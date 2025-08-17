package ru.site.datasource.mapper;

import org.springframework.stereotype.Component;
import ru.site.datasource.model.Client;
import ru.site.datasource.model.User;
import ru.site.web.model.ClientCreateRequest;

@Component
public class ClientMapper {

    public Client toEntity(ClientCreateRequest request, User user) {
        Client client = new Client();
        client.setUser(user);
        client.setDefaultAddress(request.getFirstName() + " " +
                                 request.getLastName());
        client.setRating(0.0);
        return client;
    }
}
