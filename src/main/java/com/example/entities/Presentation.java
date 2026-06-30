package com.example.entities;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

/**
 * Presentation
 */
@Entity
@Table(name = "presentations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Presentation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotNull(message = "la presentacion tiene que tener nombre")
    @NotEmpty(message = "el nombre no puede estar vacío")
    @Size(min = 4, max = 25, message = "el nombre de la presentacion debe estar comprendido entre 4 y 25 caracteres")
    private String name;

    @NotNull(message = "la presentacion tiene que tener una descripcion")
    @NotEmpty(message = "la descripción no puede estar vacío")
    @Size(max = 30, message = "la descripcion de la presentacion no debe superar los 30 caracteres")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "presentation")
    @JsonIgnore
    private List<Product> products;
}
