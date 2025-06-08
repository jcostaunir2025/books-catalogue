package com.unir.books_catalogue.controller.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LibroDto {
	private String titulo;
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date fechapub;
	private String valoracion;
	private Integer stock;
	private Integer precio;
}
