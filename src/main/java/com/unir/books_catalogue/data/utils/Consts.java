package com.unir.books_catalogue.data.utils;

public class Consts {

    //Constants for db entity fields

    public static final String TITULO = "titulo_libro";
    public static final String ISBN = "isbn_libro";
    public static final String FECHAPUB = "fechapub_libro";
    public static final String VALORACION = "valoracion_libro";
    public static final String STOCK = "stock_libro";
    public static final String VISIBLE = "visible_libro";
    public static final String IDCATLIB = "idcategoria_libro";
    public static final String IDAUTLIB = "idautor_libro";
    public static final String PRECIO = "precio_unitario";

    public static final String AGG_KEY_RANGE_FECHAPUB = "fechapubValues";

    public static final String AGG_KEY_RANGE_STOCK_0 = "-11";
    public static final String AGG_KEY_RANGE_STOCK_1 = "11-30";
    public static final String AGG_KEY_RANGE_STOCK_2 = "30-";
    public static final String AGG_KEY_RANGE_STOCK = "stockValues";

    public static final String AGG_KEY_RANGE_PRECIO_0 = "-31";
    public static final String AGG_KEY_RANGE_PRECIO_1 = "31-50";
    public static final String AGG_KEY_RANGE_PRECIO_2 = "50-";
    public static final String AGG_KEY_RANGE_PRECIO = "precioValues";

}
