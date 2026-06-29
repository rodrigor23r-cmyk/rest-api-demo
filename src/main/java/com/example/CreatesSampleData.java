package com.example;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.entities.Presentation;
import com.example.entities.Product;
import com.example.services.PresentationService;
import com.example.services.ProductService;

@Configuration
public class CreatesSampleData {

    @Bean
    public CommandLineRunner SampleData(ProductService productService, PresentationService presentationService) {

        return args -> {

            // creamos dos presentaciones por unidad y por decenas, para los productos
            presentationService.save(Presentation.builder()
                    .name("unidad")
                    .description("por unidades")
                    .build());

            presentationService.save(Presentation.builder()
                    .name("decenas")
                    .description("por decenas")
                    .build());




            productService.save(Product.builder().name("resma de papel")
                    .description("Descripcion")
                    .price(new BigDecimal(3.75))
                    .stock(10)
                    .presentation(presentationService.findById(1))
                    .build());

            productService.save(Product.builder()
                    .name("Bolígrafos azules")
                    .description("Caja de 50 unidades")
                    .price(new BigDecimal(12.50))
                    .stock(25)
                    .presentation(presentationService.findById(2))
                    .build());

            productService.save(Product.builder()
                    .name("Cuaderno A4")
                    .description("Cuaderno espiral 80 hojas")
                    .price(new BigDecimal(4.20))
                    .stock(50)
                    .presentation(presentationService.findById(1))
                    .build());

            productService.save(Product.builder()
                    .name("Grapadora")
                    .description("Grapadora metálica negra")
                    .price(new BigDecimal(8.90))
                    .stock(15)
                    .presentation(presentationService.findById(1))
                    .build());

            productService.save(Product.builder()
                    .name("Carpetas")
                    .description("Pack de 10 carpetas de cartón")
                    .price(new BigDecimal(5.60))
                    .stock(30)
                    .presentation(presentationService.findById(1))
                    .build());

            productService.save(Product.builder()
                    .name("Marcadores")
                    .description("Set de 4 colores fluorescentes")
                    .price(new BigDecimal(6.30))
                    .stock(40)
                    .presentation(presentationService.findById(2))
                    .build());

            productService.save(Product.builder()
                    .name("Tijeras")
                    .description("Acero inoxidable 17cm")
                    .price(new BigDecimal(3.50))
                    .stock(20)
                    .presentation(presentationService.findById(1))
                    .build());

            productService.save(Product.builder()
                    .name("Notas adhesivas")
                    .description("Taco de 100 hojas amarillas")
                    .price(new BigDecimal(1.80))
                    .stock(100)
                    .presentation(presentationService.findById(2))
                    .build());

            productService.save(Product.builder()
                    .name("Cinta adhesiva")
                    .description("Rollo transparente 33m")
                    .price(new BigDecimal(1.20))
                    .stock(80)
                    .presentation(presentationService.findById(1))
                    .build());

            productService.save(Product.builder()
                    .name("Clips metálicos")
                    .description("Caja de 100 unidades")
                    .price(new BigDecimal(0.95))
                    .stock(60)
                    .presentation(presentationService.findById(2))
                    .build());

            productService.save(Product.builder()
                    .name("Pizarra blanca")
                    .description("Medidas 40x60 cm con marco")
                    .price(new BigDecimal(18.00))
                    .stock(5)
                    .presentation(presentationService.findById(1))
                    .build());

            productService.save(Product.builder()
                    .name("Cartucho tinta")
                    .description("Tinta negra compatible")
                    .price(new BigDecimal(22.50))
                    .stock(12)
                    .presentation(presentationService.findById(1))
                    .build());

        };
    }
}
