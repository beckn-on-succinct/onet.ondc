package in.succinct.beckn.ondc.retail;

import in.succinct.beckn.SettlementDetails;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Payment extends in.succinct.beckn.Payment {
    @Override
    public boolean isExtendedAttributesDisplayed(){
        return false;
    }

    public Payment(){
        super();
    }
    public Payment(String payload){
        super(payload);
    }

    @Override
    public NegotiationStatus getCollectedByStatus(){
        String s =  get("@ondc/org/collected_by_status");
        return s== null ? null : NegotiationStatus.valueOf(s);
    }
    @Override
    public void setCollectedByStatus(NegotiationStatus collected_by_status){
        set("@ondc/org/collected_by_status",collected_by_status == null ? null : collected_by_status.toString());
    }

    private static Map<String,CommissionType>  commissionTypeMap = new HashMap<>(){{
        put("percent",CommissionType.Percent);
        put("Amount",CommissionType.Amount);
    }}  ;
    private static Map<CommissionType,String>  rcommissionTypeMap = new HashMap<>(){{
        put(CommissionType.Percent,"percent");
        put(CommissionType.Amount,"Amount");
    }}  ;

    @Override
    public CommissionType getBuyerAppFinderFeeType() {
        String s = get("@ondc/org/buyer_app_finder_fee_type");
        return s == null ? null : commissionTypeMap.get(s);
    }

    @Override
    public void setBuyerAppFinderFeeType(CommissionType buyer_app_finder_fee_type) {
        set("@ondc/org/buyer_app_finder_fee_type", buyer_app_finder_fee_type  == null ? null : rcommissionTypeMap.get(buyer_app_finder_fee_type));
    }

    @Override
    public Double getBuyerAppFinderFeeAmount(){
        return getDouble("@ondc/org/buyer_app_finder_fee_amount",null);
    }
    @Override
    public void setBuyerAppFinderFeeAmount(Double buyer_app_finder_fee_amount){
        set("@ondc/org/buyer_app_finder_fee_amount",buyer_app_finder_fee_amount);
    }

    @Override
    public Double getWithholdingAmount(){
        return getDouble("@ondc/org/withholding_amount");
    }
    @Override
    public void setWithholdingAmount(Double withholding_amount){
        set("@ondc/org/withholding_amount",withholding_amount);
    }

    @Override
    public NegotiationStatus getWithholdingAmountStatus(){
        String s = get("@ondc/org/withholding_amount_status");
        return s == null ? null : NegotiationStatus.valueOf(s);
    }
    @Override
    public void setWithholdingAmountStatus(NegotiationStatus withholding_amount_status){
        set("@ondc/org/withholding_amount_status",withholding_amount_status == null ? null : withholding_amount_status.toString());
    }

    @Override
    public Duration getReturnWindow(){
        String rw = get("@ondc/org/return_window");
        return rw == null ? null : Duration.parse(rw);
    }
    @Override
    public void setReturnWindow(Duration return_window){
        set("@ondc/org/return_window",return_window == null ? null : return_window.toString());
    }

    @Override
    public NegotiationStatus getReturnWindowStatus(){
        String s = get("@ondc/org/return_window_status");
        return s == null ? null : NegotiationStatus.valueOf(s);
    }

    @Override
    public void setReturnWindowStatus(NegotiationStatus return_window_status){
        set("@ondc/org/return_window_status",return_window_status == null ? null : return_window_status.toString());
    }

    @Override
    public NegotiationStatus getSettlementBasisStatus(){
        String s= get("@ondc/org/settlement_basis_status");
        return s == null ? null : NegotiationStatus.valueOf(s);
    }
    @Override
    public void setSettlementBasisStatus(NegotiationStatus settlement_basis_status){
        set("@ondc/org/settlement_basis_status",settlement_basis_status == null ? null : settlement_basis_status.toString());
    }

    @Override
    public SettlementBasis getSettlementBasis(){
        String s = get("@ondc/org/settlement_basis");
        return s == null ? null : SettlementBasis.valueOf(s);
    }

    @Override
    public void setSettlementBasis(SettlementBasis settlement_basis){
        set("@ondc/org/settlement_basis",settlement_basis == null ? null : settlement_basis.toString());
    }

    @Override
    public Duration getSettlementWindow(){
        String sw =  get("@ondc/org/settlement_window");
        return sw == null ? null : Duration.parse(sw);
    }
    @Override
    public void setSettlementWindow(Duration settlement_window){
        set("@ondc/org/settlement_window",settlement_window== null ? null : settlement_window.toString());
    }

    @Override
    public NegotiationStatus getSettlementWindowStatus(){
        String ns = get("@ondc/org/settlement_window_status");
        return ns==null? null : NegotiationStatus.valueOf(ns);
    }
    @Override
    public void setSettlementWindowStatus(NegotiationStatus settlement_window_status){
        set("@ondc/org/settlement_window_status",settlement_window_status == null ? null :
                settlement_window_status.toString());
    }

    @Override
    public SettlementDetails getSettlementDetails(){
        return get(SettlementDetails.class,"@ondc/org/settlement_details");
    }
    @Override
    public void setSettlementDetails(SettlementDetails settlement_details){
        set("@ondc/org/settlement_details",settlement_details.getInner());
    }


}
