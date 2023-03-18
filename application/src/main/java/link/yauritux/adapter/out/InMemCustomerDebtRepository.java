package link.yauritux.adapter.out;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.CustomerDebt;
import link.yauritux.port.spi.CustomerDebtRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yauritux@gmail.com
 * @version 1.0.0
 */
public class InMemCustomerDebtRepository implements CustomerDebtRepositoryPort {

    private final Map<String, CustomerDebt> records = new HashMap<>();

    @Override
    public void save(CustomerDebt customerDebt) {
        var existingRecord = records.get(customerDebt.getCreditorAccountName());
        if (existingRecord == null) {
            records.put(customerDebt.getCreditorAccountName(), customerDebt);
        }
    }

    @Override
    public Optional<CustomerDebt> findByDebtorAccount(CustomerAccount account) {
        return Optional.ofNullable(records.get(account.getName()));
    }
}
