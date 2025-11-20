// src/main/java/com/ia.platform.ia_platform_backend/service/ToolService.java

package com.ia.platform.ia_platform_backend.service;

import com.ia.platform.ia_platform_backend.dto.CreateToolRequest;
import com.ia.platform.ia_platform_backend.dto.ToolDto;
import com.ia.platform.ia_platform_backend.entity.Category;
import com.ia.platform.ia_platform_backend.entity.Tool;
import com.ia.platform.ia_platform_backend.entity.ToolFeature;
import com.ia.platform.ia_platform_backend.repository.CategoryRepository;
import com.ia.platform.ia_platform_backend.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToolService {
    private final ToolRepository toolRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ToolDto createTool(CreateToolRequest request) {
        // Buscar categoría por nombre
        Category category = categoryRepository.findByNombre(request.getCategoriaNombre())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + request.getCategoriaNombre()));

        Tool tool = new Tool();
        tool.setNombre(request.getNombre());
        tool.setDescripcionCorta(request.getDescripcionCorta());
        tool.setDescripcionLarga(request.getDescripcionLarga());
        tool.setPrecio(request.getPrecio());
        tool.setPrecioRevendedor(request.getPrecioRevendedor());
        tool.setCategoria(category);
        tool.setImagenUrl(request.getImagenUrl());
        tool.setEstado("activo");
        tool.setEsMasVendido(request.getEsMasVendido());
        tool.setEsMasPopular(request.getEsMasPopular());

        tool = toolRepository.save(tool);

        // Guardar características si existen
        if (request.getCaracteristicas() != null && !request.getCaracteristicas().isEmpty()) {
            for (int i = 0; i < request.getCaracteristicas().size(); i++) {
                String caracteristica = request.getCaracteristicas().get(i);
                ToolFeature feature = new ToolFeature();
                feature.setHerramienta(tool);
                feature.setCaracteristica(caracteristica);
                feature.setOrdenDisplay(i + 1);
                // No guardamos aquí porque usaremos cascade en la entidad Tool
            }
        }

        return mapToDto(tool);
    }

    public List<ToolDto> getAllTools() {
        return toolRepository.findByEstado("activo").stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ToolDto> getBestSellingTools() {
        return toolRepository.findByEsMasVendidoTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ToolDto> getPopularTools() {
        return toolRepository.findByEsMasPopularTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ToolDto getToolById(Long id) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));
        return mapToDto(tool);
    }

    private ToolDto mapToDto(Tool tool) {
        ToolDto dto = new ToolDto();
        dto.setId(tool.getId());
        dto.setNombre(tool.getNombre());
        dto.setDescripcionCorta(tool.getDescripcionCorta());
        dto.setDescripcionLarga(tool.getDescripcionLarga());
        dto.setPrecio(tool.getPrecio());
        dto.setPrecioRevendedor(tool.getPrecioRevendedor());
        dto.setCategoria(tool.getCategoria().getNombre());
        dto.setImagenUrl(tool.getImagenUrl());
        dto.setEstado(tool.getEstado());
        dto.setEsMasVendido(tool.getEsMasVendido());
        dto.setEsMasPopular(tool.getEsMasPopular());

        if (tool.getCaracteristicas() != null) {
            dto.setCaracteristicas(
                    tool.getCaracteristicas().stream()
                            .map(ToolFeature::getCaracteristica)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}