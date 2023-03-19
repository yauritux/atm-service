package link.yauritux.adapter.out.inmemory;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.port.out.CustomerAccountRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yauritux@gmail.com
 * @version 1.0.0
 */
public class InMemCustomerAccountRepository implements CustomerAccountRepositoryPort {

    private Map<String, CustomerAccount> records = new HashMap<>();

    @Override
    public void save(CustomerAccount customerAccount) {
        var existingRecord = records.get(customerAccount.getName());
        if (existingRecord == null) {
            records.put(customerAccount.getName(), customerAccount);
        }
    }

    @Override
    public Optional<CustomerAccount> findCustomerByName(String name) {
        return Optional.ofNullable(records.get(name));
    }
}
