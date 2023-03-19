package link.yauritux.port.out;

import link.yauritux.domain.entity.CustomerAccount;

import java.util.Optional;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
public interface CustomerAccountRepositoryPort {

    void save(CustomerAccount customerAccount);
    Optional<CustomerAccount> findCustomerByName(final String name);
}
