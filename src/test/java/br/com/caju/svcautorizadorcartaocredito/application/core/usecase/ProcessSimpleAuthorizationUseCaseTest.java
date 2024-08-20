package br.com.caju.svcautorizadorcartaocredito.application.core.usecase;

import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.BalanceCategory;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.TransactionPayload;
import br.com.caju.svcautorizadorcartaocredito.application.ports.in.FindAccountBalanceInputPort;
import br.com.caju.svcautorizadorcartaocredito.application.ports.out.DistributedLockManagerOutputPort;
import br.com.caju.svcautorizadorcartaocredito.application.ports.out.ProcessAuthorizationOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProcessSimpleAuthorizationUseCaseTest {

    @Mock
    private DistributedLockManagerOutputPort distributedLockManagerOutputPort;

    @Mock
    private FindAccountBalanceInputPort findAccountBalanceInputPort;

    @Mock
    private ProcessAuthorizationOutputPort processAuthorizationOutputPort;

    @InjectMocks
    private ProcessSimpleAuthorizationUseCase processSimpleAuthorizationUseCase;

    private AccountBalance accountBalance;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountBalance = new AccountBalance();
        accountBalance.setAccountId("account123");
        accountBalance.setBalanceFood(100.0);
        accountBalance.setBalanceMeal(100.0);
        accountBalance.setBalanceCash(100.0);
    }

    @Test
    public void testProcess_SuccessWithLock() {
        String accountId = "account123";
        TransactionPayload transactionPayload = new TransactionPayload();
        transactionPayload.setAccount(accountId);
        transactionPayload.setTotalAmount(50.0);
        transactionPayload.setMcc("5811");
        transactionPayload.setMerchant("Restaurante Teste");

        when(distributedLockManagerOutputPort.tryLock(anyString(), anyLong())).thenReturn(true);
        when(findAccountBalanceInputPort.find(anyString())).thenReturn(accountBalance);
        doNothing().when(processAuthorizationOutputPort).process(any(AccountBalance.class));

        String result = processSimpleAuthorizationUseCase.process(transactionPayload);

        assertThat(result).isEqualTo("00");
        verify(distributedLockManagerOutputPort).releaseLock(anyString());
        verify(processAuthorizationOutputPort).process(any(AccountBalance.class));
    }

    @Test
    public void testProcess_FailureWithLock() {
        String accountId = "account123";
        TransactionPayload transactionPayload = new TransactionPayload();
        transactionPayload.setAccount(accountId);
        transactionPayload.setTotalAmount(150.0);
        transactionPayload.setMcc("5811");
        transactionPayload.setMerchant("Restaurante Teste");

        when(distributedLockManagerOutputPort.tryLock(anyString(), anyLong())).thenReturn(true);
        when(findAccountBalanceInputPort.find(anyString())).thenReturn(accountBalance);
        doNothing().when(processAuthorizationOutputPort).process(any(AccountBalance.class));

        String result = processSimpleAuthorizationUseCase.process(transactionPayload);

        assertThat(result).isEqualTo("51");
        verify(distributedLockManagerOutputPort).releaseLock(anyString());
        verify(processAuthorizationOutputPort, never()).process(any(AccountBalance.class));
    }

    @Test
    public void testProcess_ExceptionDuringProcessing() {
        String accountId = "account123";
        TransactionPayload transactionPayload = new TransactionPayload();
        transactionPayload.setAccount(accountId);
        transactionPayload.setTotalAmount(50.0);
        transactionPayload.setMcc("5811");
        transactionPayload.setMerchant("Restaurante Teste");

        when(distributedLockManagerOutputPort.tryLock(anyString(), anyLong())).thenReturn(true);
        when(findAccountBalanceInputPort.find(anyString())).thenThrow(new RuntimeException("Database error"));

        String result = processSimpleAuthorizationUseCase.process(transactionPayload);

        assertThat(result).isEqualTo("07");
        verify(distributedLockManagerOutputPort).releaseLock(anyString());
    }

    @Test
    public void testProcess_LockFailed() {
        String accountId = "account123";
        TransactionPayload transactionPayload = new TransactionPayload();
        transactionPayload.setAccount(accountId);
        transactionPayload.setTotalAmount(50.0);
        transactionPayload.setMcc("5811");
        transactionPayload.setMerchant("Restaurante Teste");

        when(distributedLockManagerOutputPort.tryLock(anyString(), anyLong())).thenReturn(false);

        String result = processSimpleAuthorizationUseCase.process(transactionPayload);

        assertThat(result).isEqualTo("07");
        verify(distributedLockManagerOutputPort, never()).releaseLock(anyString());
        verify(findAccountBalanceInputPort, never()).find(anyString());
        verify(processAuthorizationOutputPort, never()).process(any(AccountBalance.class));
    }

    @Test
    public void testAdjustMccBasedOnMerchant() throws Exception {
        String merchant1 = "Food Place";
        String merchant2 = "Eatery";
        String merchant3 = "Other Place";

        String merchant4 = "Comida Place";
        String merchant5 = "Alimentacao do vale";

        String merchant6 = "Venha comer";

        Method adjustMccBasedOnMerchantMethod = ProcessSimpleAuthorizationUseCase.class
                .getDeclaredMethod("adjustMccBasedOnMerchant", String.class, String.class);
        adjustMccBasedOnMerchantMethod.setAccessible(true);

        assertThat(adjustMccBasedOnMerchantMethod.invoke(processSimpleAuthorizationUseCase, "1234", merchant1))
                .isEqualTo("5411");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processSimpleAuthorizationUseCase, "1234", merchant2))
                .isEqualTo("5811");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processSimpleAuthorizationUseCase, "1234", merchant3))
                .isEqualTo("1234");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processSimpleAuthorizationUseCase, "1234", merchant4))
                .isEqualTo("5411");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processSimpleAuthorizationUseCase, "1234", merchant5))
                .isEqualTo("5411");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processSimpleAuthorizationUseCase, "1234", merchant6))
                .isEqualTo("5811");
    }

    @Test
    public void testDetermineCategoryByMcc() throws Exception {
        Method determineCategoryByMccMethod = ProcessSimpleAuthorizationUseCase.class
                .getDeclaredMethod("determineCategoryByMcc", String.class);
        determineCategoryByMccMethod.setAccessible(true);

        assertThat(determineCategoryByMccMethod.invoke(processSimpleAuthorizationUseCase, "5411"))
                .isEqualTo(BalanceCategory.FOOD);
        assertThat(determineCategoryByMccMethod.invoke(processSimpleAuthorizationUseCase, "5412"))
                .isEqualTo(BalanceCategory.FOOD);
        assertThat(determineCategoryByMccMethod.invoke(processSimpleAuthorizationUseCase, "5811"))
                .isEqualTo(BalanceCategory.MEAL);
        assertThat(determineCategoryByMccMethod.invoke(processSimpleAuthorizationUseCase, "5812"))
                .isEqualTo(BalanceCategory.MEAL);
        assertThat(determineCategoryByMccMethod.invoke(processSimpleAuthorizationUseCase, "1234"))
                .isEqualTo(BalanceCategory.CASH);
    }

    @Test
    public void testProcessTransactionForCategory() throws Exception {
        Method processTransactionForCategoryMethod = ProcessSimpleAuthorizationUseCase.class
                .getDeclaredMethod("processTransactionForCategory", AccountBalance.class, double.class, BalanceCategory.class);
        processTransactionForCategoryMethod.setAccessible(true);

        accountBalance.setBalanceFood(100.0);
        boolean resultFood = (boolean) processTransactionForCategoryMethod.invoke(processSimpleAuthorizationUseCase, accountBalance, 50.0, BalanceCategory.FOOD);
        assertThat(resultFood).isTrue();
        assertThat(accountBalance.getBalanceFood()).isEqualTo(50.0);

        accountBalance.setBalanceMeal(100.0);
        boolean resultMeal = (boolean) processTransactionForCategoryMethod.invoke(processSimpleAuthorizationUseCase, accountBalance, 50.0, BalanceCategory.MEAL);
        assertThat(resultMeal).isTrue();
        assertThat(accountBalance.getBalanceMeal()).isEqualTo(50.0);

        boolean resultCash = (boolean) processTransactionForCategoryMethod.invoke(processSimpleAuthorizationUseCase, accountBalance, 50.0, BalanceCategory.CASH);
        assertThat(resultCash).isFalse();
    }
}
