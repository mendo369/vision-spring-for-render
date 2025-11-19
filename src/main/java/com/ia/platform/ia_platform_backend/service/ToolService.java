// src/main/java/com/ia.platform.ia_platform_backend/service/ToolService.java

package com.ia.platform.ia_platform_backend.service;

import com.ia.platform.ia_platform_backend.dto.ToolDto;
import com.ia.platform.ia_platform_backend.entity.Tool;
import com.ia.platform.ia_platform_backend.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToolService {
    private final ToolRepository toolRepository;

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

        // Si tienes características, puedes mapearlas aquí
        // dto.setCaracteristicas(tool.getCaracteristicas().stream()
        //     .map(ToolFeature::getCaracteristica)
        //     .collect(Collectors.toList()));

        return dto;
    }
}