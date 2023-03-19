package link.yauritux.port.out;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.DebtAccount;

import java.util.List;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
public interface DebtAccountRepositoryPort<ID> {

    void save(DebtAccount debtAccount);

    /**
     * Fetch all debtor's debt account records.
     *
     * @param accountId
     * @return list of DebtAccount as owed by the customer account id.
     */

    List<DebtAccount> findByDebtorAccount(ID accountId);
    void remove(DebtAccount debtAccount);
}
