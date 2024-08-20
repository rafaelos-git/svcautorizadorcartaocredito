package br.com.caju.svcautorizadorcartaocredito.config;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.DistributedLockManagerAdapter;
import br.com.caju.svcautorizadorcartaocredito.adapters.out.ProcessAuthorizationAdapter;
import br.com.caju.svcautorizadorcartaocredito.application.core.usecase.FindAccountBalanceUseCase;
import br.com.caju.svcautorizadorcartaocredito.application.core.usecase.ProcessSimpleAuthorizationUseCase;
import br.com.caju.svcautorizadorcartaocredito.application.ports.out.DistributedLockManagerOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessSimpleAuthorizationConfig {
    @Bean
    public ProcessSimpleAuthorizationUseCase processSimpleAuthorizationUseCase(
            DistributedLockManagerAdapter distributedLockManagerAdapter,
            FindAccountBalanceUseCase findAccountBalanceUseCase,
            ProcessAuthorizationAdapter processAuthorizationAdapter
    ) {
        return new ProcessSimpleAuthorizationUseCase(
                distributedLockManagerAdapter,
                findAccountBalanceUseCase,
                processAuthorizationAdapter
        );
    }
}
