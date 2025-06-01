package com.unir.books_catalogue.data;

import com.unir.books_catalogue.data.model.Libro;
import com.unir.books_catalogue.data.utils.Consts;
import com.unir.books_catalogue.data.utils.SearchCriteria;
import com.unir.books_catalogue.data.utils.SearchOperation;
import com.unir.books_catalogue.data.utils.SearchStatement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LibroRepository {

    private final LibroJpaRepository repository;

    public List<Libro> getLibros() {
        return repository.findAll();
    }

    public Libro getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Libro save(Libro Libro) {
        return repository.save(Libro);
    }

    public void delete(Libro Libro) {
        repository.delete(Libro);
    }

    public List<Libro> search(String titulo, String autor, String fechapub, String categoria, String isbn,
                              String valoracion, Boolean visible) {
        SearchCriteria<Libro> spec = new SearchCriteria<>();

        if (StringUtils.isNotBlank(titulo)) {
            spec.add(new SearchStatement("titulo", titulo, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(autor)) {
            spec.add(new SearchStatement("idautor", autor, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(fechapub)) {
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            String fechapubdate = "";
            try {
                fechapubdate = formatter.format(formatter.parse(fechapub));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            spec.add(new SearchStatement("fechapub", fechapubdate, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(categoria)) {
            spec.add(new SearchStatement("idcategoria", categoria, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(isbn)) {
            spec.add(new SearchStatement("isbn", isbn, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(valoracion)) {
            spec.add(new SearchStatement("valoracion", valoracion, SearchOperation.EQUAL));
        }

        if (visible != null) {
            spec.add(new SearchStatement("visible", visible, SearchOperation.EQUAL));
        }

        return repository.findAll(spec);
    }

}
