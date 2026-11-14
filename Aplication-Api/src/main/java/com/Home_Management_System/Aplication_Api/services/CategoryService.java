package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Category;
import com.Home_Management_System.Aplication_Api.repositories.CategoryRepository;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;

    public List<Category> listarTodas() { return categoryRepository.findAll(); }

    public Optional<Category> buscarPorId(String id) { return categoryRepository.findById(id); }

    public Category salvar(Category c) {
        validar(c);
        if (categoryRepository.existsByNameIgnoreCase(c.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma categoria com este nome!");
        }
        c.setId(null);
        return categoryRepository.save(c);
    }

    public Category atualizar(String id, Category dados) {
        Category existente = categoryRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!"));
        validar(dados);
        categoryRepository.findByNameIgnoreCase(dados.getName()).ifPresent(outra -> {
            if (!outra.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma categoria com este nome!");
            }
        });
        existente.setName(dados.getName());
        return categoryRepository.save(existente);
    }

    public void deletar(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!");
        }
        boolean emUso = productRepository.findAll().stream()
                .anyMatch(p -> p.getCategory() != null && id.equals(p.getCategory().getId()));
        if (emUso) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir: existem produtos nesta categoria!");
        }
        categoryRepository.deleteById(id);
    }

    private void validar(Category c) {
        if (c.getName() == null || c.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome da categoria é obrigatório!");
        }
    }
}