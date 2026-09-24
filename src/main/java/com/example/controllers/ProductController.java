package com.example.controllers;

import com.example.models.FileUploadResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Product;
import com.example.services.ProductService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;
import com.example.utilities.FileUtil;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    private final FileDownloadUtil fileDownloadUtil;
    private final ProductService productService;
    private final FileUploadUtil fileUploadUtil; // como tengo lombok ya se inyecta la dependencia y no se instancia
    private final FileUtil fileUtil;

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
     * (La oficial de Jackson)Esta pareja de anotaciones trabaja
     * en equipo. La parte "Managed" es la que se serializa
     * normalmente (el frente), y la parte "Back" es la inversa y
     * se omite en el JSON para evitar la redundancia.En tu
     * entidad Product:java@ManyToOne
     *
     * /@JoinColumn(name = "presentation_id")
     *
     * /@JsonManagedReference // ◄ Se serializa normalmente
     * private Presentation presentation;
     * Usa el código con precaución.En tu entidad Presentation:java
     * /@OneToMany(mappedBy = "presentation")
     * /@JsonBackReference // ◄ Se omite para no repetir el ciclo
     * private List<Product> products;
     */

    @GetMapping
    public ResponseEntity<Map<String, Object>> dameProductos(

            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        List<Product> products = null;
        Map<String, Object> responseAsMap = new HashMap<>();
        String nombre = "name";
        Sort sort = Sort.by(nombre);
        // Sort sort = Sort.by(Sort.Order.asc("name"));

        // comprobar si en la petición me han suministrado page y size
        if (page != null && size != null) {
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

        return new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
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
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);

            } else {
                String failureMessage = "No ha sido encontrado ningún producto con id: " + product_id;

                responseAsMap.put("error: ", failureMessage);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);
            }

        } catch (DataAccessException e) {

            String errorMessage = "Error grave al buscar el producto con id " + product_id
                    + " y la causa más probable es "
                    + e.getMostSpecificCause().getMessage();

            // e.getStackTrace();
            responseAsMap.put("Error grave: ", errorMessage);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;
    }

    /**
     * antes sin foto: public ResponseEntity<Map<String, Object>> saveProduct
     * (@Valid @RequestBody Product product, BindingResult result) {
     * dentro del Request no sólo hay un json sino que hay una imagen
     * y muy importante anotar el método en todos los anotados con Transactional
     * y también especificar el tipo de archivo que ...
     * 
     * @throws IOException
     *                     la anotación de antes: error al nombrar el archivo y
     *                     guardarlo
     */
    @PostMapping(consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<Map<String, Object>> saveProduct(@Valid @RequestPart Product product, BindingResult result,
            @RequestPart(name = "file", required = false) MultipartFile imagenDelProducto) throws IOException {

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
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;
        }
        /**
         * Persisto (guardo) el producto porque está bien formado
         * compruebo si hay imagen para guardarla
         */
        if (imagenDelProducto != null && !imagenDelProducto.isEmpty()) {

            /**
             * agregar prefijo: código alfanumérico aleatorio con método Apache Commons text
             * (Lang3) (dependencia Maven -> pom.xml)
             * 
             * Beans vs Components:
             * Ahora crearemos un componente en el paquete utilities. Dentro habrá un método
             * para guardar la imagen en una carpeta y
             * devuelve un código aleatorio que llevará como prefijo el nombre del fichero
             * original
             * 
             * NIO.2 (entrada salida no bloqueante) si no existe la carpeta la creará.
             */
            String fileCode = fileUploadUtil.saveFile(imagenDelProducto.getOriginalFilename(), imagenDelProducto);

            product.setProductImage(fileCode + '-' + imagenDelProducto.getOriginalFilename());
            /**
             * en el paquete models crearemos un record donde devolveremos al frontend la
             * info de la imagen
             */
            FileUploadResponse fileUploadResponse = new FileUploadResponse(
                    fileCode + '-' + imagenDelProducto.getOriginalFilename(),
                    "/products/fileDownload",
                    imagenDelProducto.getSize());

            responseAsMap.put("información de la imagen del producto", fileUploadResponse);
        }

        try {

            Product productoAGuardar = productService.save(product);
            responseAsMap.put("mensaje: ", "Producto persistido exitósamente!");
            responseAsMap.put("producto persistido: ", productoAGuardar);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.CREATED);

        } catch (DataAccessException e) {

            String errorMessage = "Error grave al guardar el producto y la causa más probable es "
                    + e.getMostSpecificCause().getMessage();
            // e.getStackTrace();
            responseAsMap.put("Error grave: ", errorMessage);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    /**
     * Metodo que recupera la imagen de un producto, dado el codigo que
     * tiene como prefijo el nombre de la imagen
     */

    @GetMapping("/fileDownload/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileCode) {

        Resource resource = null;

        try {

            resource = fileDownloadUtil.getFileAsResource(fileCode);

        } catch (IOException ioe) {

            return ResponseEntity.internalServerError().build();
        }

        if (resource == null)
            return new ResponseEntity<>("imagen no encontrada", HttpStatus.NOT_FOUND);

        /**
         * Si estamos en este punto quiere decir que el fichero (imagen del producto) ha
         * sido
         * encontrado y podemos enviarlo como respuesta a la peticion, como un fichero
         * adjunto
         * en el cuerpo de la respuesta
         */
        String contentType = "application/octet-stream";
        String headerValue = "attachment; fileName=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    /**
     * Metodo que actualiza (update) un producto dado el id del mismo.
     * 
     * Es basicamente igual al metodo que persiste el producto.
     * Respondera a una peticion del tipo siguiente, por ejemplo:
     * 
     * http://localhost:8080/productos/3
     * 
     * Y no habra ambiguedad con el metodo de buscar un producto por el id, porque
     * el verbo utilizado
     * del protocolo HTTP sera diferente, PUT en este caso.
     * 
     */

    @PutMapping("/{id}")
    @Transactional
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateProduct(@Valid @RequestBody Product producto,
            BindingResult results, @PathVariable Integer id) {

        ResponseEntity<Map<String, Object>> responseEntity = null;
        Map<String, Object> responseAsMap = new HashMap<>();

        // Comprobar si el producto recibido en el cuerpo de la peticion tiene errores
        if (results.hasErrors()) {

            // Recuperar todos los errores que tiene el producto
            List<ObjectError> objectErrors = results.getAllErrors();

            // Hay que recorrer la lista de ObjectError para recuperar los mensajes de error
            // por defecto que le voy a mostrar al cliente que ha hecho la peticion,
            // es decir, que ha enviado el producto mal formado

            // Los mensajes de error tienen que ser almacenados en una lista donde cada
            // elemento de la lista
            // sea un String
            List<String> mensajesError = new ArrayList<>();

            objectErrors.stream().forEach(objectError -> mensajesError.add(objectError.getDefaultMessage()));

            responseAsMap.put("errores", mensajesError);
            responseAsMap.put("producto", producto);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }
        /**
         * Si no hay errores vamos a actualizar el producto recibido y devolver
         * informacion
         * al respecto como se requiere para una API REST
         */

        try {
            producto.setId(id);
            Product productoModoficado = productService.save(producto);
            String mensaje = "El producto ha sido modificado exitosamente";
            responseAsMap.put("mensaje", mensaje);
            responseAsMap.put("producto", productoModoficado);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
        } catch (DataAccessException e) {
            String errorMessage = "El producto no se pudo modificar y la causa mas probable es: "
                    + e.getMostSpecificCause();
            responseAsMap.put("error", errorMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    /**
     * ======================================================================================
     * actualizar producto con id recibido en la petición
     * implementación prácticamente igual a la de persistir o save
     */
    /*
     * @PutMapping(value = "/{id}", consumes = "multipart/form-data")
     * 
     * @Transactional
     * public ResponseEntity<Map<String, Object>> updateProduct(
     * 
     * @Valid @RequestPart Product product, BindingResult result,
     * 
     * @RequestPart(name = "file", required = false) MultipartFile
     * imagenDelProducto,
     * 
     * @PathVariable(name = "id", required = true) int product_id) throws
     * IOException {
     * 
     * List<String> mensajesDeError = new ArrayList<>();
     * Map<String, Object> responseAsMap = new HashMap<>();
     * ResponseEntity<Map<String, Object>> responseEntity = null;
     * 
     * // comprobar errores de validación
     * if (result.hasErrors()) {
     * 
     * List<ObjectError> objectErrors = result.getAllErrors();
     * 
     * objectErrors.stream().forEach(objectError -> {
     * mensajesDeError.add(objectError.getDefaultMessage());
     * });
     * 
     * responseAsMap.put("respuesta de error: ", mensajesDeError);
     * responseAsMap.put("producto mal formado: ", product);
     * responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
     * HttpStatus.BAD_REQUEST);
     * 
     * return responseEntity;
     * }
     * 
     * // Persisto (guardo) el producto porque está bien formado
     * // compruebo si hay imagen para guardarla
     * // y en tal caso debo eliminar la imagen del producto
     * 
     * Product productoParaActualizar = productService.findById(product_id);
     * 
     * if (productoParaActualizar == null) {
     * 
     * responseAsMap.put("mensaje de error: ", "producto con id: " + product_id +
     * " no encontrado.");
     * return new ResponseEntity<Map<String, Object>>(responseAsMap,
     * HttpStatus.NOT_FOUND);
     * }
     * 
     * if (imagenDelProducto != null && !imagenDelProducto.isEmpty()) {
     * 
     * 
     * // comprobar si productoParaActualizar tiene imagen y si es así eliminarla
     * 
     * if (productoParaActualizar.getProductImage() != null) {
     * // Eliminar la imagen asociada
     * fileUtil.eliminarArchivo(productoParaActualizar.getProductImage());
     * }
     * 
     * // * agregar prefijo: código alfanumérico aleatorio con método Apache Commons
     * text
     * // * (Lang3) (dependencia Maven -> pom.xml)
     * // *
     * // * Beans vs Components:
     * // * Ahora crearemos un componente en el paquete utilities. Dentro habrá un
     * método
     * // * para guardar la imagen en una carpeta y
     * // * devuelve un código aleatorio que llevará como prefijo el nombre del
     * fichero
     * // * original
     * // *
     * // * NIO.2 (entrada salida no bloqueante) si no existe la carpeta la creará.
     * 
     * String fileCode =
     * fileUploadUtil.saveFile(imagenDelProducto.getOriginalFilename(),
     * imagenDelProducto);
     * 
     * product.setProductImage(fileCode + '-' +
     * imagenDelProducto.getOriginalFilename());
     * 
     * // * en el paquete models crearemos un record donde devolveremos al frontend
     * la
     * // * info de la imagen
     * 
     * FileUploadResponse fileUploadResponse = new FileUploadResponse(
     * fileCode + '-' + imagenDelProducto.getOriginalFilename(),
     * "/products/fileDownload",
     * imagenDelProducto.getSize());
     * 
     * responseAsMap.put("información de la imagen del producto",
     * fileUploadResponse);
     * }
     * 
     * try {
     * product.setId(product_id);
     * Product productoAGuardar = productService.save(product);
     * responseAsMap.put("mensaje: ", "Producto actualizado exitósamente!");
     * responseAsMap.put("producto actualizado: ", productoAGuardar);
     * responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
     * HttpStatus.OK);
     * 
     * } catch (DataAccessException e) {
     * 
     * String errorMessage =
     * "Error grave al actualizado el producto y la causa más probable es "
     * + e.getMostSpecificCause().getMessage();
     * // e.getStackTrace();
     * responseAsMap.put("Error grave: ", errorMessage);
     * 
     * responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
     * HttpStatus.INTERNAL_SERVER_ERROR);
     * }
     * 
     * return responseEntity;
     * 
     * }
     */

    /**
     * ==============================================================================================
     * Metodo para eliminar un producto dado el id
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteProducto(@PathVariable Integer id) {

        ResponseEntity<Map<String, Object>> responseEntity = null;
        var responseAsMap = new HashMap<String, Object>();

        try {

            // recuperar el producto y comprobar si hay foto y eliminar el archivo
            Product productToDelete = productService.findById(id);

            if (productToDelete == null) {

                responseAsMap.put("mensaje de error: ", "producto con id: " + id + " no encontrado.");
                return new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);
            }

            if (productToDelete.getProductImage() != null) {
                fileUtil.eliminarArchivo(productToDelete.getProductImage());
            }

            productService.delete(productService.findById(id));
            String successMessage = "El producto con id " + id + ", ha sido eliminado";
            responseAsMap.put("mensaje", successMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
        } catch (DataAccessException e) {
            String errorMessage = "No ha podido ser eliminado el producto cuyo id es: " + id
                    + ", siendo la causa mas probable: " + e.getMostSpecificCause().getMessage();
            responseAsMap.put("mensaje", errorMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

}
