package com.unir.books_catalogue.data.model;

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
	
//	@Column(name = Consts.NAME, unique = true)
//	private String name;
//
//	@Column(name = Consts.COUNTRY)
//	private String country;
//
//	@Column(name = Consts.DESCRIPTION)
//	private String description;
//
//	@Column(name = Consts.VISIBLE)
//	private Boolean visible;

	@Column(name = TITULO)
	private String titulo;

	@Column(name = ISBN, unique = true)
	private String isbn;

	@Column(name = Consts.FECHAPUB)
	private String fechapub;

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
		this.isbn = libroDto.getIsbn();
		this.fechapub = libroDto.getFechapub();
		this.valoracion = libroDto.getValoracion();
		this.stock = libroDto.getStock();
		this.precio = libroDto.getPrecio();
		/*this.visible = libroDto.getVisible();
		this.idcategoria = libroDto.getIdcategoria();
		this.idautor = libroDto.getIdautor();*/
	}

}
