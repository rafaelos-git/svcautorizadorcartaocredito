package br.com.caju.svcautorizadorcartaocredito.config;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.DistributedLockManagerAdapter;
import br.com.caju.svcautorizadorcartaocredito.adapters.out.ProcessAuthorizationAdapter;
import br.com.caju.svcautorizadorcartaocredito.application.core.usecase.FindAccountBalanceUseCase;
import br.com.caju.svcautorizadorcartaocredito.application.core.usecase.ProcessFallbackAuthorizationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessFallbackAuthorizationConfig {
    @Bean
    public ProcessFallbackAuthorizationUseCase processFallbackAuthorizationUseCase(
            DistributedLockManagerAdapter distributedLockManagerAdapter,
            FindAccountBalanceUseCase findAccountBalanceUseCase,
            ProcessAuthorizationAdapter processAuthorizationAdapter
    ) {
        return new ProcessFallbackAuthorizationUseCase(
                distributedLockManagerAdapter,
                findAccountBalanceUseCase,
                processAuthorizationAdapter
        );
    }
}
