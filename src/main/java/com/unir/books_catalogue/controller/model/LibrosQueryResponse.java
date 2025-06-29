package com.unir.books_catalogue.controller.model;

import com.unir.books_catalogue.data.model.Libro;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LibrosQueryResponse {

    private List<Libro> libros;
    private List<AggregationDetails> aggs;

}
