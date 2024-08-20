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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProcessFallbackAuthorizationUseCaseTest {

    @Mock
    private DistributedLockManagerOutputPort distributedLockManagerOutputPort;

    @Mock
    private FindAccountBalanceInputPort findAccountBalanceInputPort;

    @Mock
    private ProcessAuthorizationOutputPort processAuthorizationOutputPort;

    @InjectMocks
    private ProcessFallbackAuthorizationUseCase processFallbackAuthorizationUseCase;

    private AccountBalance accountBalance;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountBalance = new AccountBalance();
        accountBalance.setAccountId("account123");
        accountBalance.setBalanceFood(1000.0);
        accountBalance.setBalanceMeal(1000.0);
        accountBalance.setBalanceCash(10000.0);
    }

    @Test
    public void testProcess_SuccessForVariousCategories() {
        TransactionPayload transactionPayloadFood = createTransactionPayload("account123", 50.0, "5411", "Restaurante Comida");
        TransactionPayload transactionPayloadMeal = createTransactionPayload("account123", 50.0, "5811", "Restaurante Eat");
        TransactionPayload transactionPayloadCash = createTransactionPayload("account123", 2000.0, "1234", "Teste Generico");

        when(distributedLockManagerOutputPort.tryLock(anyString(), anyLong())).thenReturn(true);
        when(findAccountBalanceInputPort.find(anyString())).thenReturn(accountBalance);
        doNothing().when(processAuthorizationOutputPort).process(any(AccountBalance.class));

        assertThat(processFallbackAuthorizationUseCase.process(transactionPayloadFood)).isEqualTo("00");

        assertThat(processFallbackAuthorizationUseCase.process(transactionPayloadMeal)).isEqualTo("00");

        assertThat(processFallbackAuthorizationUseCase.process(transactionPayloadCash)).isEqualTo("00");

        verify(distributedLockManagerOutputPort, times(3)).releaseLock(anyString());
    }

    @Test
    public void testProcess_FailureWithLock() {
        String accountId = "account123";
        TransactionPayload transactionPayload = new TransactionPayload();
        transactionPayload.setAccount(accountId);
        transactionPayload.setTotalAmount(15000.0);
        transactionPayload.setMcc("8811");
        transactionPayload.setMerchant("Teste");

        when(distributedLockManagerOutputPort.tryLock(anyString(), anyLong())).thenReturn(true);
        when(findAccountBalanceInputPort.find(anyString())).thenReturn(accountBalance);
        doNothing().when(processAuthorizationOutputPort).process(any(AccountBalance.class));

        String result = processFallbackAuthorizationUseCase.process(transactionPayload);

        assertThat(result).isEqualTo("51");
        verify(distributedLockManagerOutputPort).releaseLock(anyString());
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

        Method adjustMccBasedOnMerchantMethod = ProcessFallbackAuthorizationUseCase.class
                .getDeclaredMethod("adjustMccBasedOnMerchant", String.class, String.class);
        adjustMccBasedOnMerchantMethod.setAccessible(true);

        assertThat(adjustMccBasedOnMerchantMethod.invoke(processFallbackAuthorizationUseCase, "1234", merchant1))
                .isEqualTo("5411");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processFallbackAuthorizationUseCase, "1234", merchant2))
                .isEqualTo("5811");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processFallbackAuthorizationUseCase, "1234", merchant3))
                .isEqualTo("1234");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processFallbackAuthorizationUseCase, "1234", merchant4))
                .isEqualTo("5411");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processFallbackAuthorizationUseCase, "1234", merchant5))
                .isEqualTo("5411");
        assertThat(adjustMccBasedOnMerchantMethod.invoke(processFallbackAuthorizationUseCase, "1234", merchant6))
                .isEqualTo("5811");
    }

    @Test
    public void testDetermineCategoryByMcc() throws Exception {
        Method determineCategoryByMccMethod = ProcessFallbackAuthorizationUseCase.class
                .getDeclaredMethod("determineCategoryByMcc", String.class);
        determineCategoryByMccMethod.setAccessible(true);

        assertThat(determineCategoryByMccMethod.invoke(processFallbackAuthorizationUseCase, "5411"))
                .isEqualTo(BalanceCategory.FOOD);
        assertThat(determineCategoryByMccMethod.invoke(processFallbackAuthorizationUseCase, "5412"))
                .isEqualTo(BalanceCategory.FOOD);
        assertThat(determineCategoryByMccMethod.invoke(processFallbackAuthorizationUseCase, "5811"))
                .isEqualTo(BalanceCategory.MEAL);
        assertThat(determineCategoryByMccMethod.invoke(processFallbackAuthorizationUseCase, "5812"))
                .isEqualTo(BalanceCategory.MEAL);
        assertThat(determineCategoryByMccMethod.invoke(processFallbackAuthorizationUseCase, "1234"))
                .isEqualTo(BalanceCategory.CASH);
    }

    @Test
    public void testProcess_InsufficientBalanceInAllCategories() {
        TransactionPayload transactionPayload = createTransactionPayload("account123", 20000.0, "5812", "Restaurante Teste");

        when(distributedLockManagerOutputPort.tryLock(anyString(), anyLong())).thenReturn(true);
        when(findAccountBalanceInputPort.find(anyString())).thenReturn(accountBalance);

        String result = processFallbackAuthorizationUseCase.process(transactionPayload);

        assertThat(result).isEqualTo("51");

        verify(distributedLockManagerOutputPort).releaseLock(anyString());
    }

    @Test
    public void testProcess_AccountBalanceNotFoundOrError() {
        TransactionPayload transactionPayload = createTransactionPayload("account123", 1000.0, "5411", "Restaurante Teste");

        when(distributedLockManagerOutputPort.tryLock(anyString(), anyLong())).thenReturn(true);
        when(findAccountBalanceInputPort.find(anyString())).thenThrow(new RuntimeException("Database error"));

        String result = processFallbackAuthorizationUseCase.process(transactionPayload);

        assertThat(result).isEqualTo("07");

        verify(distributedLockManagerOutputPort).releaseLock(anyString());
    }

    @Test
    public void testProcess_LockFailed() {
        TransactionPayload transactionPayload = createTransactionPayload("account123", 50.0, "5411", "Restaurante Teste");

        when(distributedLockManagerOutputPort.tryLock(anyString(), anyLong())).thenReturn(false);

        String result = processFallbackAuthorizationUseCase.process(transactionPayload);

        assertThat(result).isEqualTo("07");

        verify(distributedLockManagerOutputPort, never()).releaseLock(anyString());
    }

    private TransactionPayload createTransactionPayload(String accountId, double totalAmount, String mcc, String merchant) {
        TransactionPayload transactionPayload = new TransactionPayload();
        transactionPayload.setAccount(accountId);
        transactionPayload.setTotalAmount(totalAmount);
        transactionPayload.setMcc(mcc);
        transactionPayload.setMerchant(merchant);
        return transactionPayload;
    }
}
