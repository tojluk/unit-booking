package com.spribe.booking.exception;


//@RestControllerAdvice
public class GlobalExceptionHandler //extends AbstractErrorWebExceptionHandler
 {

    /*
    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties webProperties,
                                  ApplicationContext applicationContext) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), request -> {
                    Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                                                                                ErrorAttributeOptions.defaults());

                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .body(BodyInserters.fromValue(errorPropertiesMap));
                });
    }
    */
}
