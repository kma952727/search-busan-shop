package com.example.searchbusanshopapi.infra.config;

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
public class SwaggerConfig {
    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("busan shop api info")
                .description("부산가게리스트 엔드포인트 설명입니다.")
                .build();
    }

    @Bean
    public Docket commonApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("busans")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.searchbusanshopapi"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(this.apiInfo());
    }
}
