package com.gettasksdone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api")
@Tag(name = "Controlador Raíz")
public class RootController {
    @Operation(
        summary = "Hace un ping al servidor.",
        description = "Se hace una llamada ping al servidor para comprobar que la conexión funciona correctamente, retornando Pong."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Pong"))}, description = "La llamada ha respondido correctamente.")
    })
    @GetMapping("/ping")
    public String ping(){
        return "Pong";
    }

}