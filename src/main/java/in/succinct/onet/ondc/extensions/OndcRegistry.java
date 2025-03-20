package in.succinct.onet.ondc.extensions;

import com.venky.core.util.ObjectHolder;
import com.venky.core.util.ObjectUtil;
import com.venky.extension.Registry;
import com.venky.swf.controller.Controller;
import com.venky.swf.controller.Controller.CacheOperation;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.db.model.io.json.JSONFormatter;
import com.venky.swf.integration.api.Call;
import com.venky.swf.integration.api.HttpMethod;
import com.venky.swf.integration.api.InputFormat;
import com.venky.swf.path.Path;
import com.venky.swf.path._IPath;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.routing.Config;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.Request;
import in.succinct.beckn.Subscriber;
import in.succinct.beckn.ondc.registry.Context;
import in.succinct.beckn.ondc.registry.Context.Operation;
import in.succinct.beckn.ondc.registry.Entity;
import in.succinct.beckn.ondc.registry.Entity.CityCode;
import in.succinct.beckn.ondc.registry.Entity.Gst;
import in.succinct.beckn.ondc.registry.Entity.KeyPair;
import in.succinct.beckn.ondc.registry.Entity.NetworkParticipant;
import in.succinct.beckn.ondc.registry.Entity.NetworkParticipants;
import in.succinct.beckn.ondc.registry.Entity.Pan;
import in.succinct.beckn.ondc.registry.Message;
import in.succinct.onet.core.adaptor.NetworkAdaptor;
import in.succinct.onet.core.adaptor.NetworkApiAdaptor;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class OndcRegistry extends NetworkAdaptor {

    protected OndcRegistry(String networkName){
        super(networkName);
    }


    private NetworkApiAdaptor adaptor;
    @Override
    public NetworkApiAdaptor getApiAdaptor() {
        if (adaptor != null){
            return adaptor;
        }
        synchronized (this) {
            if (adaptor == null) {
                adaptor = super.getApiAdaptor();
            }
        }
        return adaptor;
    }

    @Override
    public void register(Subscriber subscriber) {
        register(subscriber,ObjectUtil.equals(subscriber.getType(),Subscriber.SUBSCRIBER_TYPE_BAP) ? 1 : 2);
    }
     public void register(Subscriber subscriber, int opsNo) {
        Request request = new Request();
        Context context = new Context();
        context.setOperation(new Operation());
        context.getOperation().setOpsNo(opsNo);
        request.setContext(context);

        Message message = new Message();
        request.setMessage(message);
        message.setRequestId(UUID.randomUUID().toString());
        message.setTimestamp(new Date());
        Entity entity =new Entity();
        message.setEntity(entity);

        Gst gst = new Gst();
        entity.setGst(gst);
        gst.setBusinessAddress(subscriber.getOrganization().getAddress().flatten());
        gst.setCityCode(new CityCode());
        gst.getCityCode().add(subscriber.getOrganization().getAddress().getCity());
        gst.setLegalEntityName(subscriber.getOrganization().getName());
        gst.setGstNo(subscriber.getOrganization().getSalesTaxId());


        Pan pan = new Pan();
        pan.setNameAsPerPan(subscriber.getOrganization().getName());
        pan.setDateOfIncorporation(subscriber.getOrganization().getDateOfIncorporation());
        pan.setPanNo(subscriber.getOrganization().getIncomeTaxId());
        entity.setPan(pan);

        entity.setNameOfAuthorizedSignatory(subscriber.getOrganization().getAuthorizedSignatory().getPerson().getName());
        entity.setAddressOfAuthorizedSignatory(subscriber.getOrganization().getAuthorizedSignatory().getLocation().getAddress().flatten());
        entity.setEmailId(subscriber.getOrganization().getAuthorizedSignatory().getContact().getEmail());
        entity.setMobileNo(subscriber.getOrganization().getAuthorizedSignatory().getContact().getPhone());
        entity.setCountry(subscriber.getCountry());
        entity.setSubscriberId(subscriber.getSubscriberId());
        entity.setUniqueKeyId(subscriber.getUniqueKeyId());
        try {
            URL url = new URL(subscriber.getSubscriberUrl());
            entity.setCallbackUrl(url.getPath());
        }catch (Exception ex){
            //
        }
        entity.setKeyPair( new KeyPair());
        entity.getKeyPair().setEncryptionPublicKey(subscriber.getEncrPublicKey());
        entity.getKeyPair().setSigningPublicKey(subscriber.getSigningPublicKey());
        entity.getKeyPair().setValidFrom(subscriber.getValidFrom());
        entity.getKeyPair().setValidUntil(subscriber.getValidTo());

        NetworkParticipants networkParticipants = new NetworkParticipants();
        message.setNetworkParticipants(networkParticipants);

        for (String domain : subscriber.getDomains()) {
            NetworkParticipant networkParticipant = new NetworkParticipant();
            networkParticipants.add(networkParticipant);
            networkParticipant.setDomain(domain);
            networkParticipant.setSubscriberUrl(entity.getCallbackUrl());
            networkParticipant.setType(subscriber.getType());
            networkParticipant.setCityCode(new CityCode());
            networkParticipant.getCityCode().add(subscriber.getCity());
            networkParticipant.setMsn(subscriber.isMsn());
        }

        String sign = Request.generateSignature(message.getRequestId(),Request.getPrivateKey(subscriber.getSubscriberId(),subscriber.getUniqueKeyId()));

        String html = String.format("" +
                "<html>\n" +
                "\t<head>\n" +
                "\t\t<meta name='ondc-site-verification' content='%s' />\n" +
                "\t</head>\n" +
                "\t<body>\n" +
                "\t\tONDC Site Verification Page\n" +
                "\t</body>\n" +
                "</html>"
                ,sign);



        Path currentPath = Database.getInstance().getContext(_IPath.class.getName());
        Path path = currentPath.constructNewPath(String.format("/resources/%s/%s",Config.instance().getHostName(),"ondc-site-verification.html"));

        BytesView view =new BytesView(path,html.getBytes(StandardCharsets.UTF_8), MimeType.TEXT_HTML);
        ObjectHolder<View> holder = new ObjectHolder<>(view);
        Registry.instance().callExtensions(Controller.SET_CACHED_RESULT_EXTENSION, CacheOperation.SET,path,holder);

        TaskManager.instance().executeAsync(()->{
            InputStream payload = new ByteArrayInputStream(formatter.toString(request.getInner()).getBytes(StandardCharsets.UTF_8));
            Call<InputStream> call = new Call<InputStream>().url(getRegistryUrl(), "subscribe").
                    method(HttpMethod.POST).input(payload).inputFormat(InputFormat.INPUT_STREAM).
                    header("Content-Type", MimeType.APPLICATION_JSON.toString()).
                    header("Accept", MimeType.APPLICATION_JSON.toString());

            JSONObject response = call.getResponseAsJson();
            Config.instance().getLogger(getClass().getName()).info("subscribe" + "-" + request);

        },false);


    }

    @Override
    protected void _subscribe(Subscriber subscriber) {
        register(subscriber,6);
    }

    static final JSONFormatter formatter = new JSONFormatter(2);

    @Override
    public Subscriber getRegistry(){
        Subscriber registry = get(Subscriber.class,"registry");
        return registry;
    }




    @Override
    public String getRegistryId() {
        return getRegistry().getSubscriberId();
    }

    @Override
    public String getRegistryUrl() {
        return getRegistry().getSubscriberUrl();
    }
}
