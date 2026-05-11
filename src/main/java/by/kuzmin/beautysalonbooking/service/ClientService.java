package by.kuzmin.beautysalonbooking.service;

import by.kuzmin.beautysalonbooking.dto.ClientProfileDto;
import by.kuzmin.beautysalonbooking.dto.ClientUpdateDto;
import by.kuzmin.beautysalonbooking.entity.Client;
import by.kuzmin.beautysalonbooking.mapper.ClientMapper;
import by.kuzmin.beautysalonbooking.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    public ClientProfileDto getClientProfile(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден с id: " + clientId));
        return clientMapper.toProfileDto(client);
    }

    @Transactional(readOnly = true)
    public Client getClientEntity(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден с id: " + clientId));
    }

    @Transactional
    public ClientProfileDto updateClientProfile(Long clientId, ClientUpdateDto updateDto) {
        Client client = getClientEntity(clientId);
        clientMapper.updateEntityFromDto(updateDto, client);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toProfileDto(savedClient);
    }

    @Transactional(readOnly = true)
    public long getTotalVisits(Long clientId) {
        return clientRepository.countAppointmentsByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public double getTotalSpent(Long clientId) {
        Double spent = clientRepository.sumSpentByClientId(clientId);
        return spent != null ? spent : 0.0;
    }

    @Transactional(readOnly = true)
    public OffsetDateTime getLastVisit(Long clientId) {
        return clientRepository.getLastVisitDate(clientId);
    }

}