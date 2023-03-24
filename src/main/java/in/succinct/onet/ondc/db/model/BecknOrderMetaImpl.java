package in.succinct.onet.ondc.db.model;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.beckn.BecknObject;
import in.succinct.beckn.BreakUp.BreakUpElement;
import in.succinct.beckn.BreakUp.BreakUpElement.BreakUpCategory;
import in.succinct.beckn.Context;
import in.succinct.beckn.Item;
import in.succinct.beckn.Order;
import in.succinct.beckn.Order.Status;
import in.succinct.bpp.core.adaptor.CommerceAdaptor;
import in.succinct.bpp.core.adaptor.NetworkAdaptor;

import java.util.Date;

public class BecknOrderMetaImpl extends ModelImpl<BecknOrderMeta> {
    public BecknOrderMetaImpl(BecknOrderMeta meta){
        super(meta);
    }

    public void mineTransactionLines(NetworkAdaptor networkAdaptor, CommerceAdaptor commerceAdaptor){
        TaskManager.instance().executeAsync(new MineTransactionLineTask(getProxy(),networkAdaptor,commerceAdaptor),false);
    }
    public static class MineTransactionLineTask implements Task {
        BecknOrderMeta meta;
        NetworkAdaptor networkAdaptor;
        CommerceAdaptor commerceAdaptor;
        public MineTransactionLineTask(BecknOrderMeta  meta, NetworkAdaptor networkAdaptor, CommerceAdaptor commerceAdaptor){
            this.meta = meta;
            this.networkAdaptor = networkAdaptor;
            this.commerceAdaptor = commerceAdaptor;
        }


        @Override
        public void execute() {
            meta.getTransactionLines().forEach(Model::destroy);
            if (ObjectUtil.isVoid(meta.getBapOrderId())){
                return;
            }
            if (ObjectUtil.isVoid(meta.getECommerceOrderId())){
                return;
            }

            Order order = new Order(meta.getOrderJson());
            Context context =new Context(meta.getContextJson());
            BecknObject object = new BecknObject(meta.getStatusUpdatedAtJson() == null ? "{}" : meta.getStatusUpdatedAtJson());

            for (Item item : order.getItems()) {
                TransactionLine line = Database.getTable(TransactionLine.class).newRecord();
                line.setBecknOrderMetaId(meta.getId());
                line.setBuyerNPName(context.getBapId());
                line.setSellerNPName(context.getBppId());
                line.setNetworkOrderId(meta.getBapOrderId());
                line.setNetworkTransactionId(meta.getBecknTransactionId());
                line.setSellerNPOrderId(meta.getECommerceOrderId());
                line.setItemId(item.getId());
                line.setQuantity(item.getQuantity().getCount());
                line.setSellerNPType("ISN");
                line.setOrderStatus(order.getState().toString());
                line.setNameOfTheSeller(order.getProvider().getDescriptor().getName());
                line.setSellerPincode(order.getFulfillment().getStart().getLocation().getAddress().getPinCode());
                line.setSellerCity(order.getFulfillment().getStart().getLocation().getAddress().getCity());
                line.setSKUCode(item.getDescriptor().getCode());
                line.setSKUName(item.getDescriptor().getName());
                line.setOrderCategory(item.getCategoryId());

                Date createdAt = object.getTimestamp(Status.Accepted.toString());
                if (createdAt != null) {
                    line.setCreateDateTime(TransactionLine.DATE_FORMAT.format(createdAt));
                }

                Date packedAt = object.getTimestamp(Status.Packed.toString());
                if (packedAt != null){
                    line.setReadyToShipAt(TransactionLine.DATE_FORMAT.format(packedAt));
                }
                Date shippedAt = object.getTimestamp(Status.Out_for_delivery.toString());
                if (shippedAt != null) {
                    line.setShippedAt(TransactionLine.DATE_FORMAT.format(shippedAt));
                }
                Date deliveredAt = object.getTimestamp(Status.Completed.toString());
                if (deliveredAt != null) {
                    line.setDeliveredAt(TransactionLine.DATE_FORMAT.format(deliveredAt));
                }

                line.setDeliveryType("Off-network");

                line.setDeliveryCity(order.getFulfillment().getEnd().getLocation().getAddress().getCity());
                line.setDeliveryPincode(order.getFulfillment().getEnd().getLocation().getAddress().getPinCode());



                line.setTotalOrderValue(order.getQuote().getPrice().getValue());

                for (BreakUpElement element : order.getQuote().getBreakUp()){
                    if (element.getType() == BreakUpCategory.delivery){
                        line.setTotalShippingCharges(element.getPrice().getValue());
                        break;
                    }
                }

            }



        }
    }
}
