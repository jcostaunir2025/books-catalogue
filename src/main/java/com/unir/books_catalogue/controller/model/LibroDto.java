package com.unir.books_catalogue.controller.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LibroDto {
	private Integer id;
	private String titulo;
	private Date fechapub;
	private String valoracion;
	private Integer stock;
	private Integer precio;
}
