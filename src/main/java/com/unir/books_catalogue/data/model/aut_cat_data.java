package com.unir.books_catalogue.data.model;

public class aut_cat_data {
    public static final Autor[] autores = new Autor[]{
            new Autor(12345678, "Mario Vargas Llosa"),
            new Autor(23456789, "Paulo Coelho"),
            new Autor(34567890, "Miguel de Cervantes"),
    };
    public static final Categoria[] categorias = new Categoria[]{
            new Categoria(1, "Suspenso"),
            new Categoria(2, "Drama"),
            new Categoria(3, "Documental"),
            new Categoria(4, "Terror"),
            new Categoria(5, "Comedia"),
            new Categoria(6, "Religión"),
            new Categoria(7, "Psicologia"),
            new Categoria(8, "Ciencia"),
    };
}

