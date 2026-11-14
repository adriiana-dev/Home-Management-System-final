package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.*;
import com.Home_Management_System.Aplication_Api.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock PurchaseRepository purchaseRepository;
    @Mock MarketRepository marketRepository;
    @Mock UserRepository userRepository;
    @Mock ProductRepository productRepository;
    @InjectMocks PurchaseService service;

    private Purchase compra(List<PurchaseItem> itens) {
        User u = new User(); u.setId("u1");
        Market m = new Market(); m.setId("m1");
        Purchase p = new Purchase();
        p.setUser(u); p.setMarket(m); p.setPurchasedItems(itens);
        return p;
    }

    private PurchaseItem item(String prod, String preco, int qtd) {
        PurchaseItem i = new PurchaseItem();
        i.setProductId(prod);
        i.setUnitPrice(new BigDecimal(preco));
        i.setQuantity(qtd);
        return i;
    }

    @Test
    void semItens_retorna400() {
        ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.salvar(compra(List.of())));
        Assertions.assertEquals(400, e.getStatusCode().value());
    }

    @Test
    void usuarioInexistente_retorna404() {
        Mockito.when(userRepository.findById("u1")).thenReturn(Optional.empty());
        ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.salvar(compra(List.of(item("p1", "10.00", 1)))));
        Assertions.assertEquals(404, e.getStatusCode().value());
    }

    @Test
    void marketInexistente_retorna404() {
        Mockito.when(userRepository.findById("u1")).thenReturn(Optional.of(new User()));
        Mockito.when(marketRepository.findById("m1")).thenReturn(Optional.empty());
        ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.salvar(compra(List.of(item("p1", "10.00", 1)))));
        Assertions.assertEquals(404, e.getStatusCode().value());
    }

    @Test
    void calculaTotalAutomaticamente() {
        Mockito.when(userRepository.findById("u1")).thenReturn(Optional.of(new User()));
        Mockito.when(marketRepository.findById("m1")).thenReturn(Optional.of(new Market()));
        Mockito.when(productRepository.existsById(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(purchaseRepository.save(ArgumentMatchers.any())).thenAnswer(inv -> inv.getArgument(0));

        Purchase salva = service.salvar(compra(List.of(
                item("p1", "5.50", 2), item("p2", "7.00", 3))));

        Assertions.assertEquals(new BigDecimal("32.00"), salva.getTotalValue());
    }
}