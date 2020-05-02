package com.m3u.parser.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .groupName("M3U API")
                    .apiInfo(getApiInfo())
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.m3u"))
                    .paths(PathSelectors.any())
                    .build();
        }

    private ApiInfo getApiInfo() {
           return new ApiInfoBuilder()
                   .title("M3U file management API")
                   .version("v1.0.0-BETA")
                   .description("write some description here")
                   .build();
    }


}
