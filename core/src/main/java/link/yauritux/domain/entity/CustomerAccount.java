package link.yauritux.domain.entity;

import link.yauritux.sharedkernel.exception.DomainException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
@AllArgsConstructor
public class CustomerAccount {

    @Getter
    private String name;

    @Setter
    @Getter
    private BigDecimal balance;

    public CustomerAccount(String name) {
        if (name == null || name.trim().equalsIgnoreCase("")) {
            throw new DomainException("Customer account name cannot be empty!");
        }
        this.name = name;
        this.balance = BigDecimal.ZERO;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerAccount that = (CustomerAccount) o;
        return Objects.equals(this.name, that.name);
    }
}
