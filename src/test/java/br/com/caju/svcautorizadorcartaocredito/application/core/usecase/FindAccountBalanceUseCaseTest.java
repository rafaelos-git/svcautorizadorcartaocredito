package br.com.caju.svcautorizadorcartaocredito.application.core.usecase;

import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;
import br.com.caju.svcautorizadorcartaocredito.application.ports.out.FindAccountBalanceOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

public class FindAccountBalanceUseCaseTest {

    @Mock
    private FindAccountBalanceOutputPort findAccountBalanceOutputPort;

    @InjectMocks
    private FindAccountBalanceUseCase findAccountBalanceUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAccountBalance_Success() {
        String accountId = "account123";
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccountId(accountId);
        when(findAccountBalanceOutputPort.find(accountId)).thenReturn(Optional.of(accountBalance));

        AccountBalance result = findAccountBalanceUseCase.find(accountId);

        assertThat(result).isNotNull();
        assertThat(result.getAccountId()).isEqualTo(accountId);
    }

    @Test
    public void testFindAccountBalance_NotFound() {
        String accountId = "account123";
        when(findAccountBalanceOutputPort.find(accountId)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> findAccountBalanceUseCase.find(accountId));

        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessage("Account not found");
    }
}
