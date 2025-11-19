// src/main/java/com/ia.platform.ia_platform_backend/controller/ToolController.java

package com.ia.platform.ia_platform_backend.controller;

import com.ia.platform.ia_platform_backend.dto.ToolDto;
import com.ia.platform.ia_platform_backend.service.ToolService;
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

    @GetMapping
    public ResponseEntity<List<ToolDto>> getAllTools() {
        List<ToolDto> tools = toolService.getAllTools();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/best-selling")
    public ResponseEntity<List<ToolDto>> getBestSellingTools() {
        List<ToolDto> tools = toolService.getBestSellingTools();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ToolDto>> getPopularTools() {
        List<ToolDto> tools = toolService.getPopularTools();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToolDto> getToolById(@PathVariable Long id) {
        ToolDto tool = toolService.getToolById(id);
        return ResponseEntity.ok(tool);
    }
}