package com.unir.books_catalogue.service;

import com.unir.books_catalogue.controller.model.CreateLibroRequest;
import com.unir.books_catalogue.controller.model.LibroDto;
import com.unir.books_catalogue.controller.model.LibrosQueryResponse;
import com.unir.books_catalogue.controller.model.LibrosQueryResponseAgg;
import com.unir.books_catalogue.data.model.Libro;

import java.util.List;

public interface LibrosService {

	List<Libro> getLibros(String titulo, String autor, String fechapub, String categoria, String isbn,
						  String valoracion, Boolean visible, Integer page, Boolean aggregate);

	LibrosQueryResponseAgg getLibrosAgg(List<String> fechapubValues, List<String> stockValues,
										List<String> precioValues, String valoracion, String titulo, String page);

	Libro getLibro(String LibroId);

	Boolean removeLibro(String LibroId);

	Libro createLibro(CreateLibroRequest request);

	Libro updateLibro(String LibroId, String updateRequest);

	Libro updateLibro(String LibroId, LibroDto updateRequest);

}
