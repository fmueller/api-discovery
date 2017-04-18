package org.zalando.apidiscovery.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.addAll(messageConverters().getConverters());
    }

    @Bean
    public HttpMessageConverters messageConverters() {
        return new HttpMessageConverters(false, getHttpMessageConverters());
    }

    private ArrayList<HttpMessageConverter<?>> getHttpMessageConverters() {
        final StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        ArrayList<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<>();
        httpMessageConverters.add(stringHttpMessageConverter);
        httpMessageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));

        return httpMessageConverters;
    }
}
