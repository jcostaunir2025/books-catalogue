package com.unir.books_catalogue.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unir.books_catalogue.controller.model.LibroDto;
import com.unir.books_catalogue.data.utils.Consts;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

import static com.unir.books_catalogue.data.utils.Consts.*;

@Entity
@Table(name = "libros")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Libro {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idlibro;
	
	@Column(name = TITULO)
	private String titulo;

	@Column(name = ISBN, unique = true)
	private String isbn;

	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(name = Consts.FECHAPUB)
	private Date fechapub;

	@Column(name = VALORACION)
	private String valoracion;

	@Column(name = STOCK)
	private Integer stock;

	@Column(name = VISIBLE)
	private Boolean visible;

	@Column(name = IDCATLIB)
	private Integer idcategoria;

	@Column(name = IDAUTLIB)
	private Integer idautor;

	@Column(name = PRECIO)
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
