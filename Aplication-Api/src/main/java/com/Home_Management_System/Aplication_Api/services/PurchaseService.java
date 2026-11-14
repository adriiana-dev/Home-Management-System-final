package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Purchase;
import com.Home_Management_System.Aplication_Api.models.PurchaseItem;
import com.Home_Management_System.Aplication_Api.repositories.MarketRepository;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import com.Home_Management_System.Aplication_Api.repositories.PurchaseRepository;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private MarketRepository marketRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;

    public List<Purchase> listarTodas() { return purchaseRepository.findAll(); }

    public Optional<Purchase> buscarPorId(String id) { return purchaseRepository.findById(id); }

    public Purchase salvar(Purchase p) {
        validarPreencherECalcular(p);
        p.setId(null);
        return purchaseRepository.save(p);
    }

    public Purchase atualizar(String id, Purchase dados) {
        Purchase existente = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Compra não encontrada!"));
        validarPreencherECalcular(dados);
        existente.setMarket(dados.getMarket());
        existente.setUser(dados.getUser());
        existente.setDate(dados.getDate());
        existente.setPurchasedItems(dados.getPurchasedItems());
        existente.setTotalValue(dados.getTotalValue());
        return purchaseRepository.save(existente);
    }

    public void deletar(String id) {
        if (!purchaseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra não encontrada!");
        }
        purchaseRepository.deleteById(id);
    }

    private void validarPreencherECalcular(Purchase p) {

        if (p.getPurchasedItems() == null || p.getPurchasedItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A compra deve possuir pelo menos 1 item!");
        }

        if (p.getUser() == null || vazio(p.getUser().getId())
                || p.getMarket() == null || vazio(p.getMarket().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "user e market (com id) são obrigatórios!");
        }

        for (PurchaseItem i : p.getPurchasedItems()) {
            if (vazio(i.getProductId()) || i.getUnitPrice() == null || i.getQuantity() == null
                    || i.getQuantity() <= 0 || i.getUnitPrice().signum() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cada item precisa de productId, quantity > 0 e unitPrice >= 0!");
            }
        }

        p.setUser(userRepository.findById(p.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado!")));
        p.setMarket(marketRepository.findById(p.getMarket().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supermercado não encontrado!")));
        for (PurchaseItem i : p.getPurchasedItems()) {
            if (!productRepository.existsById(i.getProductId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado: " + i.getProductId());
            }
        }
        if (p.getDate() == null) p.setDate(LocalDateTime.now());


        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItem i : p.getPurchasedItems()) {
            total = total.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
        }
        p.setTotalValue(total.setScale(2, RoundingMode.HALF_UP));
    }

    private boolean vazio(String s) { return s == null || s.isBlank(); }
}