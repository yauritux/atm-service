package link.yauritux.port.api;

import link.yauritux.domain.entity.CustomerAccount;

import java.math.BigDecimal;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
public interface CustomerAccountServicePort {

    void login(final String name);
    void logout();
    void deposit(final BigDecimal depositAmount);

    BigDecimal transfer(final String targetName, BigDecimal transferAmount);

    CustomerAccount getCurrentAccount();
}
