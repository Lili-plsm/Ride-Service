package ru.site.datasource.service;

import org.springframework.stereotype.Service;
import ru.site.datasource.model.Client;

@Service
public interface ClientRepositoryService {
    void saveClient(Client client);
	 Long getClientIdByUserId(Long userId);
}
