package in.succinct.onet.ondc.extensions;

import in.succinct.onet.core.adaptor.NetworkAdaptorFactory;

public class OndcStagingRegistry extends OndcRegistry{
    static {
        NetworkAdaptorFactory.getInstance().registerAdaptor(new OndcStagingRegistry()   );
    }
    public OndcStagingRegistry() {
        super("ondc");
    }
}
