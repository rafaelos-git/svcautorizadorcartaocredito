package br.com.caju.svcautorizadorcartaocredito.adapters.out.repository;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.ProcessAuthorizationAdapter;
import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.entity.AccountBalanceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DataJpaTest
public class AccountBalanceRepositoryTest {

    @Mock
    private AccountBalanceRepository repository;

    @InjectMocks
    private ProcessAuthorizationAdapter processAuthorizationAdapter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByAccountId() {
        AccountBalanceEntity entity = new AccountBalanceEntity();
        entity.set_id("123");
        entity.setAccountId("acc001");
        entity.setBalanceFood(100.50);
        entity.setBalanceMeal(200.75);
        entity.setBalanceCash(300.25);

        when(repository.findByAccountId("acc001")).thenReturn(Optional.of(entity));

        Optional<AccountBalanceEntity> result = repository.findByAccountId("acc001");

        assertThat(result).isPresent();
        assertThat(result.get().getAccountId()).isEqualTo("acc001");
        assertThat(result.get().getBalanceFood()).isEqualTo(100.50);
        assertThat(result.get().getBalanceMeal()).isEqualTo(200.75);
        assertThat(result.get().getBalanceCash()).isEqualTo(300.25);

        verify(repository, times(1)).findByAccountId("acc001");
    }

    @Test
    public void testFindByAccountIdNotFound() {
        when(repository.findByAccountId("nonexistent")).thenReturn(Optional.empty());

        Optional<AccountBalanceEntity> result = repository.findByAccountId("nonexistent");

        assertThat(result).isEmpty();

        verify(repository, times(1)).findByAccountId("nonexistent");
    }
}