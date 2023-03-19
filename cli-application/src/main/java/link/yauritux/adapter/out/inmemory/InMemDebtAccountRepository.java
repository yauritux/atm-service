package link.yauritux.adapter.out.inmemory;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.DebtAccount;
import link.yauritux.port.spi.DebtAccountRepositoryPort;

import java.util.*;

/**
 * @author yauritux@gmail.com
 * @version 1.0.0
 */
public class InMemDebtAccountRepository implements DebtAccountRepositoryPort {

    private final Map<String, List<DebtAccount>> records = new HashMap<>();

    @Override
    public void save(DebtAccount debtAccount) {
        var existingRecord = records.get(debtAccount.getDebtorAccountName());
        if (existingRecord == null) {
            records.put(debtAccount.getDebtorAccountName(), Arrays.asList(debtAccount));
            return;
        }

        for (DebtAccount da : existingRecord.stream().toList()) {
            if (da.getCreditorAccountName().equalsIgnoreCase(debtAccount.getCreditorAccountName())) {
                da.setAmount(da.getAmount().add(debtAccount.getAmount()));
            }
        }
    }

    @Override
    public List<DebtAccount> findByDebtorAccount(CustomerAccount account) {
        return records.get(account.getName());
    }

    @Override
    public void remove(DebtAccount debtAccount) {
        var existingRecord = records.get(debtAccount.getDebtorAccountName());
        if (existingRecord == null) {
            return;
        }
        boolean foundIndex = false;
        int removedIndex = 0;
        for (int i = 0; i < existingRecord.size() ; i++) {
            if (existingRecord.get(i).getCreditorAccountName().equalsIgnoreCase(debtAccount.getCreditorAccountName())) {
                foundIndex = true;
                removedIndex = i;
                break;
            }
        }
        if (foundIndex) {
            existingRecord.remove(removedIndex);
            records.put(debtAccount.getDebtorAccountName(), existingRecord);
        }
    }
}
