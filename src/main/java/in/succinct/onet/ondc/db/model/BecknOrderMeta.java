package in.succinct.onet.ondc.db.model;


import java.util.List;

public interface BecknOrderMeta extends in.succinct.bpp.core.db.model.BecknOrderMeta {
    public List<TransactionLine> getTransactionLines();

    public void mineTransactionLines();
}
