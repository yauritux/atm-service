package link.yauritux.port.spi;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.DebtAccount;

import java.util.List;
import java.util.Optional;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
public interface DebtAccountRepositoryPort {

    void save(DebtAccount debtAccount);
    List<DebtAccount> findByDebtorAccount(CustomerAccount account);
    void remove(DebtAccount debtAccount);
}
