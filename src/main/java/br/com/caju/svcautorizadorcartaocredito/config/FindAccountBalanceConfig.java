package br.com.caju.svcautorizadorcartaocredito.config;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.FindAccountBalanceAdapter;
import br.com.caju.svcautorizadorcartaocredito.application.core.usecase.FindAccountBalanceUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FindAccountBalanceConfig {
    @Bean
    public FindAccountBalanceUseCase findAccountBalanceUseCase(
            FindAccountBalanceAdapter findAccountBalanceAdapter
    ) {
        return new FindAccountBalanceUseCase(
            findAccountBalanceAdapter
        );
    }
}
