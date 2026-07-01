package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Product;
import com.example.services.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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

    /*
     * =================== ANTIGUO: ====================
     * Método que recibe una petición para devolver un listado de todos los
     * productos
     * 
     * @GetMapping
     * public List<Product> getProducts() {
     * 
     * List<Product> allProducts = productService.findAll();
     * 
     * return allProducts;
     * }
     * ================= NUEVO =======================
     * 
     * 
     * IMPORTANTE!!!
     * 
     * Una API REST tiene que devolver informacion respecto a como ha sido
     * solucionada la peticion (request),
     * por ejemplo: el codigo 200 significa estado OK de la peticion, el codido 201
     * significaria CREATED,
     * el codigo 500 significaria que el servidor no ha podido cumplimentar la
     * peticion, el codigo 401 NO ENCONTRADO,
     * el codigo 403 prohibido, ect. Todos estos codigos se pueden encontrar en el
     * sitio de W3Schools
     * 
     * https://www.w3schools.com/tags/ref_httpmessages.asp
     * 
     */

    /**
     * El metodo siguiente va a responder a una peticion (request) del tipo:
     * 
     * http://localhost:8080/products?page=0&size=3
     * 
     * Donde los parametros page y size seran utilizados para la paginacion, y no
     * seran requeridos, es decir,
     * que no son obligatorios que se suministren. Y en caso de NO ser suministrados
     * (page y size),
     * los productos se van a devolver ordenados.
     * 
     *
     * ============== Solución 2 JERONIMO: =================
     * Usar 
     * /@JsonManagedReference
     * /@JsonBackReference
     *                    (La oficial de Jackson)Esta pareja de anotaciones trabaja
     *                    en equipo. La parte "Managed" es la que se serializa
     *                    normalmente (el frente), y la parte "Back" es la inversa y
     *                    se omite en el JSON para evitar la redundancia.En tu
     *                    entidad Product:java@ManyToOne
     *
     * /@JoinColumn(name = "presentation_id")
     *
     * /@JsonManagedReference // ◄ Se serializa normalmente
     * private Presentation presentation;
     *                       Usa el código con precaución.En tu entidad Presentation:java
     * /@OneToMany(mappedBy = "presentation")
     * /@JsonBackReference // ◄ Se omite para no repetir el ciclo
     * private List<Product> products;
     */

    @GetMapping
    public ResponseEntity<Map<String, Object>> dameProductos(

        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size) {
        
        
        List<Product> products = null;
        Map<String, Object>  responseAsMap = new HashMap<>();
        String nombre = "name";
        Sort sort = Sort.by(nombre);
        // Sort sort = Sort.by(Sort.Order.asc("name"));
        
        // comprobar si en la petición me han suministrado page y size
        if (page !=null && size != null) {
            // Devuelvo los productos paginados

            Pageable pageable = PageRequest.of(page, size, sort);

             Page<Product> productPage = productService.findAll(pageable);
             products = productPage.getContent();
             responseAsMap.put("productos", products);
        } else {

            // Devolver los productos sin paginar pero ordenados
            products = productService.findAll(sort);
            responseAsMap.put("productos", products);
        }

       
        return new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findProductById(
        @PathVariable(name = "id", required = true) int product_id) {

            Map<String, Object> responseAsMap = new HashMap<>();

            ResponseEntity<Map<String, Object>> responseEntity = null;

            try {
                Product product = productService.findById(product_id);
                
                if (product != null) {
                    
                    String successMessage = "El producto con id " + product_id + " ha sido encontrado.";

                    responseAsMap.put("mensaje todo OK: ", successMessage);
                    responseAsMap.put("producto encontrado: ", product);
                    responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.OK);

                } else {
                    String failureMessage = "No ha sido encontrado ningún producto con id: " + product_id;
                    
                    responseAsMap.put("error: ", failureMessage);
                    responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.NOT_FOUND);
                }             
    
            } catch (DataAccessException e) {
                
                String errorMessage = "Error grave al buscar el producto con id " + product_id + " y la causa más probable es " + e.getMostSpecificCause().getMessage();
                //e.getStackTrace();
                responseAsMap.put("Error grave: ", errorMessage);

                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
                
            }



        return responseEntity;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveProduct (@Valid @RequestBody Product product, BindingResult result) {

        List<String> mensajesDeError = new ArrayList<>();
        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        
        // comprobar errores de validación
        if (result.hasErrors()) {

            List<ObjectError> objectErrors = result.getAllErrors();

            objectErrors.stream().forEach(objectError -> {
                mensajesDeError.add(objectError.getDefaultMessage());
            });


        responseAsMap.put("respuesta de error: ", mensajesDeError);
        responseAsMap.put("producto mal formado: ", product);
        responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

        return responseEntity;
        }
        // Persisto (guardo) el producto porque está bien formado
        try {
            
            Product productoAGuardar = productService.save(product);
            responseAsMap.put("mensaje: ", "Producto persistido exitósamente!");
            responseAsMap.put("producto persistido: ", productoAGuardar);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            
            String errorMessage = "Error grave al guardar el producto y la causa más probable es " + e.getMostSpecificCause().getMessage();
                //e.getStackTrace();
                responseAsMap.put("Error grave: ", errorMessage);

                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

}
