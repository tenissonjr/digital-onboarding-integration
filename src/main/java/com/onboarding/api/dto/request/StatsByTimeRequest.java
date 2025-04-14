package com.onboarding.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsByTimeRequest {

    @NotNull(message = "A unidade de tempo (unit) é obrigatória")
    private TimeUnit unit;

    @NotNull(message = "A quantidade de unidades de tempo (amount) é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser maior ou igual a 1")
    private Integer amount;

    public enum TimeUnit {
        SEGUNDOS, MINUTOS, HORAS, DIAS, MESES
    }
}