package com.unir.books_catalogue.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.books_catalogue.controller.model.LibroDto;
import com.unir.books_catalogue.data.utils.Consts;
//import jakarta.persistence.*;
import lombok.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import static com.unir.books_catalogue.data.utils.Consts.*;

@Document(indexName = "libros", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Libro {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String idlibro;

	@Field(type = FieldType.Search_As_You_Type, name = TITULO)
	private String titulo;

	@Field(type = FieldType.Keyword, name = ISBN)
	private String isbn;

	//@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Field(type = FieldType.Date, format = DateFormat.date, name = FECHAPUB)
	private LocalDate fechapub;

	@Field(type = FieldType.Keyword, name = VALORACION)
	private String valoracion;

	@Field(type = FieldType.Integer, name = STOCK)
	private Integer stock;

	@Field(type = FieldType.Boolean, name = VISIBLE)
	private Boolean visible;

	@Field(type = FieldType.Keyword, name = IDCATLIB)
	private Integer idcategoria;

	@Field(type = FieldType.Keyword, name = IDAUTLIB)
	private Integer idautor;

	@Field(type = FieldType.Integer, name = PRECIO)
	private Integer precio;

	public void update(LibroDto libroDto) {
		this.titulo = libroDto.getTitulo();
		this.fechapub = libroDto.getFechapub();
		this.valoracion = libroDto.getValoracion();
		this.stock = libroDto.getStock();
		this.precio = libroDto.getPrecio();
		/*this.visible = libroDto.getVisible();
		this.idcategoria = libroDto.getIdcategoria();
		this.idautor = libroDto.getIdautor();*/
	}
}

/*
	@Field(type = FieldType.Text, name = "name")
	private String name;

	@Field(type = FieldType.Keyword, name = "country")
	private String country;

	@Field(type = FieldType.Search_As_You_Type, name = "description")
	private String description;

	@Field(type = FieldType.Boolean, name = "visible")
	private Boolean visible;
*/
