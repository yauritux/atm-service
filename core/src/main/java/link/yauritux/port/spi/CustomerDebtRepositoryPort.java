package link.yauritux.port.spi;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.CustomerDebt;

import java.util.Optional;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
public interface CustomerDebtRepositoryPort {

    void save(CustomerDebt customerDebt);
    Optional<CustomerDebt> findByDebtorAccount(CustomerAccount account);
}
