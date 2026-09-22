package com.example.controllers;

import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
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

import ch.qos.logback.core.util.FileUtil;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * webMvcTest es la anotación para hacer test de integración en el controller.
 * No valdría si tuviéramos implementado Spring Security. Then We have to use
 * the annotation: "@SpringBootTest"
 */
@WebMvcTest(controllers = ProductController.class)
// Para usar la base de datos real y no una en memoria H2. Esto es útil para
// testear la capa de repositorio.
// Al terminar los test, la BD se queda como estaba antes.
@AutoConfigureTestDatabase(replace = Replace.NONE)
// Para usar MockMvc y poder hacer peticiones HTTP a los endpoints del
// controller.
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductService productService;

    @MockitoBean
    FileDownloadUtil fileDownloadUtil;

    @MockitoBean
    FileUploadUtil fileUploadUtil;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean 
    FileUtil fileUtil;

    Product product1;
    Presentation presentation1;
    Presentation presentation2;
    Product product2;
    List<Product> products = new ArrayList<>();

    @BeforeEach
    void setUp() {

        presentation1 = Presentation.builder()
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

        presentation2 = Presentation.builder()
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

        products.add(product1);
        products.add(product2);
    }

    @Test
    @DisplayName("controller test que recupera todos los productos")
    void testDameProductos() throws Exception {

        // given
        given(productService.findAll(Sort.by("name"))).willReturn(products);

        // when
        ResultActions response = mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.productos", is(products.size())));

    }

    @Test
    void testDeleteProducto() {

    }

    @Test
    void testDownloadFile() {

    }

    @Test
    void testFindProductById() {

    }

    @Test
    void testSaveProduct() {

    }

    @Test
    void testUpdateProduct() {

    }

}
