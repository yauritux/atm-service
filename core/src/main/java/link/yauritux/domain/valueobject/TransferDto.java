package link.yauritux.domain.valueobject;

import link.yauritux.domain.entity.CustomerAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author yauritux@gmail.com
 * @version 1.0.0
 */
@AllArgsConstructor
@Getter
public class TransferDto {

    private CustomerAccount targetAccount;
    private BigDecimal transferAmount;
}
