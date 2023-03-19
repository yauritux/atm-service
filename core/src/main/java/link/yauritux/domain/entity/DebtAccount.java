package link.yauritux.domain.entity;

import link.yauritux.sharedkernel.exception.DomainException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author yauritux@gmail.com
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
public class DebtAccount {

    private String debtorAccountName;
    private String creditorAccountName;
    private BigDecimal amount;

    public DebtAccount(String debtorName, String creditorName, BigDecimal amount) {
        if (debtorName == null || debtorName.trim().equals("")) {
            throw new DomainException("debtor name is required!");
        }
        if (creditorName == null || creditorName.trim().equals("")) {
            throw new DomainException("creditor name is required!");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("owed amount should not be less than or equal to zero!");
        }
        debtorAccountName = debtorName;
        creditorAccountName = creditorName;
        this.amount = amount;
    }
}
