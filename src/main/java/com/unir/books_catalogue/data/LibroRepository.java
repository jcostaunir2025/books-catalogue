package com.unir.books_catalogue.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.unir.books_catalogue.controller.model.LibrosQueryResponseAgg;
import com.unir.books_catalogue.data.model.Autor;
import com.unir.books_catalogue.data.model.Categoria;
import com.unir.books_catalogue.data.model.Libro;
import com.unir.books_catalogue.controller.model.AggregationDetails;
import com.unir.books_catalogue.controller.model.LibrosQueryResponse;
import com.unir.books_catalogue.data.model.LibroResponse;
import com.unir.books_catalogue.data.utils.Consts;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.ParsedDateRange;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.unir.books_catalogue.data.model.aut_cat_data.autores;
import static com.unir.books_catalogue.data.model.aut_cat_data.categorias;
import static com.unir.books_catalogue.data.utils.Consts.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LibroRepository {

    @Value("${server.fullAddress}")
    private String serverFullAddress;

    private final LibroESRepository libroESRepository;
    private final ElasticsearchOperations elasticClient;

    private final String[] tituloSearchFields = {"titulo_libro", "titulo_libro._2gram", "titulo_libro._3gram"};

    public Libro save(Libro libro) {
        return libroESRepository.save(libro);
    }

    public Boolean delete(Libro libro) {
        libroESRepository.delete(libro);
        return Boolean.TRUE;
    }

    public Libro findById(String idlibro) {
        return libroESRepository.findById(idlibro);
    }

    @SneakyThrows
    public LibrosQueryResponse findLibros(String titulo, String autor, String fechapub, String categoria, String isbn,
                                          String valoracion, Boolean visible, Integer page, Boolean aggregate) {

        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(titulo)) {
            querySpec.must(QueryBuilders.multiMatchQuery(titulo, tituloSearchFields).type(Type.BOOL_PREFIX));
        }

        if (!StringUtils.isEmpty(autor)) {
            querySpec.must(QueryBuilders.termQuery(IDAUTLIB, autor));
        }

        if (!StringUtils.isEmpty(fechapub)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date fechapubdate = sdf.parse(fechapub);
                querySpec.must(QueryBuilders.termQuery(FECHAPUB, fechapubdate));
            } catch (ParseException e) {
                System.err.println("Error parsing date: " + e.getMessage());
            }
        }

        if (!StringUtils.isEmpty(categoria)) {
            querySpec.must(QueryBuilders.termQuery(IDCATLIB, categoria));
        }

        if (!StringUtils.isEmpty(isbn)) {
            querySpec.must(QueryBuilders.termQuery(ISBN, isbn));
        }

        if (!StringUtils.isEmpty(valoracion)) {
            querySpec.must(QueryBuilders.termQuery(VALORACION, valoracion));
        }


        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        querySpec.must(QueryBuilders.termQuery(VISIBLE, true));

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        if (aggregate) {
//            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("Titulo Aggregation").field("titulo").size(1000));
//            // Mas info sobre size 1000 - https://www.elastic.co/guide/en/elasticsearch/reference/7.10/search-aggregations-bucket-terms-aggregation.html#search-aggregations-bucket-terms-aggregation-size
//            nativeSearchQueryBuilder.withMaxResults(0);
        }

        nativeSearchQueryBuilder.withPageable(PageRequest.of(page, 10));

        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Libro> result = elasticClient.search(query, Libro.class);

        List<AggregationDetails> responseAggs = new LinkedList<>();

        if (result.hasAggregations()) {
//            Map<String, Aggregation> aggs = result.getAggregations().asMap();
//            ParsedStringTerms countryAgg = (ParsedStringTerms) aggs.get("Titulo Aggregation");
//
//            //Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
//            String queryParams = getQueryParams(autor, fechapub, categoria, isbn, valoracion);
//            countryAgg.getBuckets()
//                    .forEach(
//                            bucket -> responseAggs.add(
//                                    new AggregationDetails(
//                                            bucket.getKey().toString(),
//                                            (int) bucket.getDocCount(),
//                                            serverFullAddress + "/books?titulo=" + bucket.getKey() + queryParams)));
        }
        return new LibrosQueryResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList(), responseAggs);
    }

    @SneakyThrows
    public LibrosQueryResponseAgg findLibrosAgg(List<String> fechapubValues, List<String> stockValues, List<String> precioValues, String titulo, String page){
        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(titulo)) {
            querySpec.must(QueryBuilders.multiMatchQuery(titulo, tituloSearchFields).type(Type.BOOL_PREFIX));
        }

        querySpec = setRangeValues(fechapubValues, querySpec, FECHAPUB, "/");
        querySpec = setRangeValues(stockValues, querySpec, STOCK, "-");
        querySpec = setRangeValues(precioValues, querySpec, PRECIO, "-");

        if(!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .dateRange(AGG_KEY_RANGE_FECHAPUB)
                .field(FECHAPUB)
                .addUnboundedTo("2022-12-31")
                .addRange("2023-01-01", "2024-12-31")
                .addUnboundedFrom("2025-01-01"));

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .range(AGG_KEY_RANGE_STOCK)
                .field(STOCK)
                .addUnboundedTo(Consts.AGG_KEY_RANGE_STOCK_0,11)
                .addRange(Consts.AGG_KEY_RANGE_STOCK_1, 11, 30)
                .addUnboundedFrom(Consts.AGG_KEY_RANGE_STOCK_2,30));

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .range(AGG_KEY_RANGE_PRECIO)
                .field(PRECIO)
                .addUnboundedTo(Consts.AGG_KEY_RANGE_PRECIO_0,31)
                .addRange(Consts.AGG_KEY_RANGE_PRECIO_1, 31, 50)
                .addUnboundedFrom(Consts.AGG_KEY_RANGE_PRECIO_2,50));

        nativeSearchQueryBuilder.withMaxResults(10);

        int pageInt = Integer.parseInt(page);
        if (pageInt >= 0) {
            nativeSearchQueryBuilder.withPageable(PageRequest.of(pageInt,10));
        }

        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Libro> result = elasticClient.search(query, Libro.class);
        return new LibrosQueryResponseAgg(getResponseLibros(result), getResponseAggregations(result));

    }

    public List<LibroResponse> getLibroResponsesMapping(List<Libro> libros) {
        List<LibroResponse> libroResponses = new ArrayList<>();

        libros.forEach(libro -> {
            libroResponses.add(getLibroResponseMapping(libro));
        });

        return libroResponses;
    }

    public LibroResponse getLibroResponseMapping(Libro libro){
        var newLibro = new LibroResponse();
        for (Autor aut : autores) {
            if (aut.idautor_libro.equals(libro.getIdautor())){
                newLibro.setIdautor(aut.idautor_libro);
                newLibro.autor = aut.autor_nombre;
            }
        }
        for (Categoria cate : categorias) {
            if (cate.idcategoria_libro.equals(libro.getIdcategoria())){
                newLibro.setIdcategoria(cate.idcategoria_libro);
                newLibro.categoria = cate.categoria_nombre;
            }
        }
        newLibro.setIdlibro(libro.getIdlibro());
        newLibro.setTitulo(libro.getTitulo());
        newLibro.setIsbn(libro.getIsbn());
        newLibro.setValoracion(libro.getValoracion());
        newLibro.setFechapub(libro.getFechapub());
        newLibro.setPrecio(libro.getPrecio());
        newLibro.setVisible(libro.getVisible());
        newLibro.setStock(libro.getStock());

        return newLibro;
    }

    private List<LibroResponse> getResponseLibros(SearchHits<Libro> result) {
        List<Libro> response = result.getSearchHits().stream().map(SearchHit::getContent).toList();
        return getLibroResponsesMapping(response);
    }

    private Map<String, List<AggregationDetails>> getResponseAggregations(SearchHits<Libro> result) {

        Map<String, List<AggregationDetails>> responseAggregations = new HashMap<>();

        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();

            aggs.forEach((key, value) -> {

                if(!responseAggregations.containsKey(key)) {
                    responseAggregations.put(key, new LinkedList<>());
                }

                if (value instanceof ParsedRange parsedRange) {
                    parsedRange.getBuckets().forEach(bucket -> {
                        responseAggregations.get(key).add(new AggregationDetails(bucket.getKeyAsString(), (int) bucket.getDocCount()));
                    });
                }
            });
        }
        return responseAggregations;
    }

//    private String getQueryParams(String autor, String fechapub, String categoria, String isbn,
//                                  String valoracion) {
//        String queryParams = (StringUtils.isEmpty(autor) ? "" : "&autor=" + autor)
//                + (StringUtils.isEmpty(fechapub) ? "" : "&fechapub=" + fechapub)
//                + (StringUtils.isEmpty(categoria) ? "" : "&categoria=" + categoria)
//                + (StringUtils.isEmpty(isbn) ? "" : "&isbn=" + isbn)
//                + (StringUtils.isEmpty(valoracion) ? "" : "&valoracion=" + valoracion);
//        // Eliminamos el ultimo & si existe
//        return queryParams.endsWith("&") ? queryParams.substring(0, queryParams.length() - 1) : queryParams;
//    }

    private BoolQueryBuilder setRangeValues(List<String> rangeValues, BoolQueryBuilder querySpec, String fieldName, String splitter) {
        if (rangeValues != null && !rangeValues.isEmpty()) {
            rangeValues.forEach(
                    item -> {
                        String[] itemRange = item != null && item.contains(splitter) ? item.split(splitter) : new String[]{};

                        if (itemRange.length == 2) {
                            if ("".equals(itemRange[0])) {
                                querySpec.must(QueryBuilders.rangeQuery(fieldName).to(itemRange[1]).includeUpper(false));
                            } else {
                                querySpec.must(QueryBuilders.rangeQuery(fieldName).from(itemRange[0]).to(itemRange[1]).includeUpper(false));
                            }
                        } if (itemRange.length == 1) {
                            querySpec.must(QueryBuilders.rangeQuery(fieldName).from(itemRange[0]));
                        }
                    }
            );
        }

        return querySpec;
    }
}