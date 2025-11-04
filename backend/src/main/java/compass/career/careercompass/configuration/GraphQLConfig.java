package compass.career.careercompass.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;

@Configuration
public class GraphQLConfig {

    @Bean
    public WebGraphQlInterceptor authInterceptor() {
        return (request, chain) -> {
            String authHeader = request.getHeaders().getFirst("Authorization");

            if (authHeader != null && !authHeader.isEmpty()) {
                request.configureExecutionInput((executionInput, builder) ->
                        builder.graphQLContext(contextBuilder ->
                                contextBuilder.put("authorization", authHeader)
                        ).build()
                );
            }

            return chain.next(request);
        };
    }
}
