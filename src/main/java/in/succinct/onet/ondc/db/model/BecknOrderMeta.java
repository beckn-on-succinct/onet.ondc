package in.succinct.onet.ondc.db.model;


import in.succinct.bpp.core.adaptor.CommerceAdaptor;
import in.succinct.bpp.core.adaptor.NetworkAdaptor;

import java.util.List;

public interface BecknOrderMeta extends in.succinct.bpp.core.db.model.BecknOrderMeta {
    public List<TransactionLine> getTransactionLines();

    public void mineTransactionLines(NetworkAdaptor networkAdaptor, CommerceAdaptor commerceAdaptor);
}
