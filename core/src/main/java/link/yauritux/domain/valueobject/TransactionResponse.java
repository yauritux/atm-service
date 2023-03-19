package link.yauritux.domain.valueobject;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.DebtAccount;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yauritux@gmail.com
 * @version 1.0.0
 */
@NoArgsConstructor
@Data
public class TransactionResponse {

    private CustomerAccount customerAccount;
    private List<TransferDto> transferList = new ArrayList<>();
    private List<DebtAccount> debtAccounts = new ArrayList<>();

    public void addTransfer(TransferDto dto) {
        this.transferList.add(dto);
    }
}
