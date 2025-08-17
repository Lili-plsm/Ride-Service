package ru.site.datasource.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.site.datasource.model.Client;
import ru.site.datasource.repository.ClientRepository;

@Service
public class ClientRepositoryServiceImpl implements ClientRepositoryService {

  private final ClientRepository clientRepository;

  public ClientRepositoryServiceImpl(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public void saveClient(Client client) {
    clientRepository.save(client);
  }

  public Long getClientIdByUserId(Long userId) {
    Optional<Client> optObj = clientRepository.findByUserId(userId);
    return optObj.orElseThrow(() -> new NoSuchElementException("Клиент не найден")).getId();
  }
}
