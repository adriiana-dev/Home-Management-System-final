package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.ShoppingList;
import com.Home_Management_System.Aplication_Api.models.ShoppingListItem;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import com.Home_Management_System.Aplication_Api.repositories.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListService {

    @Autowired private ShoppingListRepository shoppingListRepository;
    @Autowired private ProductRepository productRepository;

    public List<ShoppingList> listarTodas() { return shoppingListRepository.findAll(); }

    public Optional<ShoppingList> buscarPorId(String id) { return shoppingListRepository.findById(id); }

    public ShoppingList salvar(ShoppingList list) {
        validar(list);
        list.setId(null);
        if (list.getDate() == null) list.setDate(LocalDateTime.now());
        return shoppingListRepository.save(list);
    }

    public ShoppingList atualizar(String id, ShoppingList dados) {
        ShoppingList existente = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lista de compras não encontrada!"));
        validar(dados);
        existente.setTitle(dados.getTitle());
        if (dados.getDate() != null) existente.setDate(dados.getDate());
        existente.setItems(dados.getItems());
        return shoppingListRepository.save(existente);
    }

    public void deletar(String id) {
        if (!shoppingListRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista de compras não encontrada!");
        }
        shoppingListRepository.deleteById(id);
    }

    private void validar(ShoppingList l) {
        if (l.getTitle() == null || l.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O título é obrigatório!");
        }
        if (l.getItems() == null || l.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista deve ter pelo menos 1 item!");
        }
        for (ShoppingListItem i : l.getItems()) {
            if (i.getProductId() == null || i.getProductId().isBlank()
                    || i.getQuantityNeeded() == null || i.getQuantityNeeded() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cada item precisa de productId e quantityNeeded > 0!");
            }
            if (i.getIsPurchased() == null) i.setIsPurchased(false);
        }
        for (ShoppingListItem i : l.getItems()) {
            if (!productRepository.existsById(i.getProductId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado: " + i.getProductId());
            }
        }
    }
}