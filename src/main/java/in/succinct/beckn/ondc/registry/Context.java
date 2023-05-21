package in.succinct.beckn.ondc.registry;

import in.succinct.beckn.BecknObject;

public class Context extends in.succinct.beckn.Context {

    public Operation getOperation(){
        return get(Operation.class, "operation");
    }
    public void setOperation(Operation operation){
        set("operation",operation);
    }

    public static class Operation extends BecknObject {
        public int getOpsNo(){
            return getInteger("ops_no");
        }
        public void setOpsNo(int ops_no){
            set("ops_no",ops_no);
        }

    }
}
