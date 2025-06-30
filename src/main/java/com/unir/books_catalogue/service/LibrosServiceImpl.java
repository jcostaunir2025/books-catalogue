package com.unir.books_catalogue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.unir.books_catalogue.controller.model.CreateLibroRequest;
import com.unir.books_catalogue.controller.model.LibroDto;
import com.unir.books_catalogue.controller.model.LibrosQueryResponseAgg;
import com.unir.books_catalogue.data.LibroRepository;
import com.unir.books_catalogue.data.model.Libro;
import com.unir.books_catalogue.data.model.LibroResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Service
@Slf4j
public class LibrosServiceImpl implements LibrosService {

	@Autowired
	private LibroRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
    public List<Libro> getLibros(String titulo, String autor, String fechapub, String categoria, String isbn,
			String valoracion, Boolean visible, Integer page, Boolean aggregate) {

		List<Libro> Libros = repository.findLibros(titulo, autor, fechapub, categoria, isbn, valoracion, visible, page, aggregate).getLibros();
		return Libros.isEmpty() ? null : Libros;
	}

	@Override
	public LibrosQueryResponseAgg getLibrosAgg(List<String> fechapubValues, List<String> stockValues, List<String> precioValues, String titulo, String page) {

		return repository.findLibrosAgg(fechapubValues, stockValues, precioValues, titulo, page);
	}

	@Override
	public Libro getLibro(String LibroId) {
		return repository.findById(LibroId);
	}

	@Override
	public Boolean removeLibro(String LibroId) {

		Libro Libro = repository.findById(LibroId);

		if (Libro != null) {
			repository.delete(Libro);
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public Libro createLibro(CreateLibroRequest request) {

		if (request != null && StringUtils.hasLength(request.getTitulo_libro().trim())
				&& (request.getIdautor_libro() != null && request.getIdautor_libro() > 0)
				&& (request.getFechapub_libro() != null)
				&& (request.getIdcategoria_libro() != null && request.getIdcategoria_libro() > 0)
				&& StringUtils.hasLength(request.getIsbn_libro().trim())
				&& StringUtils.hasLength(request.getValoracion_libro().trim())
				&& (request.getStock_libro() != null && request.getStock_libro() > 0)
				&& (request.getPrecio_libro() != null && request.getPrecio_libro() > 0)
				&& request.getVisible_libro() != null) {

			Libro Libro = com.unir.books_catalogue.data.model.Libro.builder().titulo(request.getTitulo_libro())
					.idautor(request.getIdautor_libro())
					.fechapub(request.getFechapub_libro())
					.idcategoria(request.getIdcategoria_libro())
					.isbn(request.getIsbn_libro())
					.valoracion(request.getValoracion_libro())
					.stock(request.getStock_libro())
					.precio(request.getPrecio_libro())
					.visible(request.getVisible_libro())
					.build();

			return repository.save(Libro);
		} else {
			return null;
		}
	}

	@Override
	public Libro updateLibro(String LibroId, String request) {

		//PATCH se implementa en este caso mediante Merge Patch: https://datatracker.ietf.org/doc/html/rfc7386
		Libro Libro = repository.findById(LibroId);
		if (Libro != null) {
			try {
				JsonMergePatch jsonMergePatch = JsonMergePatch.fromJson(objectMapper.readTree(request));
				JsonNode target = jsonMergePatch.apply(objectMapper.readTree(objectMapper.writeValueAsString(Libro)));
				Libro patched = objectMapper.treeToValue(target, Libro.class);
				repository.save(patched);
				return patched;
			} catch (JsonProcessingException | JsonPatchException e) {
				log.error("Error updating Libro {}", LibroId, e);
                return null;
            }
        } else {
			return null;
		}
	}

	@Override
	public Libro updateLibro(String LibroId, LibroDto updateRequest) {
		Libro Libro = repository.findById(LibroId);
		if (Libro != null) {
			Libro.update(updateRequest);
			repository.save(Libro);
			return Libro;
		} else {
			return null;
		}
	}

	@Override
	public List<LibroResponse> getLibroResponsesMapping(List<Libro> libros){
		return repository.getLibroResponsesMapping(libros);
	}

	@Override
	public LibroResponse getLibroResponseMapping(Libro libro){
		return repository.getLibroResponseMapping(libro);
	}
}
