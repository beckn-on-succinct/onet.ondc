package in.succinct.onet.ondc.db.model;

import com.venky.core.util.Bucket;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.beckn.Amount;
import in.succinct.beckn.BecknObject;
import in.succinct.beckn.BreakUp.BreakUpElement;
import in.succinct.beckn.BreakUp.BreakUpElement.BreakUpCategory;
import in.succinct.beckn.Cancellation;
import in.succinct.beckn.Cancellation.CancelledBy;
import in.succinct.beckn.CancellationReasons.CancellationReasonCode;
import in.succinct.beckn.Context;
import in.succinct.beckn.Fulfillment;
import in.succinct.beckn.Fulfillment.FulfillmentStatus;
import in.succinct.beckn.Fulfillment.FulfillmentStatus.FulfillmentStatusConvertor;
import in.succinct.beckn.Item;
import in.succinct.beckn.Order;
import in.succinct.beckn.Order.Return;
import in.succinct.beckn.Order.Return.ReturnStatus;
import in.succinct.beckn.Order.Returns;
import in.succinct.beckn.Order.Status;
import in.succinct.beckn.ReturnReasons.ReturnReasonCode;
import in.succinct.beckn.TagGroups;
import in.succinct.beckn.Tags;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class BecknOrderMetaImpl extends ModelImpl<BecknOrderMeta> {
    public BecknOrderMetaImpl(BecknOrderMeta meta){
        super(meta);
    }

    public void mineTransactionLines(){
        TaskManager.instance().executeAsync(new MineTransactionLineTask(getProxy()),false);
    }
    public static class MineTransactionLineTask implements Task {
        BecknOrderMeta meta;
        public MineTransactionLineTask(BecknOrderMeta  meta){
            this.meta = meta;
        }


        @Override
        public void execute() {
            meta = Database.getTable(BecknOrderMeta.class).lock(meta.getId());

            for (TransactionLine line :meta.getTransactionLines()){
                line.destroy();
            }
            if (ObjectUtil.isVoid(meta.getBapOrderId())){
                return;
            }
            if (ObjectUtil.isVoid(meta.getECommerceOrderId())){
                return;
            }

            Order order = new Order(meta.getOrderJson());
            Context context =new Context(meta.getContextJson());
            if (meta.getStatusUpdatedAtJson() == null) {
                meta.setStatusUpdatedAtJson("{}");
            }

            Bucket refund = new Bucket();
            Returns returns = order.getReturns();
            if (returns != null ){
                for (Return ret : returns){
                    Amount ref = ret.getRefund();
                    if (ref != null) {
                        refund.increment(ref.getValue());
                    }
                }
            }
            Set<String> refundedLineStatus = new HashSet<>(){{
                add("Cancelled");
                add("Liquidated");
                add("Return_Delivered");
            }} ;


            for (Item item : order.getItems()) {
                TransactionLine line = Database.getTable(TransactionLine.class).newRecord();
                line.setTotalRefundAmount(refund.doubleValue());
                line.setBecknOrderMetaId(meta.getId());
                line.setBuyerNPName(context.getBapId());
                line.setSellerNPName(context.getBppId());
                line.setNetworkOrderId(meta.getBapOrderId());
                line.setNetworkTransactionId(meta.getBecknTransactionId());
                line.setSellerNPOrderId(meta.getECommerceOrderId());
                line.setItemId(item.getId());
                JSONObject inspectQuantity = (JSONObject) item.getInner().get("quantity");
                if (inspectQuantity.containsKey("count")){
                    line.setQuantity(item.getQuantity().getCount());
                }else {
                    line.setQuantity(item.getItemQuantity().getAllocated().getCount());
                }

                //line.setQuantity(item.getQuantity().getCount());
                line.setOrderCategory(getCategory(item.getCategoryId()));
                line.setSellerNPType("ISN");
                line.setOrderStatus(order.getState().toString());
                line.setNameOfTheSeller(order.getProvider().getDescriptor().getName());
                line.setSellerPincode(order.getFulfillment().getStart().getLocation().getAddress().getPinCode());
                line.setSellerCity(order.getFulfillment().getStart().getLocation().getAddress().getCity());
                line.setSKUCode(item.getDescriptor().getCode());
                line.setSKUName(item.getDescriptor().getName());
                line.setNetworkId(meta.getNetworkId());

                Date createdAt = meta.getStatusReachedAt(Status.Accepted);
                if (createdAt != null) {
                    line.setCreateDateTime(TransactionLine.DATE_FORMAT.format(createdAt));
                }

                Date packedAt = meta.getFulfillmentStatusReachedAt(FulfillmentStatus.Packed);
                if (packedAt != null){
                    line.setReadyToShipAt(TransactionLine.DATE_FORMAT.format(packedAt));
                }

                Date shippedAt = meta.getFulfillmentStatusReachedAt(FulfillmentStatus.Order_picked_up);
                if (shippedAt != null) {
                    line.setShippedAt(TransactionLine.DATE_FORMAT.format(shippedAt));
                }


                Date deliveredAt = meta.getStatusReachedAt(Status.Completed);
                if (deliveredAt != null) {
                    line.setDeliveredAt(TransactionLine.DATE_FORMAT.format(deliveredAt));
                }

                Date cancelledAt = meta.getStatusReachedAt(Status.Cancelled);
                if (cancelledAt != null) {
                    line.setCancelledAt(TransactionLine.DATE_FORMAT.format(cancelledAt));
                }

                line.setDeliveryType("Off-network");

                line.setDeliveryCity(order.getFulfillment().getEnd().getLocation().getAddress().getCity());
                line.setDeliveryPincode(order.getFulfillment().getEnd().getLocation().getAddress().getPinCode());
                if (order.getState() == Status.Cancelled){
                    Cancellation cancellation = order.getCancellation();
                    if (cancellation != null) {
                        line.setCancelledBy(cancellation.getCancelledBy().name());
                        if (cancellation.getSelectedReason() != null && cancellation.getSelectedReason().getDescriptor() != null) {
                            line.setCancellationReason(order.getCancellation().getSelectedReason().getDescriptor().getLongDesc());
                        }
                    }
                }


                line.setTotalOrderValue(order.getQuote().getPrice().getValue());

                for (BreakUpElement element : order.getQuote().getBreakUp()){
                    if (element.getType() == BreakUpCategory.delivery){
                        line.setTotalShippingCharges(element.getPrice().getValue());
                        break;
                    }
                }
                if (item.getTags() == null){
                    item.setTags(new TagGroups());
                }
                Fulfillment fulfillment = order.getFulfillments().get(item.getFulfillmentId());

                String lineStatus = fulfillment.getState().getDescriptor().getCode();

                String reason = fulfillment.getTag("cancellation_request","reason_id");

                if (!ObjectUtil.isVoid(lineStatus)){
                    if (refundedLineStatus.contains(lineStatus)) {
                        if (ObjectUtil.equals(lineStatus, "Cancelled") && !ObjectUtil.isVoid(reason)) {
                            line.setCancellationReason(CancellationReasonCode.convertor.valueOf(reason).name());
                            line.setCancelledBy(CancellationReasonCode.convertor.valueOf(reason).isUsableByBuyerParty() ? CancelledBy.BUYER.name() : CancelledBy.PROVIDER.name());
                        } else {
                            if (!ObjectUtil.isVoid(reason)) {
                                line.setReturnReason(ReturnReasonCode.convertor.valueOf(reason).name());
                            }
                            line.setReturnStatus(lineStatus);
                        }
                    }
                }
                line.setFulfillmentStatus(new FulfillmentStatusConvertor().toString(order.getFulfillment().getFulfillmentStatus()));

                line.save();
            }



        }

        private String getCategory(String categoryId) {
            return "Grocery";
        }
    }
}
