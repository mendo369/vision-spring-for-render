// src/main/java/com/ia.platform.ia_platform_backend/controller/ToolController.java

package com.ia.platform.ia_platform_backend.controller;

import com.ia.platform.ia_platform_backend.dto.CreateToolRequest;
import com.ia.platform.ia_platform_backend.dto.ToolDto;
import com.ia.platform.ia_platform_backend.service.ToolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ToolController {
    private final ToolService toolService;

    // Crear herramienta (requiere autenticación)
    @PostMapping
    public ResponseEntity<ToolDto> createTool(@Valid @RequestBody CreateToolRequest request) {
        ToolDto toolDto = toolService.createTool(request);
        return ResponseEntity.ok(toolDto);
    }

    // Obtener todas las herramientas
    @GetMapping
    public ResponseEntity<List<ToolDto>> getAllTools() {
        List<ToolDto> tools = toolService.getAllTools();
        return ResponseEntity.ok(tools);
    }

    // Obtener herramientas más vendidas
    @GetMapping("/best-selling")
    public ResponseEntity<List<ToolDto>> getBestSellingTools() {
        List<ToolDto> tools = toolService.getBestSellingTools();
        return ResponseEntity.ok(tools);
    }

    // Obtener herramientas populares
    @GetMapping("/popular")
    public ResponseEntity<List<ToolDto>> getPopularTools() {
        List<ToolDto> tools = toolService.getPopularTools();
        return ResponseEntity.ok(tools);
    }

    // Obtener herramienta por ID
    @GetMapping("/{id}")
    public ResponseEntity<ToolDto> getToolById(@PathVariable Long id) {
        ToolDto tool = toolService.getToolById(id);
        return ResponseEntity.ok(tool);
    }
}