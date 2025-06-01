package com.unir.books_catalogue.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateLibroRequest {

	private String titulo_libro;
	private String isbn_libro;
	private String fechapub_libro;
	private String valoracion_libro;
	private Integer stock_libro;
	private Boolean visible_libro;
	private Integer idcategoria_libro;
	private Integer idautor_libro;
}
