package ru.hogwarts.school.configuration;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchoolConfig {
    @Bean
    public GroupedOpenApi group() {
        return GroupedOpenApi.builder().group("Все контроллеры").pathsToMatch("/**").build();
    }

    @Bean
    public GroupedOpenApi studentGroup() {
        return GroupedOpenApi.builder().group("Контроллеры для студентов").pathsToMatch("/student/**").build();
    }

    @Bean
    public GroupedOpenApi facultyGroup() {
        return GroupedOpenApi.builder().group("Контроллеры для факультетов").pathsToMatch("/faculty/**").build();
    }

    @Bean
    public GroupedOpenApi avatarGroup() {
        return GroupedOpenApi.builder().group("Контроллеры для аватаров").pathsToMatch("/{id}/avatar/**").build();
    }
}
