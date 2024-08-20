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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FindAccountBalanceAdapterTest {

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @Mock
    private AccountBalanceMapper accountBalanceMapper;

    @InjectMocks
    private FindAccountBalanceAdapter findAccountBalanceAdapter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAccountBalanceSuccess() {
        String accountId = "1234";
        AccountBalanceEntity accountBalanceEntity = new AccountBalanceEntity();
        AccountBalance accountBalance = new AccountBalance();

        when(accountBalanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(accountBalanceEntity));
        when(accountBalanceMapper.toAccountBalance(accountBalanceEntity)).thenReturn(accountBalance);

        Optional<AccountBalance> result = findAccountBalanceAdapter.find(accountId);

        assertTrue(result.isPresent());
        assertEquals(accountBalance, result.get());
        verify(accountBalanceRepository).findByAccountId(accountId);
        verify(accountBalanceMapper).toAccountBalance(accountBalanceEntity);
    }

    @Test
    public void testFindAccountBalanceNotFound() {
        String accountId = "1234";

        when(accountBalanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        Optional<AccountBalance> result = findAccountBalanceAdapter.find(accountId);

        assertFalse(result.isPresent());
        verify(accountBalanceRepository).findByAccountId(accountId);
        verifyNoInteractions(accountBalanceMapper);
    }

    @Test
    public void testFindAccountBalanceException() {
        String accountId = "1234";
        when(accountBalanceRepository.findByAccountId(accountId)).thenThrow(new RuntimeException("Database error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> findAccountBalanceAdapter.find(accountId));
        assertEquals("Error fetching account", thrown.getMessage());

        verify(accountBalanceRepository).findByAccountId(accountId);
        verifyNoInteractions(accountBalanceMapper);
    }

    @Test
    public void testLoggingForFindAccountBalance() {
        String accountId = "1234";
        AccountBalanceEntity accountBalanceEntity = new AccountBalanceEntity();
        AccountBalance accountBalance = new AccountBalance();

        when(accountBalanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(accountBalanceEntity));
        when(accountBalanceMapper.toAccountBalance(accountBalanceEntity)).thenReturn(accountBalance);

        Logger logger = (Logger) ReflectionTestUtils.getField(findAccountBalanceAdapter, "log");
    }
}
