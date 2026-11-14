package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.ActivityLog;
import com.Home_Management_System.Aplication_Api.repositories.ActivityLogRepository;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityLogService {

    @Autowired private ActivityLogRepository activityLogRepository;
    @Autowired private UserRepository userRepository;

    public List<ActivityLog> listarTodos() { return activityLogRepository.findAll(); }

    public Optional<ActivityLog> buscarPorId(String id) { return activityLogRepository.findById(id); }

    public ActivityLog salvar(ActivityLog log) {
        if (log.getAction() == null || log.getAction().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A ação é obrigatória!");
        }
        if (log.getUser() == null || log.getUser().getId() == null || log.getUser().getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário (com id) é obrigatório!");
        }
        log.setUser(userRepository.findById(log.getUser().getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!")));
        log.setId(null);
        if (log.getTimestamp() == null) log.setTimestamp(LocalDateTime.now());
        return activityLogRepository.save(log);
    }

    public void deletar(String id) {
        if (!activityLogRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Log não encontrado!");
        }
        activityLogRepository.deleteById(id);
    }
}