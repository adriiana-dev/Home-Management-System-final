package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Budget;
import com.Home_Management_System.Aplication_Api.repositories.BudgetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget criar(Budget b) {
        validar(b);
        if (budgetRepository.existsByMonthIgnoreCase(b.getMonth())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um orçamento para este mês!");
        }
        b.setId(null);
        b.setLimitAmount(b.getLimitAmount().setScale(2, RoundingMode.HALF_UP));
        return budgetRepository.save(b);
    }

    public List<Budget> listarTodos() { return budgetRepository.findAll(); }

    public Budget buscarPorId(String id) {
        return budgetRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado!"));
    }

    public Budget buscarPorMes(String month) {
        return budgetRepository.findByMonthIgnoreCase(month).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado para o mês informado!"));
    }

    public Budget atualizar(String id, Budget dados) {
        Budget existente = buscarPorId(id);
        validar(dados);
        if (!existente.getMonth().equalsIgnoreCase(dados.getMonth())
                && budgetRepository.existsByMonthIgnoreCase(dados.getMonth())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um orçamento para este mês!");
        }
        existente.setMonth(dados.getMonth());
        existente.setLimitAmount(dados.getLimitAmount().setScale(2, RoundingMode.HALF_UP));
        return budgetRepository.save(existente);
    }

    public void excluir(String id) {
        budgetRepository.delete(buscarPorId(id));
    }

    private void validar(Budget b) {
        if (b.getMonth() == null || !b.getMonth().matches("\\p{L}+/\\d{4}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O mês deve estar no formato 'Outubro/2026'!");
        }
        if (b.getLimitAmount() == null || b.getLimitAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O limite do orçamento deve ser maior que zero!");
        }
    }
}