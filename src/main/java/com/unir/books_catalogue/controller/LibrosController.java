package com.unir.books_catalogue.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.unir.books_catalogue.controller.model.LibroDto;
import com.unir.books_catalogue.service.LibrosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.unir.books_catalogue.data.model.Libro;
import com.unir.books_catalogue.controller.model.CreateLibroRequest;
//import com.unir.books_catalogue.service.ProductsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Libros Controller", description = "Microservicio encargado de exponer operaciones CRUD sobre Libros alojados en una base de datos MySQL.")
public class LibrosController {

    private final LibrosService service;

    @GetMapping("/libros")
    @Operation(
            operationId = "Obtener libros",
            description = "Operacion de lectura",
            summary = "Se devuelve una lista de todos los libros almacenados en la base de datos.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class)))
    public ResponseEntity<List<Libro>> getLibros(
            @RequestHeader Map<String, String> headers,
            @Parameter(name = "titulo", description = "Nombre del libro. No tiene por que ser exacto", example = "", required = false)
            @RequestParam(required = false) String titulo,
            @Parameter(name = "autor", description = "Autor del libro. Debe ser exacto", example = "", required = false)
            @RequestParam(required = false) String autor,
            @Parameter(name = "fechapub", description = "Fecha de publicacion del libro. Debe ser exacta", example = "", required = false)
            @RequestParam(required = false) String fechapub,
            @Parameter(name = "categoria", description = "Categoria del libro. Debe ser exacta", example = "", required = false)
            @RequestParam(required = false) String categoria,
            @Parameter(name = "isbn", description = "ISBN del libro. Debe ser exacto", example = "", required = false)
            @RequestParam(required = false) String isbn,
            @Parameter(name = "valoracion", description = "Valoracion del libro. Debe ser exacta", example = "", required = false)
            @RequestParam(required = false) String valoracion) {

        log.info("headers: {}", headers);
        List<Libro> libros = service.getLibros(titulo, autor, fechapub, categoria, isbn, valoracion, true);

        if (libros != null) {
            return ResponseEntity.ok(libros);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/libros/{idlibro}")
    @Operation(
            operationId = "Obtener un libro",
            description = "Operacion de lectura",
            summary = "Se devuelve un libro a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el libro con el identificador indicado.")
    public ResponseEntity<Libro> getLibro(@PathVariable String idlibro) {

        log.info("Request received for product {}", idlibro);
        Libro libro = service.getLibro(idlibro);

        if (libro != null) {
            return ResponseEntity.ok(libro);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/libros/{idlibro}")
    @Operation(
            operationId = "Eliminar un libro",
            description = "Operacion de escritura",
            summary = "Se elimina un libro a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el libro con el identificador indicado.")
    public ResponseEntity<Void> deleteLibro(@PathVariable String idlibro) {

        Boolean removed = service.removeLibro(idlibro);

        if (Boolean.TRUE.equals(removed)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/libros")
    @Operation(
            operationId = "Insertar un libro",
            description = "Operacion de escritura",
            summary = "Se crea un libro a partir de sus datos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a crear.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateLibroRequest.class))))
    @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos incorrectos introducidos.")
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el libro con el identificador indicado.")
    public ResponseEntity<Libro> addLibro(@RequestBody CreateLibroRequest request) {

        Libro createdLibro = service.createLibro(request);

        if (createdLibro != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLibro);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @PatchMapping("/libros/{idlibro}")
    @Operation(
            operationId = "Modificar parcialmente un libro",
            description = "RFC 7386. Operacion de escritura",
            summary = "RFC 7386. Se modifica parcialmente un libro.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a crear.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = String.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "libro inválido o datos incorrectos introducidos.")
    public ResponseEntity<Libro> patchLibro(@PathVariable String idlibro, @RequestBody String patchBody) {

        Libro patched = service.updateLibro(idlibro, patchBody);
        if (patched != null) {
            return ResponseEntity.ok(patched);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/libros/{idlibro}")
    @Operation(
            operationId = "Modificar totalmente un libro",
            description = "Operacion de escritura",
            summary = "Se modifica totalmente un libro.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a actualizar.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = LibroDto.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "libro no encontrado.")
    public ResponseEntity<Libro> updateLibro(@PathVariable String idlibro, @RequestBody LibroDto body) {

        Libro updated = service.updateLibro(idlibro, body);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}