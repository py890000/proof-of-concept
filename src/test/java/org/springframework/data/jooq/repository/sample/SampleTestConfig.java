package org.springframework.data.jooq.repository.sample;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jooq.repository.config.EnableJooqRepositories;

@EnableJooqRepositories(basePackageClasses = UserRepository.class)
@Configuration
public class SampleTestConfig {
}
