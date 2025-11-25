// src/main/java/com/ia/platform/ia_platform_backend/dto/RechargeRequest.java

package com.ia.platform.ia_platform_backend.dto;

import lombok.Data;

@Data
public class RechargeRequest {
    private String monto; // En formato string para manejar decimales fácilmente
}