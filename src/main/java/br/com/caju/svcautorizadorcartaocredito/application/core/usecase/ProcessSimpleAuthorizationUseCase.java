package br.com.caju.svcautorizadorcartaocredito.application.core.usecase;

import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.BalanceCategory;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.TransactionPayload;
import br.com.caju.svcautorizadorcartaocredito.application.ports.in.FindAccountBalanceInputPort;
import br.com.caju.svcautorizadorcartaocredito.application.ports.in.ProcessSimpleAuthorizationInputPort;
import br.com.caju.svcautorizadorcartaocredito.application.ports.out.DistributedLockManagerOutputPort;
import br.com.caju.svcautorizadorcartaocredito.application.ports.out.ProcessAuthorizationOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ProcessSimpleAuthorizationUseCase implements ProcessSimpleAuthorizationInputPort {
    private static final Logger logger = LoggerFactory.getLogger(ProcessSimpleAuthorizationUseCase.class);

    private final DistributedLockManagerOutputPort distributedLockManagerOutputPort;
    private final FindAccountBalanceInputPort findAccountBalanceInputPort;
    private final ProcessAuthorizationOutputPort processAuthorizationOutputPort;

    public ProcessSimpleAuthorizationUseCase(
            DistributedLockManagerOutputPort distributedLockManagerOutputPort,
            FindAccountBalanceInputPort findAccountBalanceInputPort,
            ProcessAuthorizationOutputPort processAuthorizationOutputPort
    ) {
        this.distributedLockManagerOutputPort = distributedLockManagerOutputPort;
        this.findAccountBalanceInputPort = findAccountBalanceInputPort;
        this.processAuthorizationOutputPort = processAuthorizationOutputPort;
    }

    @Override
    public String process(TransactionPayload transactionPayload) {
        String lockKey = "account:" + transactionPayload.getAccount();
        logger.info("Attempting to acquire lock for account: {}", transactionPayload.getAccount());

        if (distributedLockManagerOutputPort.tryLock(lockKey, 100)) {
            try {
                logger.info("Lock acquired for account: {}", transactionPayload.getAccount());
                var accountBalance = findAccountBalanceInputPort.find(transactionPayload.getAccount());

                logger.info("Adjusting MCC for merchant: {}", transactionPayload.getMerchant());
                String adjustedMcc = adjustMccBasedOnMerchant(transactionPayload.getMcc(), transactionPayload.getMerchant());

                logger.info("Determining category for MCC: {}", adjustedMcc);
                BalanceCategory category = determineCategoryByMcc(adjustedMcc);

                logger.info("Processing transaction for category: {}", category);
                boolean isApproved = processTransactionForCategory(accountBalance, transactionPayload.getTotalAmount(), category);

                if (isApproved) {
                    logger.info("Transaction approved. Processing authorization for account: {}", transactionPayload.getAccount());
                    processAuthorizationOutputPort.process(accountBalance);
                    return "00";
                } else {
                    logger.warn("Transaction declined due to insufficient funds for account: {}", transactionPayload.getAccount());
                    return "51";
                }
            } catch (Exception e) {
                logger.error("Error occurred while processing transaction for account: {}", transactionPayload.getAccount(), e);
                return "07";
            } finally {
                logger.info("Releasing lock for account: {}", transactionPayload.getAccount());
                distributedLockManagerOutputPort.releaseLock(lockKey);
            }
        } else {
            logger.warn("Could not acquire lock for account: {}", transactionPayload.getAccount());
            return "07";
        }
    }

    private String adjustMccBasedOnMerchant(String mcc, String merchant) {
        String merchantLowerCase = merchant.toLowerCase();
        if (merchantLowerCase.contains("food") || merchantLowerCase.contains("comida") || merchantLowerCase.contains("alimentacao")) {
            logger.info("MCC adjusted to '5411' based on merchant: {}", merchant);
            return "5411";
        }
        if (merchantLowerCase.contains("eat") || merchantLowerCase.contains("restaurante") || merchantLowerCase.contains("comer")) {
            logger.info("MCC adjusted to '5811' based on merchant: {}", merchant);
            return "5811";
        }
        return mcc;
    }

    private BalanceCategory determineCategoryByMcc(String mcc) {
        BalanceCategory category = switch (mcc) {
            case "5411", "5412" -> BalanceCategory.FOOD;
            case "5811", "5812" -> BalanceCategory.MEAL;
            default -> BalanceCategory.CASH;
        };
        logger.info("Category determined for MCC {}: {}", mcc, category);
        return category;
    }

    private boolean processTransactionForCategory(AccountBalance accountBalance, double totalAmount, BalanceCategory category) {
        boolean result = switch (category) {
            case FOOD -> adjustBalance(accountBalance::getBalanceFood, accountBalance::setBalanceFood, totalAmount);
            case MEAL -> adjustBalance(accountBalance::getBalanceMeal, accountBalance::setBalanceMeal, totalAmount);
            default -> false;
        };
        logger.info("Transaction {} for category: {}", result ? "approved" : "declined", category);
        return result;
    }

    private boolean adjustBalance(Supplier<Double> getBalance, Consumer<Double> setBalance, double totalAmount) {
        double currentBalance = getBalance.get();
        logger.info("Current balance: {}. Required amount: {}", currentBalance, totalAmount);
        if (currentBalance >= totalAmount) {
            setBalance.accept(currentBalance - totalAmount);
            logger.info("Balance adjusted. New balance: {}", currentBalance - totalAmount);
            return true;
        }
        logger.warn("Insufficient funds. Current balance: {}, Required amount: {}", currentBalance, totalAmount);
        return false;
    }
}