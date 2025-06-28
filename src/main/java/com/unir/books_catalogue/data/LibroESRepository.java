package com.unir.books_catalogue.data;

import com.unir.books_catalogue.controller.model.LibrosQueryResponse;
import com.unir.books_catalogue.data.model.Libro;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
interface LibroESRepository extends ElasticsearchRepository<Libro, String> {

	//List<Libro> findByName(String name);

//	LibrosQueryResponse findLibros(String titulo, String autor, String fechapub, String categoria, String isbn,
//								   String valoracion, Boolean visible, Boolean aggregate);

	Libro findById(String idlibro);

	Libro save(Libro libro);

	void delete(Libro libro);

	//List<Libro> findAll();


//	List<Libro> findByTitulo(String titulo);
//
//	List<Libro> findByIdautor(Integer idautor);
//
//	List<Libro> findByFechapub(Date fechapub);
//
//	List<Libro> findByIdcategoria(Integer idcategoria);
//
//	List<Libro> findByIsbn(String isbn);
//
//	List<Libro> findByValoracion(String valoracion);
//
//	List<Libro> findByVisible(Boolean visible);

}
