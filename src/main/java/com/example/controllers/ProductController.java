package com.example.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Product;
import com.example.services.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * La anotacion @RestController es para que todos los metodos que van a ser
 * creados dentro de este
 * controlador y reciben peticiones a través del protocolo HTTP, mediante los
 * verbos correspondientes
 * (GET, POST, PUT, DELETE, PATCH, etc.) devuelvan o reciban datos en formato de
 * JSON (JavaScript Object Notation)
 *
 *
 * Una API REST esta orientada al recurso, es decir, que el controlador necesita
 * que se le especifique
 * que recurso va a responder, por ejemplo en esto seria /products, y en
 * dependencia del verbo del protocolo
 * HTTP se estaria haciendo una peticion (request) concreta. Por ejemplo: Si el
 * verbo es GET, significa
 * que estamos solicitando todos los productos al recurso /products. Si el
 * verbo es POST significa que queremos
 * recibir un producto en formato JSON, en el cuerpo de la peticion (request) y
 * persistirlo (guardarlo) en las tablas correspondientes
 */

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Método que recibe una petición para devolver un listado de todos los
    // productos
    @GetMapping
    public List<Product> getProducts() {

        List<Product> allProducts = productService.findAll();

        return allProducts;
    }
}
