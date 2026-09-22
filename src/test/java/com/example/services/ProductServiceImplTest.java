package com.example.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dao.PresentationDao;
import com.example.dao.ProductDao;
import com.example.entities.Presentation;
import com.example.entities.Product;


/**
 * Los test hay que realizarlos en aislamiento. Los test a la capa de servicio ya se consideran
 * test de integración porque dependen de la capa de repositorio. Pero dichas dependencias se
 * simulan con mockito. 
 */
@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @Mock 
    private PresentationDao presentationDao;

    // anotación para que los mock anteriores se inyecten aquí
    @InjectMocks 
    private ProductServiceImpl productServiceImpl;


    Product product1, product2;

    List<Product> productsList;

    // metemos datos falsos
    @BeforeEach 
    void setUp() {

        Presentation presentation = Presentation.builder()
            .name("unidades")
            .description("por unidades")
            .build();

        product1 = Product.builder()
            .name("Google pixel 7")
            .description("teléfono de google")
            .price(new BigDecimal(400))
            .stock(1000)
            .productImage(null)
            .presentation(presentation)
            .build();

        product2 = Product.builder()
            .name("iPhone 17 Pro")
            .description("teléfono de Apple")
            .price(new BigDecimal(1400))
            .stock(1500)
            .productImage(null)
            .presentation(presentation)
            .build();
            
        productsList = new ArrayList<>(); // se reinicia en cada test, explícitamente
        productsList.add(product1);
        productsList.add(product2);
    }


    @Test
    void testDelete() {

    }

    @Test
    void testFindAll2() {

    }

    @Test
    void testFindAll3() {

    }

    @Test
    void testFindById() {

    }

    @Test
    @DisplayName("Test de servicio para persistir un producto.")
    void testSave() {

        // given. -- behavior driven development.--
        given(productDao.save(product1)).willReturn(product1);

        // when
        Product productoGuardado = productServiceImpl.save(product1);

        // then
        assertThat(productoGuardado).isNotNull();

        //set -a; source .env; set +a; ./mvnw spring-boot:run
        //mvn test -Dtest=ProductDaoTest#testSaveProduct
        //mvn test -Dtest=ProductServiceImplTest#testSave
        // ./mvnw test

    }

    @Test
    @DisplayName("Test para recuperar una lista vacía de producto.")
    void testEmptyProductList() {

        // given
        given(productDao.findAll()).willReturn(Collections.emptyList());
        
        // when
        List<Product> products = productServiceImpl.findAll();
        
        // then
        assertThat(products).isEmpty();

    }

    @Test
    @DisplayName("Test para recuperar los dos productos creados.")
    void testFindAll() {

        // given
        given(productDao.findAll()).willReturn(productsList);
        
        // when
        List<Product> products = productServiceImpl.findAll();

        // then
        assertEquals(2, products.size());
    }
}
