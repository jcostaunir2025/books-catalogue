package com.unir.books_catalogue.controller.model;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LibroDto {

	private String titulo;
	private String isbn;
	private String fechapub;
	private String valoracion;
	private Integer stock;
	private Integer precio;
	//private Boolean visible;
	//private String idcategoria;
	//private String idautor;

}
