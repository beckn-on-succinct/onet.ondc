package in.succinct.beckn.ondc.retail;

import in.succinct.beckn.Amount;
import in.succinct.beckn.Cancellation;
import in.succinct.beckn.Descriptor;
import in.succinct.beckn.Payer;
import in.succinct.beckn.SettlementCorrection;

public class Order extends in.succinct.beckn.Order {
    @Override
    public boolean isExtendedAttributesDisplayed(){
        return false;
    }

    public Order() { super(); }

    public Order(String payload){
        super(payload);
    }

    @Override
    public Cancellation getCancellation(){
        return get(Cancellation.class,"@org/ondc/cancellation");
    }

    @Override
    public void setCancellation(Cancellation cancellation){
        set("@org/ondc/cancellation",cancellation.getInner());
    }


    public String getInvoiceNo(){
        return get("invoice_no");
    }
    public void setInvoiceNo(String invoice_no){
        set("invoice_no",invoice_no);
    }

    public Amount getIncomeTaxWithheld(){
        return get(Amount.class, "withholding_tax_tds");
    }
    public void setIncomeTaxWithheld(Amount income_tax_withheld){
        set("withholding_tax_tds",income_tax_withheld);
    }

    public Amount getGstWithheld(){
        return get(Amount.class, "withholding_tax_gst");
    }
    public void setGstWithheld(Amount gst_withheld){
        set("withholding_tax_gst",gst_withheld);
    }

    public OrderReconStatus getOrderReconStatus(){
        return getEnum(OrderReconStatus.class, "order_recon_status");
    }
    public void setOrderReconStatus(OrderReconStatus order_recon_status){
        setEnum("order_recon_status",order_recon_status);
    }



    public String getCollectorSubscriberId(){
        return get("collector_app_id");
    }
    public void setCollectorSubscriberId(String collector_subscriber_id){
        set("collector_app_id",collector_subscriber_id);
    }

    public String getReceiverSubscriberId(){
        return get("receiver_app_id");
    }
    public void setReceiverSubscriberId(String receiver_subscriber_id){
        set("receiver_app_id",receiver_subscriber_id);
    }


    public Payer getPayer(){
        return get(Payer.class, "payerdetails");
    }
    public void setPayer(Payer payer){
        set("payerdetails",payer);
    }


    public SettlementReasonCode getSettlementReasonCode(){
        return getEnum(SettlementReasonCode.class, "settlement_reason_code", in.succinct.beckn.Order.SettlementReasonCode.convertor);
    }
    public void setSettlementReasonCode(SettlementReasonCode settlement_reason_code){
        setEnum("settlement_reason_code",settlement_reason_code, SettlementReasonCode.convertor);
    }

    public SettlementCorrection getSettlementCorrection(){
        return get(SettlementCorrection.class, "correction");
    }
    public void setSettlementCorrection(SettlementCorrection settlement_correction){
        set("correction",settlement_correction);
    }

    public String getCollectionTransactionId(){
        return get("transaction_id");
    }
    public void setCollectionTransactionId(String transaction_id){
        set("transaction_id",transaction_id);
    }

    public String getSettlementId(){
        return get("settlement_id");
    }
    public void setSettlementId(String settlement_id){
        set("settlement_id",settlement_id);
    }

    public String getSettlementReference(){
        return get("settlement_reference_no");
    }
    public void setSettlementReference(String settlement_reference){
        set("settlement_reference_no",settlement_reference);
    }


    public ReconStatus getReconStatus(){
        return getEnum(ReconStatus.class, "recon_status" , ReconStatus.convertor);
    }
    public void setReconStatus(ReconStatus recon_status){
        setEnum("recon_status",recon_status, ReconStatus.convertor);
    }

    public Double getDiffAmount(){
        return getDouble("diff_amount", null);
    }
    public void setDiffAmount(Double diff_amount){
        set("diff_amount",diff_amount);
    }


    public ReconStatus getCounterpartyReconStatus(){
        return getEnum(ReconStatus.class, "counter_party_recon_status");
    }
    public void setCounterpartyReconStatus(ReconStatus counter_party_recon_status){
        setEnum("counter_party_recon_status",counter_party_recon_status);
    }

    public Double getCounterPartyDiffAmount(){
        return getDouble("counter_party_diff_amount",null);
    }
    public void setCounterPartyDiffAmount(Double counter_party_diff_amount){
        set("counter_party_diff_amount",counter_party_diff_amount);
    }

    public Descriptor getReconMessage(){
        return get(Descriptor.class, "message");
    }
    public void setReconMessage(Descriptor recon_message){
        set("message",recon_message);
    }


    @Override
    public String getBppTaxNumber(){
        return getTag("bpp_terms","tax_number");
    }
    @Override
    public void setBppTaxNumber(String bpp_tax_number){
        setTag("bpp_terms","tax_number", bpp_tax_number);
    }
    @Override
    public String getBapTaxNumber(){
        return getTag("bap_terms","tax_number");
    }
    @Override
    public void setBapTaxNumber(String bap_tax_number){
        setTag("bap_terms","tax_number",bap_tax_number);
    }

}
