package com.spribe.booking.controller;

import com.spribe.booking.dto.PaymentResponse;
import com.spribe.booking.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "API для работы с платежами")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{paymentId}/process")
    @Operation(summary = "Emulate payment")
    public Mono<PaymentResponse> processPayment(@PathVariable Long paymentId) {
        return paymentService.processPayment(paymentId)
                             .map(payment -> new PaymentResponse(
                                     payment.getId(),
                                     payment.getBookingId(),
                                     payment.getAmount(),
                                     payment.getStatus(),
                                     payment.getPaymentDate(),
                                     payment.getExpirationTime(),
                                     payment.getCreatedAt()
                             ));
    }
}
