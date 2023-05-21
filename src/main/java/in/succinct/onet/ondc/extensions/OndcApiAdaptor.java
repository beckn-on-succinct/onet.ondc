package in.succinct.onet.ondc.extensions;

import in.succinct.beckn.Request;
import in.succinct.bpp.core.adaptor.CommerceAdaptor;
import in.succinct.bpp.core.adaptor.NetworkAdaptor;
import in.succinct.bpp.core.adaptor.api.NetworkApiAdaptor;

public class OndcApiAdaptor extends NetworkApiAdaptor {
    OndcApiAdaptor(NetworkAdaptor networkAdaptor) {
        super(networkAdaptor);
    }


    @Override
    public void issue(CommerceAdaptor adaptor, Request request, Request reply) {

        super.issue(adaptor, request, reply);
    }

    @Override
    public void issue_status(CommerceAdaptor adaptor, Request request, Request reply) {
        super.issue_status(adaptor, request, reply);
    }
}
