package com.unir.books_catalogue.data;

import com.unir.books_catalogue.data.model.Libro;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
interface LibroESRepository extends ElasticsearchRepository<Libro, String> {

	//List<Libro> findByName(String name);

	Libro findById(String idlibro);

	Libro save(Libro libro);

	void delete(Libro libro);

	//List<Libro> findAll();

}
