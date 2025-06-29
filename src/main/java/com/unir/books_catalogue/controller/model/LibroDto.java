package com.unir.books_catalogue.controller.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LibroDto {
	private String titulo;
	//@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate fechapub;
	private String valoracion;
	private Integer stock;
	private Integer precio;
}
