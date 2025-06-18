package com.hackathon.back.controller;

import com.hackathon.back.service.PortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/portals")
@Tag(name = "Portal", description = "APIs para gerenciamento de portais de notícias")
public class PortalController {
    
    @Autowired
    private PortalService portalService;

    @Operation(summary = "Registra um novo portal de notícias", 
               description = "Registra um portal e retorna instruções para configuração do DNS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Portal registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerPortal(
            @Parameter(description = "Nome do portal de notícias") @RequestParam String name, 
            @Parameter(description = "URL do portal") @RequestParam String url, 
            @Parameter(description = "Email de contato") @RequestParam String email) {
        try {
            // Cria o portal e gera instruções para configuração do DNS
            portalService.createPortal(name, url, email);
            List<String> dnsInstructions = portalService.getDnsInstructions(url);
            
            return ResponseEntity.ok(dnsInstructions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao registrar portal: " + e.getMessage());
        }
    }

    @Operation(summary = "Verifica se um portal está configurado corretamente", 
               description = "Verifica se o registro DNS do portal está configurado com o hash correto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Portal verificado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Portal não verificado")
    })
    @GetMapping("/verify")
    public ResponseEntity<?> verifyPortal(
            @Parameter(description = "URL do portal a ser verificado") @RequestParam String url) {
        boolean isVerified = portalService.verifyPortal(url);
        if (isVerified) {
            return ResponseEntity.ok("Portal verificado com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Portal não verificado. Verifique se o registro TXT foi configurado corretamente.");
        }
    }

    @Operation(summary = "Verifica se uma URL é confiável", 
               description = "Verifica se uma URL pertence a um portal verificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL verificada")
    })
    @GetMapping("/check")
    public ResponseEntity<?> checkUrl(
            @Parameter(description = "URL a ser verificada") @RequestParam String url) {
        boolean isVerified = portalService.verifyPortal(url);
        if (isVerified) {
            return ResponseEntity.ok("URL verificada e confiável");
        } else {
            return ResponseEntity.ok("URL não verificada. Pode ser uma fonte não confiável.");
        }
    }
}
