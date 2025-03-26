package com.spribe.booking.config;

import com.spribe.booking.model.types.AccommodationType;
import com.spribe.booking.model.types.BookingStatus;
import com.spribe.booking.model.types.EventType;
import com.spribe.booking.model.types.PaymentStatus;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.util.List;

@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class RepositoryConfig extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.host:localhost}")
    private String host;

    @Value("${spring.r2dbc.port:5432}")
    private int port;

    @Value("${spring.r2dbc.database:booking_db}")
    private String database;

    @Value("${spring.r2dbc.username:tst}")
    private String username;

    @Value("${spring.r2dbc.password:tst}")
    private String password;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(username)
                .password(password)
                .codecRegistrar(EnumCodec.builder()
                                        .withEnum("accommodation_type", AccommodationType.class)
                                        .withEnum("booking_status", BookingStatus.class)
                                        .withEnum("payment_status", PaymentStatus.class)
                                        .withEnum("event_type", EventType.class)
                                        .build())
                .build();

        return new PostgresqlConnectionFactory(config);
    }

    @Override
    protected List<Object> getCustomConverters() {
        return List.of(
                new AccommodationTypeWriteConverter(),
                new BookingStatusWriteConverter(),
                new EventTypeWriteConverter(),
                new PaymentStatusWriteConverter()
        );
    }

    @WritingConverter
    static class AccommodationTypeWriteConverter implements Converter<AccommodationType, AccommodationType> {
        @Override
        public AccommodationType convert(AccommodationType source) {
            return source;
        }
    }

    @WritingConverter
    static class BookingStatusWriteConverter implements Converter<BookingStatus, BookingStatus> {
        @Override
        public BookingStatus convert(BookingStatus source) {
            return source;
        }
    }

    @WritingConverter
    static class PaymentStatusWriteConverter implements Converter<PaymentStatus, PaymentStatus> {
        @Override
        public PaymentStatus convert(PaymentStatus source) {
            return source;
        }
    }

    @WritingConverter
    static class EventTypeWriteConverter implements Converter<EventType, EventType> {
        @Override
        public EventType convert(EventType source) {
            return source;
        }
    }


}
