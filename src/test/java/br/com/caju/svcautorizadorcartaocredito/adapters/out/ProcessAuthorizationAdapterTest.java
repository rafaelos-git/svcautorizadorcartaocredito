package br.com.caju.svcautorizadorcartaocredito.adapters.out;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.AccountBalanceRepository;
import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.entity.AccountBalanceEntity;
import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.mapper.AccountBalanceMapper;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ProcessAuthorizationAdapterTest {

    @Mock
    private AccountBalanceMapper accountBalanceMapper;

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @InjectMocks
    private ProcessAuthorizationAdapter processAuthorizationAdapter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcess() {
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccountId("1234");

        AccountBalanceEntity accountBalanceEntity = new AccountBalanceEntity();
        when(accountBalanceMapper.toAccountBalanceEntity(accountBalance)).thenReturn(accountBalanceEntity);

        processAuthorizationAdapter.process(accountBalance);

        verify(accountBalanceMapper).toAccountBalanceEntity(accountBalance);
        verify(accountBalanceRepository).save(accountBalanceEntity);
    }

    @Test
    public void testLoggingForProcess() {
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccountId("1234");

        Logger logger = (Logger) ReflectionTestUtils.getField(processAuthorizationAdapter, "log");
    }

    @Test
    public void testProcessThrowsException() {
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccountId("account1");
        accountBalance.setBalanceFood(100.0);
        accountBalance.setBalanceMeal(50.0);
        accountBalance.setBalanceCash(20.0);
        AccountBalanceEntity accountBalanceEntity = mock(AccountBalanceEntity.class);

        when(accountBalanceMapper.toAccountBalanceEntity(accountBalance)).thenReturn(accountBalanceEntity);
        doThrow(new RuntimeException("Save failed")).when(accountBalanceRepository).save(any(AccountBalanceEntity.class));

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            processAuthorizationAdapter.process(accountBalance);
        });

        assertEquals("Failed to update account balance", thrownException.getMessage());
        verify(accountBalanceRepository).save(accountBalanceEntity);
    }
}
