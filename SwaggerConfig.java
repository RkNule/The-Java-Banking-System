package com.banking.jbs.config;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;
//import org.springdoc.core.SpringDocUtils;

@Configuration
public class SwaggerConfig {
    static {
        SpringDocUtils.getConfig().replaceWithClass(org.springframework.data.domain.Pageable.class, io.swagger.v3.oas.annotations.Parameter.class);
    }
}
