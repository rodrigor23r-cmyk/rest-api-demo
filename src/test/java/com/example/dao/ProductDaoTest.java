package com.example.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.example.entities.Presentation;
import com.example.entities.Product;

@DataJpaTest 
@AutoConfigureTestDatabase(replace = Replace.NONE) // no sustituir nada en la BD
public class ProductDaoTest {

    @Autowired 
    private PresentationDao presentationDao;
    @Autowired 
    private ProductDao productDao;

    private Presentation presentacionPorUnidad;

    private Presentation presentacionPorDecenas;

    private Product productCero;

    @BeforeEach 
    void setUp() {

        presentacionPorUnidad = Presentation.builder()
            .name("unidad")
            .description("por unidades")
            .build();

        presentacionPorDecenas = Presentation.builder()
            .name("decenas")
            .description("por decenas")
            .build();

    }

    @Test
    @DisplayName("Test para probar agregar, salvar o persistir un producto")
    void testSaveProduct() {

        // given

        Presentation presentationCero = presentationDao.save(presentacionPorUnidad);

        productCero = Product.builder()
            .name("Google Pixel Pro XX")
            .description("Smart Phone")
            .price(new BigDecimal(900))
            .presentation(presentationCero)
            .build();

        // when

        Product productGuardado = productDao.save(productCero);

        // then

        assertThat(productGuardado).isNotNull();
        assertThat

    }
}
