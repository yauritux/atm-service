package link.yauritux.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yauritux@gmail.com
 * @version 1.0.0
 */
@AllArgsConstructor
@Data
public class CustomerDebt {

    private String debtorAccountName;
    private String creditorAccountName;
    private BigDecimal owedBalance;
}
