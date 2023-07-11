package in.succinct.onet.ondc.extensions;

import in.succinct.beckn.Message;
import in.succinct.beckn.Request;
import in.succinct.bpp.core.adaptor.CommerceAdaptor;
import in.succinct.bpp.core.adaptor.NetworkAdaptor;
import in.succinct.bpp.core.adaptor.api.NetworkApiAdaptor;

import java.util.Map;

public class OndcApiAdaptor extends NetworkApiAdaptor {
    OndcApiAdaptor(NetworkAdaptor networkAdaptor) {
        super(networkAdaptor);
    }

    @Override
    public void call(CommerceAdaptor adaptor, Map<String, String> headers, Request request, Request response) {
        boolean networkByPass = false;
        if (request.getContext().getAction().startsWith("issue")){
            networkByPass = true;
        }else if (request.getContext().getAction().equals("receiver_recon")){
            networkByPass = true;
        }
        if (networkByPass) {
            super.call_by_pass(adaptor, headers, request, response);
        }else {
            super.call(adaptor, headers, request, response);
        }
    }

    @Override
    public void callback(CommerceAdaptor adaptor, Request reply) {

        boolean networkByPass = false;
        Request oReply = reply;
        if (reply.getContext().getAction().startsWith("on_issue")){
            networkByPass = true;
            oReply = adaptor.getIssueTracker().createNetworkResponse(reply);
        }else if (reply.getContext().getAction().equals("on_receiver_recon")){
            oReply = adaptor.getReceiverReconProvider().createNetworkResponse(reply);
            networkByPass = true;
        }

        callback(adaptor, oReply, networkByPass);
    }

}
