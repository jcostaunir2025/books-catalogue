package com.unir.books_catalogue.data;

import com.unir.books_catalogue.data.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

interface LibroJpaRepository extends JpaRepository<Libro, Long>, JpaSpecificationExecutor<Libro> {

	List<Libro> findByTitulo(String titulo);

	List<Libro> findByIdautor(Integer idautor);

	List<Libro> findByFechapub(Date fechapub);

	List<Libro> findByIdcategoria(Integer idcategoria);

	List<Libro> findByIsbn(String isbn);

	List<Libro> findByValoracion(String valoracion);

	List<Libro> findByVisible(Boolean visible);

}
