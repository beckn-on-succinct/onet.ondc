package in.succinct.beckn.ondc.retail;


import java.time.Duration;

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

    
    

    public Duration getReturnWindow(){
        String rw = get("@ondc/org/return_window");
        return rw == null ? null : Duration.parse(rw);
    }
    public void setReturnWindow(Duration return_window){
        set("@ondc/org/return_window",return_window == null ? null : return_window.toString());
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



}
