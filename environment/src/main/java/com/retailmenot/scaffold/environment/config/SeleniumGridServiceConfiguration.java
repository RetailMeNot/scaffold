package com.retailmenot.scaffold.environment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class SeleniumGridServiceConfiguration {

    private final static int THIRTY_SECONDS = 30000;

    @Bean
    public RestTemplate seleniumGridRestTemplate(
            // Set a default value if the remote URL is null. It isn't required if the user is not testing through Grid.
            @Value("${desired-capabilities.remote-url:http://add.remoteurl.configuration}") String chromeSeleniumGridRootUri,
            @Qualifier("objectMapper") ObjectMapper objectMapper
    ) {
        // Create new message converters
        var messageConverters = List.of(new FormHttpMessageConverter(), new MappingJackson2HttpMessageConverter(objectMapper));

        return new RestTemplateBuilder()
                .rootUri(chromeSeleniumGridRootUri)
                .messageConverters(messageConverters)
                .requestFactory(this::getClientHttpRequestFactory)
                .build();
    }

    /**
     * Spring 2.0 change. We need to create our own custom {@link ClientHttpRequestFactory} to be used
     * with the {@link RestTemplateBuilder}.
     * <p>
     * Set up a Request Configuration with our timeouts. Then, build a client with additional options. Afterward,
     * return the request factory using the client options.
     *
     * @return the {@link ClientHttpRequestFactory}
     */
    private ClientHttpRequestFactory getClientHttpRequestFactory() {

        var config = RequestConfig.custom()
                .setConnectTimeout(THIRTY_SECONDS)
                .setConnectionRequestTimeout(THIRTY_SECONDS)
                .setSocketTimeout(THIRTY_SECONDS)
                .build();

        // Build an HTTPCLIENT with SSL enabled
        var httpClient = HttpClientBuilder.create()
                .useSystemProperties()
                .setDefaultRequestConfig(config)
                .disableAuthCaching()
                .disableCookieManagement()
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}
