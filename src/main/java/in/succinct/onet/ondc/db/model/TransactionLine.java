package in.succinct.onet.ondc.db.model;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;
import in.succinct.bpp.core.db.model.BecknOrderMeta;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface TransactionLine extends Model {
    // S. No.	Buyer NP Name	Seller NP Name	Order Create Date & Time	Network Order Id	Network Transaction Id	Seller NP Order Id	Item Id	Qty	"Seller NP Type
            // (MSN/ ISN)"	Order Status	Name of Seller	Seller Pincode	Seller City	SKU Name	SKU Code	Order Category (F&B/ Grocery/ Home & Decor)	Ready to Ship At Date & Time	Shipped At Date & Time	Delivered At Date & Time	Delivery Type (On-network/ Off-network)	Logistics Seller NP Name (For on-network delivery)	Logistics Network Order Id (For on-network delivery)	Logistics Network Transaction Id (For on-network delivery)	Delivery City	Delivery Pincode	Cancelled At Date & Time	Cancelled By (Buyer/ Seller/ Logistics)	Cancellation Reason / Return Reason (Network Error Description)	Total Shipping Charges	Total Order Value	Total Refund Amount

    @HIDDEN
    public Long getBecknOrderMetaId();
    public void setBecknOrderMetaId(Long id);
    public BecknOrderMeta getBecknOrderMeta();

    
    public String getBuyerNPName();
    public void setBuyerNPName(String buyerNPName);

    public String getSellerNPName();
    public void setSellerNPName(String sellerNPName);


    public String getCreateDateTime();
    public void setCreateDateTime(String createDateTime);

    public String getNetworkOrderId();
    public void setNetworkOrderId(String orderId);

    public String getNetworkTransactionId();
    public void setNetworkTransactionId(String networkTransactionId);

    public String getSellerNPOrderId();
    public void setSellerNPOrderId(String sellerNPOrderId);


    public String getItemId();
    public void setItemId(String itemId);

    @COLUMN_DEF(StandardDefault.ZERO)
    public int getQuantity();
    public void setQuantity(int quantity);


    public String getSellerNPType();
    public void setSellerNPType(String sellerNPType);


    public String getOrderStatus();
    public void setOrderStatus(String orderStatus);


    public String getNameOfTheSeller();
    public void setNameOfTheSeller(String nameOfTheSeller);

    public String getSellerPincode();
    public void setSellerPincode(String sellerPincode);

    public String getSellerCity();
    public void setSellerCity(String sellerCity);


    public String getSKUName();
    public void setSKUName(String sKUName);

    public String getSKUCode();
    public void setSKUCode(String sKUCode);

    @Enumeration("F&B,Grocery,Home & Decor")
    public String getOrderCategory();
    public void setOrderCategory(String orderCategory);
    

    public String getReadyToShipAt();
    public void setReadyToShipAt(String readyToShipAtDate);

    public String getShippedAt();
    public void setShippedAt(String shippedAtDateTime);


    public String getDeliveredAt();
    public void setDeliveredAt(String deliveredAtDateTime);

    @Enumeration("On-network,Off-network")
    public String getDeliveryType();
    public void setDeliveryType(String deliveryType);

    public String getLogisticsSellerNPName();
    public void setLogisticsSellerNPName(String logisticsSellerNPName);
    
    public String getLogisticsNetworkTransactionId();
    public void setLogisticsNetworkTransactionId(String logisticsNetworkTransactionId);

    public String getDeliveryCity();
    public void setDeliveryCity(String deliveryCity);

    public String getDeliveryPincode();
    public void setDeliveryPincode(String deliveryPincode);


    public Timestamp getCancelledAt();
    public void setCancelledAt(Timestamp cancelledAt);


    @Enumeration("Buyer,Seller,Logistics")
    public String getCancelledBy();
    public void setCancelledBy(String cancelledBy);

    public String getCancellationReason();
    public void setCancellationReason(String cancellationReason);

    @COLUMN_DEF(StandardDefault.ZERO)
    public double getTotalShippingCharges();
    public void setTotalShippingCharges(double totalShippingCharges);

    @COLUMN_DEF(StandardDefault.ZERO)
    public double getTotalOrderValue();
    public void setTotalOrderValue(double totalOrderValue);

    
    @COLUMN_DEF(StandardDefault.ZERO)
    public double getTotalRefundAmount();
    public void setTotalRefundAmount(double totalRefundAmount);





    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");





}
