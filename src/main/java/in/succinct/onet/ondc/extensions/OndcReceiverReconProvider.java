package in.succinct.onet.ondc.extensions;

import in.succinct.beckn.Request;
import in.succinct.bpp.core.adaptor.CommerceAdaptor;
import in.succinct.bpp.core.adaptor.rsp.ReceiverReconProviderFactory;
import in.succinct.bpp.core.extensions.SuccinctReceiverReconProvider;

public class OndcReceiverReconProvider extends SuccinctReceiverReconProvider {

    static {
        ReceiverReconProviderFactory.getInstance().registerAdaptor("ondc",OndcReceiverReconProvider.class);
    }
    protected OndcReceiverReconProvider(CommerceAdaptor adaptor) {
        super(adaptor);
    }

    @Override
    public void receiver_recon(Request request, Request reply) {
        super.receiver_recon(request,reply);
    }
}
