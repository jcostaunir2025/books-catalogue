package com.unir.books_catalogue.controller.model;

import com.unir.books_catalogue.data.model.LibroResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LibrosQueryResponseAgg {

    private List<LibroResponse> libros;
    private Map<String, List<AggregationDetails>> aggs;

}
