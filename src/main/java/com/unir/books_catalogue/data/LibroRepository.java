package com.unir.books_catalogue.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import com.unir.books_catalogue.controller.model.LibrosQueryResponseAgg;
import com.unir.books_catalogue.data.model.Libro;
import com.unir.books_catalogue.controller.model.AggregationDetails;
import com.unir.books_catalogue.controller.model.LibrosQueryResponse;
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
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
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

import static com.unir.books_catalogue.data.utils.Consts.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LibroRepository {

    @Value("${server.fullAddress}")
    private String serverFullAddress;

    // Esta clase (y bean) es la unica que usan directamente los servicios para
    // acceder a los datos.
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



        //Si no he recibido ningun parametro, busco todos los elementos.
        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        //Filtro implicito
        //No le pido al usuario que lo introduzca pero lo aplicamos proactivamente en todas las peticiones
        //En este caso, que los productos sean visibles (estado correcto de la entidad)
        querySpec.must(QueryBuilders.termQuery(VISIBLE, true));

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        if (aggregate) {
//            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("Titulo Aggregation").field("titulo").size(1000));
//            // Mas info sobre size 1000 - https://www.elastic.co/guide/en/elasticsearch/reference/7.10/search-aggregations-bucket-terms-aggregation.html#search-aggregations-bucket-terms-aggregation-size
//            nativeSearchQueryBuilder.withMaxResults(0);
        }

        //Opcionalmente, podemos paginar los resultados
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page, 10));
        //Pagina 0, 10 elementos por pagina. El tam de pagina puede venir como qParam y tambien el numero de pagina

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
    public LibrosQueryResponseAgg findLibrosAgg(List<String> fechapubValues, List<String> stockValues, List<String> precioValues, String valoracion, String titulo, String page){
        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(valoracion)) {
            querySpec.must(QueryBuilders.matchQuery(VALORACION, valoracion));
        }

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
                .terms(Consts.AGG_KEY_RANGE_VALORACION)
                .field(VALORACION).size(10000));

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
        return new LibrosQueryResponseAgg(getResponseEmployees(result), getResponseAggregations(result));

    }

    private List<Libro> getResponseEmployees(SearchHits<Libro> result) {
        return result.getSearchHits().stream().map(SearchHit::getContent).toList();
    }

    private Map<String, List<AggregationDetails>> getResponseAggregations(SearchHits<Libro> result) {

        //Mapa de detalles de agregaciones
        Map<String, List<AggregationDetails>> responseAggregations = new HashMap<>();

        //Recorremos las agregaciones
        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();

            //Recorremos las agregaciones
            aggs.forEach((key, value) -> {

                //Si no existe la clave en el mapa, la creamos
                if(!responseAggregations.containsKey(key)) {
                    responseAggregations.put(key, new LinkedList<>());
                }

                //Si la agregacion es de tipo termino, recorremos los buckets
                if (value instanceof ParsedDateRange parsedDateRange) {
                    parsedDateRange.getBuckets().forEach(bucket -> {
                        responseAggregations.get(key).add(new AggregationDetails(bucket.getKey().toString(), (int) bucket.getDocCount()));
                    });
                }

                //Si la agregacion es de tipo rango, recorremos tambien los buckets
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


//package com.unir.books_catalogue.data;
//
//import com.unir.books_catalogue.data.model.Libro;
//import com.unir.books_catalogue.data.utils.SearchCriteria;
//import com.unir.books_catalogue.data.utils.SearchOperation;
//import com.unir.books_catalogue.data.utils.SearchStatement;
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Repository;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class LibroRepository {
//
//    private final LibroESRepository repository;
//

//    public List<Libro> getLibros() {
//        return repository.findAll();
//    }
//
//    public Libro getById(Long id) {
//        return repository.findById(id).orElse(null);
//    }
//
//    public Libro save(Libro Libro) {
//        return repository.save(Libro);
//    }
//
//    public void delete(Libro Libro) {
//        repository.delete(Libro);
//    }

//    public List<Libro> search(String titulo, String autor, String fechapub, String categoria, String isbn,
//                              String valoracion, Boolean visible) {
//        SearchCriteria<Libro> spec = new SearchCriteria<>();
//
//        if (StringUtils.isNotBlank(titulo)) {
//            spec.add(new SearchStatement("titulo", titulo, SearchOperation.MATCH));
//        }
//
//        if (StringUtils.isNotBlank(autor)) {
//            spec.add(new SearchStatement("idautor", autor, SearchOperation.EQUAL));
//        }
//
//        if (StringUtils.isNotBlank(fechapub)) {
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            Date fechapubdate = null;
//            try {
//                fechapubdate = df.parse(fechapub);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            spec.add(new SearchStatement("fechapub", fechapubdate, SearchOperation.EQUAL));
//        }
//
//        if (StringUtils.isNotBlank(categoria)) {
//            spec.add(new SearchStatement("idcategoria", categoria, SearchOperation.EQUAL));
//        }
//
//        if (StringUtils.isNotBlank(isbn)) {
//            spec.add(new SearchStatement("isbn", isbn, SearchOperation.EQUAL));
//        }
//
//        if (StringUtils.isNotBlank(valoracion)) {
//            spec.add(new SearchStatement("valoracion", valoracion, SearchOperation.EQUAL));
//        }
//
//        if (visible != null) {
//            spec.add(new SearchStatement("visible", visible, SearchOperation.EQUAL));
//        }
//
//        return repository.findAll(spec);
//    }

//}
