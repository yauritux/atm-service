package link.yauritux.port.in;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.valueobject.TransactionResponse;

import java.math.BigDecimal;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
public interface CustomerAccountServicePort {

    BigDecimal login(final String name);
    void logout();
    TransactionResponse deposit(final BigDecimal depositAmount);

    TransactionResponse transfer(final String targetName, BigDecimal transferAmount);

    CustomerAccount getCurrentAccount();
}
