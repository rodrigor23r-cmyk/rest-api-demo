package com.example.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Product implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "el producto tiene que tener nombre")
    @NotEmpty(message = "el nombre no puede estar vacío")
    @Size(min = 4, max = 25, message = "el nombre del producto debe estar comprendido entre 4 y 25 caracteres")
    private String name;

    @NotNull(message = "descripción requerida")
    @NotEmpty(message = "descripción no debe estar vacía")
    @Size(max = 45, message = "no debe superar los 45 caracteres")
    private String description;

    @Min(value = 0, message = "el stock no puede ser negativo")
    private int stock;

    @Min(value = 0, message = "el precio no puede ser negativo")
    private BigDecimal price;

    
    @NotNull(message = "la presentación es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Presentation presentation;

}
