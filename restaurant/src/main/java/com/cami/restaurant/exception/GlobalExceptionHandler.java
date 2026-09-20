package com.cami.restaurant.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

//ESTANDAR RFC 7807: Se compone del campo (type | title | status | detail | instance)
@RestControllerAdvice //Esta anotacion funciona de forma 100% automatica y global (no se tiene que llamar en otros controladores)
public class GlobalExceptionHandler {

    // =========================================================================
    // 404 NOT FOUND: Para mesas, platos u órdenes que no existen
    // =========================================================================
    @ExceptionHandler(EntityNotFoundException.class) //Cuando el servicio detecte el intercepte un EntityNotFoundException Spring lo intercepta y manda el usuario aqui
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex){

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,//ESPECIFICAMOS QUE ES UN ERROR 404
                ex.getMessage()//Captura el mensaje que se escribio antes de lanzar la excepcion, con el fin de explicar que fallo puntualmente
        );
        problem.setTitle("Recurso no encontrado"); //Categorizar el problema a nivel general
        problem.setType(URI.create("about:blank")); /**Si se cuenta con documentacion (Swagger) lo ideal es apuntarla si no apuntamos en blank*/
        problem.setProperty("timestamp", Instant.now()); //captura el momento en el que se generó el error en UTC-0
        return problem;
    }

    // =========================================================================
    // 409 CONFLICT: Reglas de negocio rotas por el estado actual de la entidad
    // =========================================================================
    @ExceptionHandler(IllegalStateException.class)//Cuando el servicio detecte el intercepte un IllegalStateException Spring lo intercepta y manda el usuario aqui
    public ProblemDetail handleIllegalState(IllegalStateException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problem.setTitle("Conflicto en la operación");
        problem.setType(URI.create("https://api.restaurante.com/errors/state-conflict"));//La URI sirve para validar el detectar el error en el frontend, ya que no es idonio detectarlo con lenguaje humano como lo es Title ni con el estado HTTP ya que es demasaido general, URI es mas especifico
        problem.setProperty("timestamp", Instant.now());
        return problem; // devolvemos el error en un JSON el cual el cliente en el FRONTEND nunca lo ve, esto es solo para el desarrollador que consume la API el FRONTEND DEVELOPER, el frontend es el que decide como mostrarselo al usuario
    }
}
