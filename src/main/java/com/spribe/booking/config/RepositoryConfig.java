package com.spribe.booking.config;

import com.spribe.booking.model.types.*;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableR2dbcRepositories
public class RepositoryConfig extends AbstractR2dbcConfiguration {

    @Override
    public ConnectionFactory connectionFactory() {
        return null;
    }

    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        converters.add(new EnumReadConverter<>(AccommodationType.class));
        converters.add(new EnumReadConverter<>(BookingStatus.class));
        converters.add(new EnumReadConverter<>(PaymentStatus.class));
        converters.add(new EnumReadConverter<>(EventType.class));
        converters.add(new EnumWriteConverter<>());

        return new R2dbcCustomConversions(getStoreConversions(), converters);
    }

    @ReadingConverter
    static class EnumReadConverter<T extends Enum<T>> implements Converter<String, T> {
        private final Class<T> enumClass;

        public EnumReadConverter(Class<T> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public T convert(String source) {
            return Enum.valueOf(enumClass, source.toUpperCase());
        }
    }

    @WritingConverter
    static class EnumWriteConverter<T extends Enum<T>> implements Converter<T, String> {
        @Override
        public String convert(T source) {
            return source.name();
        }
    }
}
