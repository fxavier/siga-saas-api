package com.xavier.sigasaasapi.common.application.mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct configuration for all mappers in the application.
 * Defines default behaviors for mapping operations.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@MapperConfig(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MapStructConfig {
}