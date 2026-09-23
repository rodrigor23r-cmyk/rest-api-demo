package com.example.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.entities.Presentation;
import com.example.entities.Product;
import com.example.services.ProductService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;
import com.example.utilities.FileUtil;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * webMvcTest es la anotación para hacer test de integración en el controller.
 * No valdría si tuviéramos implementado Spring Security. Then We have to use
 * the annotation: 
 * "@SpringBootTest"


 * INNECESARIOs:
 
* "@AutoConfigureTestDatabase(replace = Replace.NONE)"
¿Por qué? - No se toca la BD aquí...
 * Para usar la base de datos real y no una en memoria H2. Esto es útil para
 testear la capa de repositorio.
 Al terminar los test, la BD se queda como estaba antes.
 
 
 * "@AutoConfigureMockMvc"
¿Por qué? - Es redundante. Su uso YA lo incluye la anotación "@WebMvcTest"
 Para usar MockMvc y poder hacer peticiones HTTP a los endpoints del
 controller. 
 

La única anotación necesaria:*/
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductService productService;

    @MockitoBean
    FileDownloadUtil fileDownloadUtil;

    @MockitoBean
    FileUploadUtil fileUploadUtil;

    @MockitoBean 
    FileUtil fileUtil;

    // no se usa en testDameProductos() pero SÍ en save y update!
    @Autowired
    ObjectMapper objectMapper;


    Product product1;
    Product product2;
    List<Product> productsList;

    @BeforeEach
    void setUp() {

        Presentation presentation1 = Presentation.builder()
                .name("decenas")
                .description("Por decenas")
                .build();

        product1 = Product.builder()
                .name("Canon ES800")
                .description("Un pepino de cámara")
                .price(new BigDecimal(1500))
                .stock(400)
                .productImage(null)
                .presentation(presentation1)
                .build();

        Presentation presentation2 = Presentation.builder()
                .name("unidades")
                .description("Por unidades")
                .build();

        product2 = Product.builder()
                .name("HP frigorífico")
                .description("Un pepino de frigorífico")
                .price(new BigDecimal(1000))
                .stock(200)
                .productImage(null)
                .presentation(presentation2)
                .build();

        productsList = new ArrayList<>();
        productsList.add(product1);
        productsList.add(product2);
    }

    @Test
    @DisplayName("controller test que recupera todos los productos")
    void testDameProductos() throws Exception {

        // given
        given(productService.findAll(Sort.by("name"))).willReturn(productsList);

        // when
        ResultActions response = mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON));

        // then
        // OJO!!! andDo(print()) va primero.
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos", hasSize(productsList.size())))
                .andExpect(jsonPath("$.productos[0].name", is("Canon ES800")))
                .andExpect(jsonPath("$.productos[1].name", is("HP frigorífico")));

    }


    @Test
    @DisplayName("ControllerTest para persistir un producto")
    void testSaveProduct() throws Exception {

        // given
        given(productService.save(any(Product.class)))
                .willAnswer(invocation->invocation.getArgument(0));
        
        // when

        String jsonStringProduct = objectMapper.writeValueAsString(product1);

        // estamos simulando los campos del Postman, que son los que se envían en la petición HTTP.
        MockMultipartFile byteArrayProducto = new MockMultipartFile(
                "product",
                null,
                "Application/json",
                jsonStringProduct.getBytes());

        ResultActions response = mockMvc.perform(multipart("/products")
                .file("file", null)
                .file(byteArrayProducto));

        // then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['producto persistido: '].name", is("Canon ES800")))
                .andExpect(jsonPath("$['producto persistido: '].name", is(product1.getName()))) // es la misma comprobación que la línea superior.
                .andExpect(jsonPath("$['producto persistido: '].description", is("Un pepino de cámara")))
                .andExpect(jsonPath("$['producto persistido: '].price", is(1500)))
                .andExpect(jsonPath("$['producto persistido: '].stock", is(400)))
                .andExpect(jsonPath("$['producto persistido: '].presentation.name", is("decenas")))
                .andExpect(jsonPath("$['producto persistido: '].presentation.description", is("Por decenas")));
        
    }

    @Test
    @DisplayName ("Test de controller para recuperar un producto por su Id")
    void testFindProductById() throws Exception {

        // given

        int productId =1;

        given(productService.findById(productId)).willReturn(product1);

        // when

        ResultActions response = mockMvc.perform(get("/products/{Id}", productId));
        
        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['producto encontrado: '].name", is(product1.getName())))
                .andExpect(jsonPath("$['producto encontrado: '].description", is(product1.getDescription())));
    }


    @Test 
    @DisplayName("testProductoNoEncontrado")
    void testProductoNoEncontrado() throws Exception {

        given(productService.findById(20)).willReturn(null);

        mockMvc.perform(get("/products/{id}", 20))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    
    
    
    @Test
    void testDeleteProducto() {

    }

    @Test
    void testDownloadFile() {

    }

    

    @Test
    void testUpdateProduct() {

    }



}
