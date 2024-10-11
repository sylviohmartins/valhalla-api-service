package io.martins.valhalla.domain.converter;

import io.martins.valhalla.domain.enumeration.StatusEnum;
import jakarta.persistence.AttributeConverter;
import org.springframework.data.convert.PropertyValueConverter;
import org.springframework.data.convert.ValueConversionContext;

public class StatusConverter implements AttributeConverter<StatusEnum, Integer>, PropertyValueConverter<StatusEnum, String, ValueConversionContext<?>> {

	@Override
	public Integer convertToDatabaseColumn(final StatusEnum status) {
		return status != null ? status.getId() : null;
	}

	@Override
	public StatusEnum convertToEntityAttribute(final Integer id) {
		return StatusEnum.valueOfId(id);
	}

	@Override
	public StatusEnum read(final String name, final ValueConversionContext<?> context) {
    return StatusEnum.valueOfName(name);
	}

	@Override
	public String write(final StatusEnum status, final ValueConversionContext<?> context) {
		return status.name().toLowerCase();
	}

}