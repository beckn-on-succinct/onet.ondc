package in.succinct.onet.ondc.extensions;

import in.succinct.bpp.core.adaptor.NetworkAdaptorFactory;

public class OndcStaging12Registry extends OndcRegistry{
    static {
        NetworkAdaptorFactory.getInstance().registerAdaptor(new OndcStaging12Registry()   );
    }
    public OndcStaging12Registry() {
        super("ondc1_2");
    }
}
