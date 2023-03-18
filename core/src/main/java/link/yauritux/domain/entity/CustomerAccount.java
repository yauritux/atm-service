package link.yauritux.domain.entity;

import link.yauritux.sharedkernel.exception.DomainException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
@AllArgsConstructor
@Data
public class CustomerAccount {

    private String name;
    private BigDecimal balance;

    public CustomerAccount(String name) {
        if (name == null || name.trim().equalsIgnoreCase("")) {
            throw new DomainException("Customer account name cannot be empty!");
        }
        this.name = name;
        this.balance = BigDecimal.ZERO;
    }
}
