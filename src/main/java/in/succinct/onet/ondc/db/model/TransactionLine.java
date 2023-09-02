package in.succinct.onet.ondc.db.model;

import com.venky.network.Network;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import in.succinct.bpp.core.db.model.BecknOrderMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@MENU("Dashboard")
public interface TransactionLine extends Model {
    // S. No.	Buyer NP Name	Seller NP Name	Order Create Date & Time	Network Order Id	Network Transaction Id	Seller NP Order Id	Item Id	Qty	"Seller NP Type
            // (MSN/ ISN)"	Order Status	Name of Seller	Seller Pincode	Seller City	SKU Name	SKU Code	Order Category (F&B/ Grocery/ Home & Decor)	Ready to Ship At Date & Time	Shipped At Date & Time	Delivered At Date & Time	Delivery Type (On-network/ Off-network)	Logistics Seller NP Name (For on-network delivery)	Logistics Network Order Id (For on-network delivery)	Logistics Network Transaction Id (For on-network delivery)	Delivery City	Delivery Pincode	Cancelled At Date & Time	Cancelled By (Buyer/ Seller/ Logistics)	Cancellation Reason / Return Reason (Network Error Description)	Total Shipping Charges	Total Order Value	Total Refund Amount

    @HIDDEN
    @EXPORTABLE(value = false)
    public Long getBecknOrderMetaId();
    public void setBecknOrderMetaId(Long id);
    public BecknOrderMeta getBecknOrderMeta();


    @Index
    public String getBuyerNPName();
    public void setBuyerNPName(String buyerNPName);

    @Index
    public String getSellerNPName();
    public void setSellerNPName(String sellerNPName);


    public String getCreateDateTime();
    public void setCreateDateTime(String createDateTime);

    public String getNetworkOrderId();
    public void setNetworkOrderId(String orderId);

    @Index
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


    @Index
    public String getOrderStatus();
    public void setOrderStatus(String orderStatus);

    @Index
    public String getFulfillmentStatus();
    public void setFulfillmentStatus(String fulfillmentStatus);

    @Index
    public String getReturnStatus();
    public void setReturnStatus(String returnStatus);

    @Index
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

    @Index
    public String getLogisticsSellerNPName();
    public void setLogisticsSellerNPName(String logisticsSellerNPName);

    @Index
    public String getLogisticsNetworkTransactionId();
    public void setLogisticsNetworkTransactionId(String logisticsNetworkTransactionId);

    @Index
    public String getDeliveryCity();
    public void setDeliveryCity(String deliveryCity);

    @Index
    public String getDeliveryPincode();
    public void setDeliveryPincode(String deliveryPincode);


    public String getCancelledAt();
    public void setCancelledAt(String cancelledAt);


    @Enumeration(enumClass = "in.succinct.beckn.Cancellation$CancelledBy")
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

    public String getReturnReason();
    public void setReturnReason(String returReason);


    @Index
    public String getNetworkId();
    public void setNetworkId(String networkId);

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");





}
