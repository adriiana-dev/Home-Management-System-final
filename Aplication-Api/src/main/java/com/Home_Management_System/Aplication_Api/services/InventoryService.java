package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Inventory;
import com.Home_Management_System.Aplication_Api.repositories.InventoryRepository;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private ProductRepository productRepository;

    public List<Inventory> listarTodos() { return inventoryRepository.findAll(); }

    public Optional<Inventory> buscarPorId(String id) { return inventoryRepository.findById(id); }

    public List<Inventory> estoqueBaixo(Integer max) {
        if (max == null || max < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O limite deve ser maior ou igual a zero!");
        }
        return inventoryRepository.findByQuantityLessThanEqual(max);
    }

    public Inventory salvar(Inventory i) {
        validarEPreencher(i);
        i.setId(null);
        return inventoryRepository.save(i);
    }

    public Inventory atualizar(String id, Inventory dados) {
        Inventory existente = inventoryRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de estoque não encontrado!"));
        validarEPreencher(dados);
        existente.setProduct(dados.getProduct());
        existente.setQuantity(dados.getQuantity());
        existente.setExpirationDate(dados.getExpirationDate());
        return inventoryRepository.save(existente);
    }

    public void deletar(String id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de estoque não encontrado!");
        }
        inventoryRepository.deleteById(id);
    }

    private void validarEPreencher(Inventory i) {
        if (i.getProduct() == null || i.getProduct().getId() == null || i.getProduct().getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto (com id) é obrigatório!");
        }
        if (i.getQuantity() == null || i.getQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A quantidade é obrigatória e não pode ser negativa!");
        }
        i.setProduct(productRepository.findById(i.getProduct().getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado!")));
    }
}