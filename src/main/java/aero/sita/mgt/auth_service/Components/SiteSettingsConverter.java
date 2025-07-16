package aero.sita.mgt.auth_service.Components;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import aero.sita.mgt.auth_service.Schemas.DTO.SectionSettings;

@Converter
public class SiteSettingsConverter implements AttributeConverter<Map<String, SectionSettings>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, SectionSettings> siteSettings) {
        try {
            return objectMapper.writeValueAsString(siteSettings);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar siteSettings", e);
        }
    }

    @Override
    public Map<String, SectionSettings> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new HashMap<>(); // ou return null, dependendo do seu caso
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, SectionSettings>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to Map<String, SectionSettings>", e);
        }
    }

}
