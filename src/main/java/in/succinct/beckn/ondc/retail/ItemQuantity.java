package in.succinct.beckn.ondc.retail;

import in.succinct.beckn.Quantity;

public class ItemQuantity extends in.succinct.beckn.ItemQuantity {
    @Override
    public void setAllocated(Quantity q) {
        q.setCount(String.valueOf(q.getCount()));
        super.setAllocated(q);
    }

    @Override
    public void setAvailable(Quantity q) {
        q.setCount(String.valueOf(q.getCount()));
        super.setAvailable(q);
    }

    @Override
    public void setMaximum(Quantity q) {
        q.setCount(String.valueOf(q.getCount()));
        super.setMaximum(q);
    }

    @Override
    public void setMinimum(Quantity q) {
        q.setCount(String.valueOf(q.getCount()));
        super.setMinimum(q);
    }

    @Override
    public void setSelected(Quantity q) {
        q.setCount(String.valueOf(q.getCount()));
        super.setSelected(q);
    }


}
