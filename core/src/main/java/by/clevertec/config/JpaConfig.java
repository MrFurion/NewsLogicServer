package by.clevertec.config;

import by.clevertec.lucene.repository.impl.BaseRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "by.clevertec",
        repositoryBaseClass = BaseRepositoryImpl.class)
public class JpaConfig {

}
