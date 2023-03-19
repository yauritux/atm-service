package link.yauritux.adapter.out.inmemory;

import link.yauritux.domain.entity.DebtAccount;
import link.yauritux.exception.ApplicationException;
import link.yauritux.port.out.DebtAccountRepositoryPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yauritux@gmail.com
 * @version 1.0.0
 */
public class InMemDebtAccountRepository implements DebtAccountRepositoryPort<String> {

    private final Map<String, List<DebtAccount>> debtorRecords = new HashMap<>();

    @Override
    public void save(DebtAccount debtAccount) {
        if (debtAccount == null) {
            throw new ApplicationException("debt account is missing!");
        }
        if (debtAccount.getDebtorAccountName() == null || debtAccount.getDebtorAccountName().trim().equals("")) {
            throw new ApplicationException("debtor name is missing!");
        }
        if (debtAccount.getCreditorAccountName() == null || debtAccount.getCreditorAccountName().trim().equals("")) {
            throw new ApplicationException("creditor name is missing!");
        }
        var existingRecord = debtorRecords.get(debtAccount.getDebtorAccountName());
        if (existingRecord == null || existingRecord.isEmpty()) {
            var daRecords = new ArrayList<DebtAccount>();
            daRecords.add(debtAccount);
            debtorRecords.put(debtAccount.getDebtorAccountName(), daRecords);
            return;
        }

        boolean newRecords = true;
        for (DebtAccount da : existingRecord) {
            if (da.getCreditorAccountName().equalsIgnoreCase(debtAccount.getCreditorAccountName())) {
                da.setAmount(da.getAmount().add(debtAccount.getAmount()));
                newRecords = false;
            }
        }
        if (newRecords) {
            existingRecord.add(debtAccount);
            debtorRecords.put(debtAccount.getDebtorAccountName(), existingRecord);
        }
    }

    @Override
    public List<DebtAccount> findByDebtorAccount(String accountId) {
        return debtorRecords.get(accountId);
    }

    @Override
    public void remove(DebtAccount debtAccount) {
        var existingRecord = debtorRecords.get(debtAccount.getDebtorAccountName());
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
            debtorRecords.put(debtAccount.getDebtorAccountName(), existingRecord);
        }
    }
}
