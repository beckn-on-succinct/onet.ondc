package in.succinct.onet.ondc.extensions;

import com.venky.core.util.ObjectUtil;
import in.succinct.beckn.AdditionalProperties;
import in.succinct.beckn.AdditionalProperties.AdditionalSources;
import in.succinct.beckn.BecknObject;
import in.succinct.beckn.BecknObjectBase.EnumConvertor;
import in.succinct.beckn.BecknObjects;
import in.succinct.beckn.Contact;
import in.succinct.beckn.Context;
import in.succinct.beckn.Descriptor;
import in.succinct.beckn.Faq;
import in.succinct.beckn.Issue;
import in.succinct.beckn.Issue.EscalationLevel;
import in.succinct.beckn.Item;
import in.succinct.beckn.Items;
import in.succinct.beckn.Measure;
import in.succinct.beckn.Message;
import in.succinct.beckn.Note;
import in.succinct.beckn.Note.Action;
import in.succinct.beckn.Note.Notes;
import in.succinct.beckn.Note.RepresentativeAction;
import in.succinct.beckn.Order;
import in.succinct.beckn.Person;
import in.succinct.beckn.Quantity;
import in.succinct.beckn.Representative;
import in.succinct.beckn.Representative.Complainant;
import in.succinct.beckn.Representative.Representatives;
import in.succinct.beckn.Request;
import in.succinct.beckn.Role;
import in.succinct.bpp.core.adaptor.CommerceAdaptor;
import in.succinct.bpp.core.adaptor.NetworkAdaptor;
import in.succinct.bpp.core.adaptor.NetworkAdaptorFactory;
import in.succinct.bpp.core.adaptor.igm.IssueTrackerFactory;
import in.succinct.bpp.core.extensions.SuccinctIssueTracker;
import in.succinct.onet.ondc.extensions.OndcIssueTracker.GRO.GroType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OndcIssueTracker extends SuccinctIssueTracker {

    static {
        IssueTrackerFactory.getInstance().registerAdaptor("ondc", OndcIssueTracker.class);
    }
    public OndcIssueTracker(CommerceAdaptor adaptor) {
        super(adaptor);
    }

    @Override
    public void save(Request oRequest, Request oResponse) {
        Request bRequest = new Request();
        Request bResponse = new Request();
        bResponse.setContext(oResponse.getContext());
        bResponse.setMessage(new Message());

        o2b(oRequest,bRequest);
        super.save(bRequest,bResponse);
        b2o(bResponse,oResponse);
    }

    @Override
    public void status(Request oRequest, Request oResponse) {
        Request bRequest = new Request();
        o2b(oRequest,bRequest);

        Request bResponse = new Request();
        bResponse.setContext(oResponse.getContext());
        bResponse.setMessage(new Message());

        super.status(bRequest,bResponse);
        b2o(bResponse,oResponse);
    }

    public Request createNetworkResponse(Request becknResponse){
        Request oResponse = new Request();

        NetworkAdaptor networkAdaptor = NetworkAdaptorFactory.getInstance().getAdaptor(becknResponse.getContext().getNetworkId());
        networkAdaptor.getApiAdaptor().createReplyContext(getAdaptor().getSubscriber(),becknResponse,oResponse);
        oResponse.getContext().setNetworkId(becknResponse.getContext().getNetworkId());
        b2o(becknResponse,oResponse);
        return oResponse;
    }


    private void o2b(Request source, Request target){
        target.setContext(source.getContext());
        target.setMessage(new Message());
        target.getMessage().setIssue(new Issue());
        if (source.getMessage().getIssue() == null){
            source.getMessage().setIssue(new Issue());
            source.getMessage().getIssue().setId(source.getMessage().get("issue_id"));
        }

        o2bIssue(target.getContext(),source.getMessage().getIssue(),target.getMessage().getIssue());
    }
    private void o2bIssue(Context context, Issue source, Issue target){

        target.setId(source.getId());
        target.setRepresentatives(new Representatives());

        target.setNotes(new Notes());

        Order order = source.get(Order.class,"order_details");
        if (order != null) {
            target.setOrder(order);
            Items items = target.getOrder().getItems();
            for (Item item : items) {
                Number quantity = item.get("quantity");
                item.rm("quantity");
                if (quantity != null) {
                    Quantity q = new Quantity();
                    q.setCount(quantity.intValue());
                    item.setQuantity(q);
                }
            }
        }

        target.setIssueCategory(source.getIssueCategory());
        target.setIssueSubCategory(source.getIssueSubCategory());
        target.setEscalationLevel(source.getEnum(EscalationLevel.class,"issue_type",EscalationLevel.convertor));
        target.setExpectedResponseTime(source.getExpectedResponseTime());
        target.setExpectedResolutionTime(source.getExpectedResolutionTime());
        target.setStatus(source.getStatus());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());

        o2bComplainant(context,source,target);
        o2bOdr(context,source,target);
        o2bRating(context,source,target);
        o2bResolutionProvider(context,source,target);
        o2bResolution(context,source,target);

        Descriptor description = source.get(Descriptor.class,"description");
        if (description != null) {
            Note firstNote = new Note();
            firstNote.setDescription(description);
            firstNote.setId(source.getId()); // First note is for the issue.
            firstNote.setCreatedAt(source.getCreatedAt());
            firstNote.setCreatedBy(target.getComplainant());
            firstNote.setAction(new Action());
            firstNote.getAction().setComplainantAction(RepresentativeAction.OPEN);
            target.getNotes().add(firstNote);
        }
        o2bNotes(context,source,target);

    }
    private void o2bComplainant(Context context,Issue source, Issue target){
        Complainant internalComplainant = new Complainant();
        Organization complainant_info = source.get(Organization.class, "complainant_info");

        IssueSource issueSource = source.get(IssueSource.class,"source");
        if (complainant_info == null || issueSource == null){
            return;
        }

        if (complainant_info.getOrg() == null){
            complainant_info.setOrg(new Org());
            complainant_info.getOrg().setName(issueSource.getNetworkParticipantId());
        }
        internalComplainant.setPerson(complainant_info.getPerson());
        internalComplainant.setContact(complainant_info.getContact());
        internalComplainant.setOrganization(complainant_info.getOrg());
        internalComplainant.setSubscriberId(issueSource.getNetworkParticipantId());
        if (issueSource.getIssueSourceType() == IssueSourceType.CONSUMER) {
            internalComplainant.setRole(Role.COMPLAINANT_PARTY);
            if (ObjectUtil.isVoid(internalComplainant.getSubscriberId())) {
                internalComplainant.setSubscriberId(context.getBapId());
            }else if (!ObjectUtil.equals(internalComplainant.getSubscriberId(),context.getBapId())){
                throw new RuntimeException("IssueSource has to be " + context.getBapId());
            }
        }else if (issueSource.getIssueSourceType() == IssueSourceType.SELLER){
            internalComplainant.setRole(Role.COMPLAINANT_PARTY);
            if (ObjectUtil.isVoid(internalComplainant.getSubscriberId())) {
                internalComplainant.setSubscriberId(context.getBppId());
            }else if (!ObjectUtil.equals(internalComplainant.getSubscriberId(),context.getBppId())){
                throw new RuntimeException("IssueSource has to be " + context.getBppId());
            }
        }else if (issueSource.getIssueSourceType() == IssueSourceType.INTERFACING_NP){
            internalComplainant.setRole(Role.COMPLAINANT_PLATFORM);
        }
        target.setComplainant(internalComplainant);
        target.getRepresentatives().add(internalComplainant);
    }
    private void o2bOdr(Context context,Issue source, Issue target){
        Odr finalizedOdr = source.get(Odr.class,"finalized_odr");

        if (finalizedOdr != null){
            target.setFinalizedOdr(new in.succinct.beckn.Odr());
            target.getFinalizedOdr().update(finalizedOdr);//Organization is an issue.
            target.getFinalizedOdr().setRepresentative(new Representative());
            Representative odrRepresentative  = target.getFinalizedOdr().getRepresentative();
            odrRepresentative.setRole(Role.ODR_ARBITRATOR);
            odrRepresentative.setContact(finalizedOdr.getOrganization().getContact());
            odrRepresentative.setOrganization(finalizedOdr.getOrganization().getOrg());
            odrRepresentative.setPerson(finalizedOdr.getOrganization().getPerson());
            target.getRepresentatives().add(odrRepresentative);
        }

        SelectedOdrsList selectedOdrList = source.get(SelectedOdrsList.class,"selected_odrs_info");
        if (selectedOdrList != null) {
            target.setSelectedOdrsList(new in.succinct.beckn.SelectedOdrs.SelectedOdrsList());
            selectedOdrList.forEach(selectedOdrs -> {
                in.succinct.beckn.SelectedOdrs selectedOdrsInternal = new in.succinct.beckn.SelectedOdrs();
                selectedOdrsInternal.setRepresentative(new Representative());

                selectedOdrsInternal.getRepresentative().setOrganization(selectedOdrs.getOrganization().getOrg());
                selectedOdrsInternal.getRepresentative().setContact(selectedOdrs.getOrganization().getContact());
                selectedOdrsInternal.getRepresentative().setPerson(selectedOdrs.getOrganization().getPerson());
                selectedOdrsInternal.setOdrs(new in.succinct.beckn.Odr.Odrs());

                selectedOdrs.getOdrs().forEach(odr -> {
                    in.succinct.beckn.Odr internalOdr = new in.succinct.beckn.Odr();
                    internalOdr.update(odr);
                    internalOdr.setRepresentative(new Representative());
                    internalOdr.getRepresentative().setOrganization(odr.getOrganization().getOrg());
                    internalOdr.getRepresentative().setContact(odr.getOrganization().getContact());
                    internalOdr.getRepresentative().setPerson(odr.getOrganization().getPerson());
                    selectedOdrsInternal.getOdrs().add(internalOdr);

                });
                target.getSelectedOdrsList().add(selectedOdrsInternal);
            });
        }


    }
    private void o2bRating(Context context,Issue source, Issue target){
        IssueRating rating = source.getEnum(IssueRating.class,"rating",IssueRating.convertor);
        if (rating == IssueRating.THUMBS_DOWN){
            target.setSatisfied(false);
        }else if (rating == IssueRating.THUMBS_UP){
            target.setSatisfied(true);
        }else {
            target.rm("satisfied");
        }
    }
    private void o2bResolutionProvider(Context context, Issue source, Issue target){
        if (target.getComplainant() == null){
            return;
        }

        String respondentSubscriberId = ObjectUtil.equals(context.getBapId(),target.getComplainant().getSubscriberId())? context.getBppId() : context.getBapId();

        ResolutionProvider provider = source.get(ResolutionProvider.class, "resolution_provider");


        if (provider != null){
            RespondentInfo respondentInfo = provider.getRespondentInfo();
            if (respondentInfo.getOrganization().getOrg() == null){
                respondentInfo.getOrganization().setOrg(new Org());
                respondentInfo.getOrganization().getOrg().setName(respondentSubscriberId);
            }

            Representative resolutionProvider = new Representative();
            resolutionProvider.setChatUrl(respondentInfo.getResolutionSupport().getRespondentChatLink());
            //resolutionProvider.setFaqUrl(respondentInfo.getResolutionSupport().getF);
            resolutionProvider.setOrganization(respondentInfo.getOrganization().getOrg());
            resolutionProvider.setContact(respondentInfo.getOrganization().getContact());
            resolutionProvider.setPerson(respondentInfo.getOrganization().getPerson());
            resolutionProvider.getContact().setEmail(respondentInfo.getResolutionSupport().getRespondentEmail());

            if (respondentInfo.getRespondentType() == RespondentType.INTERFACING_NP) {
                resolutionProvider.setSubscriberId(target.getComplainant().getSubscriberId());
                resolutionProvider.setRole(Role.COMPLAINANT_PLATFORM);
            }else if (respondentInfo.getRespondentType() == RespondentType.TRANSACTION_COUNTERPARTY_NP){
                resolutionProvider.setSubscriberId(respondentSubscriberId);
                resolutionProvider.setRole(Role.RESPONDENT_PLATFORM);
            }else if (respondentInfo.getRespondentType() == RespondentType.CASCADED_COUNTERPARTY_NP){
                //We don;t have subscriber of the cascaded party but their org is present.
                resolutionProvider.setSubscriberId(respondentSubscriberId);
                resolutionProvider.setRole(Role.CASCADED_RESPONDENT_PLATFORM);
            }


            resolutionProvider.setAdditionalSources(respondentInfo.getResolutionSupport().getAdditionalSources());
            target.setResolutionProvider(resolutionProvider);
            target.getRepresentatives().add(resolutionProvider);
            respondentInfo.getResolutionSupport().getGROS().forEach(gro->{
                Representative representative = new Representative();
                representative.update(resolutionProvider);
                representative.setPerson(gro.getPerson());
                representative.setContact(gro.getContact());
                String roleName = resolutionProvider.getRole().name();
                roleName = String.format("%s_GRO",roleName.substring(0,roleName.lastIndexOf('_')));
                representative.setRole(Role.valueOf(roleName));
                target.getRepresentatives().add(representative);
            });
        }

    }
    private void o2bResolution(Context context, Issue source, Issue target) {
        Resolution resolution = source.get(Resolution.class,"resolution");
        if (resolution != null){
            if (!ObjectUtil.equals(resolution.getNetworkIssueId(),source.getId())){
                throw new RuntimeException("Invalid Issue Id");
            }
            target.setResolution(new in.succinct.beckn.Resolution());
            target.getResolution().update(resolution);
        }
    }
    private void o2bNotes(Context context, Issue source, Issue target) {
        if (target.getComplainant() == null){
            return;
        }
        String respondentSubscriberId = ObjectUtil.equals(context.getBapId(),target.getComplainant().getSubscriberId())? context.getBppId() : context.getBapId();

        IssueActions issueActions = source.get(IssueActions.class,"issue_action");
        if (issueActions != null){
            ComplainantActions complainantActions = issueActions.getComplainantActions();
            if (complainantActions != null){
                complainantActions.forEach(ca->{
                    Note note = new Note();
                    note.setDescription(new Descriptor());
                    note.setCreatedBy(target.getComplainant());
                    note.setCreatedAt(ca.getUpdatedAt());
                    note.setAction(new Action());
                    note.getAction().setComplainantAction(ca.getAction());
                    note.setDescription(new Descriptor());
                    note.getDescription().setLongDesc(ca.getRemarks());
                    note.setId(BecknObject.TIMESTAMP_FORMAT_WITH_MILLS.format(note.getCreatedAt()));
                    target.getNotes().add(note);
                });
            }
            RespondentActions respondentActions = issueActions.getRespondentActions();
            if (respondentActions != null){
                respondentActions.forEach(respondentAction->{
                    Note note = new Note();
                    note.setDescription(new Descriptor());

                    note.setCreatedBy(new Representative());
                    note.getCreatedBy().setPerson(respondentAction.getUpdatedBy().getPerson());
                    note.getCreatedBy().setContact(respondentAction.getUpdatedBy().getContact());
                    note.getCreatedBy().setOrganization(respondentAction.getUpdatedBy().getOrg());

                    note.getCreatedBy().setSubscriberId(respondentSubscriberId);

                    note.getCreatedBy().setRole(Role.RESPONDENT_PLATFORM);
                    note.setCreatedAt(respondentAction.getUpdatedAt());
                    note.setAction(new Action());
                    note.getAction().setRespondentAction(respondentAction.getAction());
                    note.setDescription(new Descriptor());
                    note.getDescription().setLongDesc(respondentAction.getRemarks());
                    note.setId(BecknObject.TIMESTAMP_FORMAT_WITH_MILLS.format(note.getCreatedAt()));
                    target.getNotes().add(note);
                });
            }

        }

        AdditionalInfosRequired additional_infos_required = source.get(AdditionalInfosRequired.class,"additional_info_required");
        if (additional_infos_required != null){
            Notes notes = target.getNotes();
            additional_infos_required.forEach(additional_info_required -> {
                SupplementaryInformation req = additional_info_required.getInfoRequired();
                SupplementaryInformation reply = additional_info_required.getInfoProvided();
                if (req != null) {
                    Note note = new Note();
                    note.setId(req.getMessageId());
                    note.setCreatedAt(req.getUpdatedAt());
                    note.setCreatedBy(target.getComplainant());
                    note.setAction(new Action());
                    note.getAction().setComplainantAction(RepresentativeAction.INFORMATION_REQUESTED);
                    note.setDescription(req.getIssueUpdateInfo());
                }
                if (reply != null ){
                    Note note = new Note();
                    note.setId(reply.getMessageId());
                    note.setCreatedAt(reply.getUpdatedAt());
                    note.setCreatedBy(new Representative());
                    note.getCreatedBy().setSubscriberId(respondentSubscriberId);
                    note.setAction(new Action());
                    note.getAction().setRespondentAction(RepresentativeAction.INFORMATION_PROVIDED);
                    if(req != null) {
                        note.setParentNoteId(req.getMessageId());
                    }
                    note.setDescription(reply.getIssueUpdateInfo());
                }
            });
        }
    }

    private void b2o(Request source, Request target){
        target.setContext(source.getContext());
        target.setMessage(target.getObjectCreator().create(Message.class));
        target.getMessage().setIssue(target.getObjectCreator().create(Issue.class));

        b2oIssue(target.getContext(),source.getMessage().getIssue(),target.getMessage().getIssue());
    }
    private void b2oIssue(Context context, Issue source, Issue target){

        target.setId(source.getId());
        Representatives representatives = source.getRepresentatives();
        Notes notes = source.getNotes();
        if (notes == null){
            notes = new Notes();
            source.setNotes(notes);
        }
        Note baseNote = notes.get(target.getId());
        if (baseNote != null) {
            target.set("description", baseNote.getDescription());
        }


        Items items = source.getOrder().getItems();
        for (Item item : items){
            Quantity quantity = item.getQuantity();
            item.rm("quantity");
            item.rm("extended_attributes");
            item.set("quantity",quantity.getCount());
        }
        target.set  ("order_details",source.getOrder().getInner());
        JSONObject o = target.get("order_details");
        o.remove("extended_attributes");

        target.setIssueCategory(source.getIssueCategory());
        target.setIssueSubCategory(source.getIssueSubCategory());
        target.setEnum("issue_type",source.getEscalationLevel(),EscalationLevel.convertor);
        target.setExpectedResponseTime(source.getExpectedResponseTime());
        target.setExpectedResolutionTime(source.getExpectedResolutionTime());
        target.setStatus(source.getStatus());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());


        b2oComplainant(context,source,target);
        b2oOdr(context,source,target);
        b2oRating(context,source,target);
        b2oResolutionProvider(context,source,target);
        b2oResolution(context,source,target);
        b2oNotes(context,source,target);
    }
    private void b2oComplainant(Context context,Issue source, Issue target){
        Complainant internalComplainant = source.getComplainant();

        target.set("complainant_info",new Organization());
        Organization complainant_info = target.get(Organization.class, "complainant_info");
        complainant_info.setOrg(new Org());
        complainant_info.getOrg().setInner(internalComplainant.getOrganization().getInner());
        complainant_info.setPerson(internalComplainant.getPerson());
        complainant_info.setContact(internalComplainant.getContact());


        IssueSource issueSource = new IssueSource();
        target.set("source",issueSource);
        issueSource.setNetworkParticipantId(internalComplainant.getSubscriberId());
        if (internalComplainant.getRole() == Role.COMPLAINANT_PARTY){
            if (ObjectUtil.equals(internalComplainant.getSubscriberId(),context.getBapId())){
                issueSource.setIssueSourceType(IssueSourceType.CONSUMER);
            }else {
                issueSource.setIssueSourceType(IssueSourceType.SELLER);
            }
        }else if (internalComplainant.getRole() == Role.COMPLAINANT_PLATFORM){
            issueSource.setIssueSourceType(IssueSourceType.INTERFACING_NP);
        }

    }
    private void b2oOdr(Context context,Issue source, Issue target){
        in.succinct.beckn.Odr finalizedOdr = source.getFinalizedOdr();


        if (finalizedOdr != null){
            Odr outOdr = new Odr();
            target.set("finalized_odr",outOdr);
            outOdr.update(finalizedOdr);
            outOdr.setOrganization(new Organization());
            outOdr.getOrganization().setOrg(new Org());
            outOdr.getOrganization().getOrg().update(finalizedOdr.getRepresentative().getOrganization());
            outOdr.getOrganization().setContact(finalizedOdr.getRepresentative().getContact());
            outOdr.getOrganization().setPerson(finalizedOdr.getRepresentative().getPerson());
        }

        in.succinct.beckn.SelectedOdrs.SelectedOdrsList selectedOdrsList = source.getSelectedOdrsList();


        //SelectedOdrsList selectedOdrList = source.get(SelectedOdrsList.class,"selected_odrs_info");
        if (selectedOdrsList != null) {
            target.set("selected_odrs_info",new SelectedOdrsList());

            selectedOdrsList.forEach(selectedOdrsInternal -> {

                SelectedOdrs selectedOdrs = new SelectedOdrs();
                selectedOdrs.setOrganization(new Organization());
                selectedOdrs.getOrganization().setOrg(new Org());
                selectedOdrs.getOrganization().getOrg().update(selectedOdrsInternal.getRepresentative().getOrganization());
                selectedOdrs.getOrganization().setPerson(selectedOdrsInternal.getRepresentative().getPerson());
                selectedOdrs.getOrganization().setContact(selectedOdrsInternal.getRepresentative().getContact());
                selectedOdrs.setOdrs(new Odrs());

                selectedOdrsInternal.getOdrs().forEach(internalOdr -> {
                    Odr odr = new Odr();
                    odr.update(internalOdr);
                    odr.setOrganization(new Organization());
                    odr.getOrganization().setOrg(new Org());
                    odr.getOrganization().getOrg().update(internalOdr.getRepresentative().getOrganization());
                    odr.getOrganization().setContact(internalOdr.getRepresentative().getContact());
                    odr.getOrganization().setPerson(internalOdr.getRepresentative().getPerson());

                    selectedOdrs.getOdrs().add(odr);

                });
                target.get(SelectedOdrsList.class,"selected_odrs_info").add(selectedOdrs);
            });
        }


    }
    private void b2oRating(Context context,Issue source, Issue target){
        if (source.getBoolean("satisfied") == null){
            target.rm("rating");
        }else if (source.isSatisfied()){
            target.setEnum("rating",IssueRating.THUMBS_UP,IssueRating.convertor);
        }else {
            target.setEnum("rating",IssueRating.THUMBS_DOWN,IssueRating.convertor);
        }
    }
    private void b2oResolutionProvider(Context context, Issue source, Issue target){
        //TODO

        String respondentSubscriberId = ObjectUtil.equals(context.getBapId(),source.getComplainant().getSubscriberId())? context.getBppId() : context.getBapId();

        Representative resolutionProvider =  source.getResolutionProvider();
        if (resolutionProvider != null){

            ResolutionProvider provider = new ResolutionProvider();
            target.set("resolution_provider",provider);
            provider.setRespondentInfo(new RespondentInfo());
            RespondentInfo respondentInfo = provider.getRespondentInfo();

            respondentInfo.setResolutionSupport(new ResolutionSupport());

            respondentInfo.getResolutionSupport().setRespondentChatLink(resolutionProvider.getChatUrl());

            respondentInfo.setOrganization(new Organization());
            respondentInfo.getOrganization().setOrg(new Org());
            respondentInfo.getOrganization().getOrg().update(resolutionProvider.getOrganization());

            //resolutionProvider.setFaqUrl(respondentInfo.getResolutionSupport().getF);
            respondentInfo.getOrganization().setContact(resolutionProvider.getContact());
            respondentInfo.getOrganization().setPerson(resolutionProvider.getPerson());
            respondentInfo.getResolutionSupport().setRespondentEmail(resolutionProvider.getContact().getEmail());
            if (resolutionProvider.getRole() == Role.COMPLAINANT_PLATFORM){
                respondentInfo.setRespondentType(RespondentType.INTERFACING_NP);
            }else if (resolutionProvider.getRole() == Role.RESPONDENT_PLATFORM){
                respondentInfo.setRespondentType(RespondentType.TRANSACTION_COUNTERPARTY_NP);
            }else if (resolutionProvider.getRole() == Role.CASCADED_RESPONDENT_PLATFORM){
                respondentInfo.setRespondentType(RespondentType.CASCADED_COUNTERPARTY_NP);
            }

            respondentInfo.getResolutionSupport().setAdditionalSources(resolutionProvider.getAdditionalSources());
            respondentInfo.getResolutionSupport().setGROS(new GROS());
            source.getRepresentatives().forEach(representative->{
                if (representative.getRole().name().endsWith("_GRO")){
                    GRO gro = new GRO();
                    gro.setContact(representative.getContact());
                    gro.setPerson(representative.getPerson());
                    String roleName = String.format("%s_GRO",respondentInfo.getRespondentType().name());
                    gro.setGroType(GroType.valueOf(roleName));
                }
            });
        }

    }
    private void b2oResolution(Context context, Issue source, Issue target) {
        in.succinct.beckn.Resolution resolution = source.getResolution();
        if (resolution != null){
            Resolution ondcResolution =  new Resolution();
            target.set("resolution", ondcResolution);
            ondcResolution.update(resolution);
            ondcResolution.setNetworkIssueId(source.getId());
        }
    }
    private void b2oNotes(Context context, Issue source, Issue target) {
        String respondentSubscriberId = ObjectUtil.equals(context.getBapId(),source.getComplainant().getSubscriberId())? context.getBppId() : context.getBapId();
        Notes notes = source.getNotes();
        IssueActions issueActions = new IssueActions();
        target.set("issue_action", issueActions);
        issueActions.setComplainantActions(new ComplainantActions());
        issueActions.setRespondentActions(new RespondentActions());
        Map<String,AdditionalInfoRequired> tmap = new HashMap<>();

        for (Note note: notes){
            RepresentativeAction internalComplainantAction = note.getAction().getComplainantAction();
            RepresentativeAction internalRespondentAction = note.getAction().getRespondentAction();
            RepresentativeAction internalNoteAction = null;
            if (internalComplainantAction != null){
                if (internalComplainantAction.isInformation()){
                    internalNoteAction = internalComplainantAction;
                }else {
                    ComplainantAction complainantAction = new ComplainantAction();
                    complainantAction.setAction(internalComplainantAction);
                    complainantAction.setRemarks(note.getDescription().getLongDesc());
                    complainantAction.setUpdatedAt(note.getCreatedAt());
                    complainantAction.setUpdatedBy(new Organization());
                    complainantAction.getUpdatedBy().setOrg(new Org());
                    complainantAction.getUpdatedBy().getOrg().update(source.getComplainant().getOrganization());
                    complainantAction.getUpdatedBy().setContact(source.getComplainant().getContact());
                    complainantAction.getUpdatedBy().setPerson(source.getComplainant().getPerson());
                    issueActions.getComplainantActions().add(complainantAction);
                }
            }else if (internalRespondentAction != null){
                if (internalRespondentAction.isInformation()){
                    internalNoteAction = internalRespondentAction;
                }else {
                    RespondentAction respondentAction = new RespondentAction();
                    respondentAction.setAction(internalRespondentAction);
                    respondentAction.setRemarks(note.getDescription().getLongDesc());
                    respondentAction.setUpdatedAt(note.getCreatedAt());
                    respondentAction.setUpdatedBy(new Organization());
                    respondentAction.getUpdatedBy().setOrg(new Org());
                    respondentAction.getUpdatedBy().getOrg().update(source.getComplainant().getOrganization());
                    respondentAction.getUpdatedBy().setContact(source.getComplainant().getContact());
                    respondentAction.getUpdatedBy().setPerson(source.getComplainant().getPerson());
                    respondentAction.setCascadedLevel(note.getCreatedBy().getRole().getEscalation());
                    issueActions.getRespondentActions().add(respondentAction);
                }
            }

            if (internalNoteAction != null){
                AdditionalInfosRequired additional_infos_required = source.get(AdditionalInfosRequired.class,"additional_info_required");
                if (additional_infos_required == null) {
                    additional_infos_required = new AdditionalInfosRequired();
                    source.set("additional_info_required", additional_infos_required);
                }
                AdditionalInfoRequired additional_info_required = tmap.get(note.getParentNoteId() == null ? note.getId() : note.getParentNoteId());
                if (additional_info_required == null){
                    additional_info_required = new AdditionalInfoRequired();
                    SupplementaryInformation req = new SupplementaryInformation(); additional_info_required.setInfoRequired(req);
                    SupplementaryInformation reply = new SupplementaryInformation(); additional_info_required.setInfoProvided(reply);

                    tmap.put(note.getId(),additional_info_required);
                    additional_infos_required.add(additional_info_required);
                }

                SupplementaryInformation req = additional_info_required.getInfoRequired();
                SupplementaryInformation reply = additional_info_required.getInfoProvided();
                if (internalNoteAction == RepresentativeAction.INFORMATION_REQUESTED){
                    req.setUpdatedAt(note.getCreatedAt());
                    req.setMessageId(note.getId());
                    req.setIssueUpdateInfo(note.getDescription());
                }else {
                    reply.setUpdatedAt(note.getCreatedAt());
                    reply.setMessageId(note.getId());
                    req.setIssueUpdateInfo(note.getDescription());
                }
            }
        }
    }

    public static class AdditionalInfosRequired extends BecknObjects<AdditionalInfoRequired> {
        public AdditionalInfosRequired() {

        }

        public AdditionalInfosRequired(JSONArray value) {
            super(value);
        }

        public AdditionalInfosRequired(String payload) {
            super(payload);
        }

    }
    public static class AdditionalInfoRequired extends BecknObject {

        public SupplementaryInformation getInfoRequired(){
            return get(SupplementaryInformation.class, "info_required");
        }
        public void setInfoRequired(SupplementaryInformation info_required){
            set("info_required",info_required);
        }

        public SupplementaryInformation getInfoProvided(){
            return get(SupplementaryInformation.class, "info_provided");
        }
        public void setInfoProvided(SupplementaryInformation info_provided){
            set("info_provided",info_provided);
        }
    }
    public static class SupplementaryInformation extends BecknObject {
        public Descriptor getIssueUpdateInfo(){
            return get(Descriptor.class, "issue_update_info");
        }
        public void setIssueUpdateInfo(Descriptor issue_update_info){
            set("issue_update_info",issue_update_info);
        }
        
        public Date getUpdatedAt(){
            return getTimestamp("updated_at");
        }
        public void setUpdatedAt(Date updated_at){
            set("updated_at",updated_at,TIMESTAMP_FORMAT);
        }

        public String getMessageId(){
            return get("message_id");
        }
        public void setMessageId(String message_id){
            set("message_id",message_id);
        }
    }
    public static class Resolution extends in.succinct.beckn.Resolution {
        public String getNetworkIssueId(){
            return get("network_issue_id");
        }
        public void setNetworkIssueId(String network_issue_id){
            set("network_issue_id",network_issue_id);
        }

        @Override
        public String getResolutionRemarks() {
            return get("short_desc");
        }

        @Override
        public void setResolutionRemarks(String resolution_remarks) {
            set("short_desc",resolution_remarks);
        }

        @Override
        public ResolutionStatus getResolutionStatus(){
            return getEnum(ResolutionStatus.class, "resolution_action",convertor);
        }
        @Override
        public void setResolutionStatus(ResolutionStatus resolution_action){
            setEnum("resolution_action",resolution_action,convertor);
        }
        @Override
        public ResolutionAction getResolutionAction(){
            return getEnum(ResolutionAction.class, "action_triggered");
        }
        @Override
        public void setResolutionAction(ResolutionAction resolution_action){
            setEnum("action_triggered",resolution_action);
        }


        public static EnumConvertor<ResolutionStatus> convertor = new EnumConvertor<>(ResolutionStatus.class) {
            Map<ResolutionStatus,String> map = new HashMap<>(){{
               put(ResolutionStatus.RESOLVED,"RESOLVE");
               put(ResolutionStatus.REJECTED,"REJECT");
            }};
            Map<String,ResolutionStatus> rmap = new HashMap<>(){{
               map.forEach((k,v)-> put(v,k));
            }};

            @Override
            public String toString(ResolutionStatus value) {
                return map.get(value);
            }

            @Override
            public ResolutionStatus valueOf(String enumRepresentation) {
                return rmap.get(enumRepresentation);
            }
        };

    }
    public static class ResolutionProvider extends BecknObject {
        public RespondentInfo getRespondentInfo(){
            return get(RespondentInfo.class, "respondent_info");
        }
        public void setRespondentInfo(RespondentInfo respondent_info){
            set("respondent_info",respondent_info);
        }

    }
    public static class RespondentInfo extends BecknObject {
        public RespondentType getRespondentType(){
            return getEnum(RespondentType.class, "type",RespondentType.convertor);
        }
        public void setRespondentType(RespondentType type){
            setEnum("type",type,RespondentType.convertor);
        }
        public String getPosId(){
            return get("pos_id");
        }
        public void setPosId(String pos_id){
            set("pos_id",pos_id);
        }
        public Organization getOrganization(){
            return get(Organization.class, "organization");
        }
        public void setOrganization(Organization organization){
            set("organization",organization);
        }
        public ResolutionSupport getResolutionSupport(){
            return get(ResolutionSupport.class, "resolution_support");
        }
        public void setResolutionSupport(ResolutionSupport resolution_support){
            set("resolution_support",resolution_support);
        }

    }
    public static class ResolutionSupport extends BecknObject {
        public String getRespondentChatLink(){
            return get("respondent_chat_link");
        }
        public void setRespondentChatLink(String respondent_chat_link){
            set("respondent_chat_link",respondent_chat_link);
        }
        public String getRespondentEmail(){
            return get("respondent_email");
        }
        public void setRespondentEmail(String respondent_email){
            set("respondent_email",respondent_email);
        }
        public RespondentFaqs getRespondentFaqs(){
            return get(RespondentFaqs.class, "respondent_faqs");
        }
        public void setRespondentFaqs(RespondentFaqs respondent_faqs){
            set("respondent_faqs",respondent_faqs);
        }
        public AdditionalSources getAdditionalSources(){
            return get(AdditionalSources.class, "additional_sources");
        }
        public void setAdditionalSources(AdditionalSources additional_sources){
            set("additional_sources",additional_sources);
        }
        public GROS getGROS(){
            return get(GROS.class, "gros");
        }
        public void setGROS(GROS gros){
            set("gros",gros);
        }

    }
    public static class RespondentFaqs extends AdditionalProperties<Faq> {

    }
    public static class GROS extends BecknObjects<GRO> {

    }
    public static class GRO extends BecknObject {
        public Person getPerson(){
            return get(Person.class, "person");
        }
        public void setPerson(Person person){
            set("person",person);
        }
        public Contact getContact(){
            return get(Contact.class, "contact");
        }
        public void setContact(Contact contact){
            set("contact",contact);
        }
        public GroType getGroType(){
            return getEnum(GroType.class, "gro_type",GroType.convertor);
        }
        public void setGroType(GroType gro_type){
            setEnum("gro_type",gro_type,GroType.convertor);
        }

        public enum GroType {
            INTERFACING_NP_GRO,
            TRANSACTION_COUNTERPARTY_NP_GRO,
            CASCADED_COUNTERPARTY_NP_GRO;

            public static EnumConvertor<GroType> convertor = new EnumConvertor<>(GroType.class);
        }
    }
    public enum RespondentType {
        INTERFACING_NP,
        TRANSACTION_COUNTERPARTY_NP,
        CASCADED_COUNTERPARTY_NP;

        public static EnumConvertor<RespondentType> convertor = new EnumConvertor<>(RespondentType.class);
    }
    public enum IssueRating {
        THUMBS_UP,THUMBS_DOWN;
        public static EnumConvertor<IssueRating> convertor = new EnumConvertor<>(IssueRating.class);
    }
    public static class SelectedOdrsList extends BecknObjects<SelectedOdrs>{
        public SelectedOdrsList() {
        }

        public SelectedOdrsList(JSONArray value) {
            super(value);
        }

        public SelectedOdrsList(String payload) {
            super(payload);
        }

    }
    public static class SelectedOdrs extends BecknObject{
        public Organization getOrganization(){
            return get(Organization.class, "respondent_info");
        }
        public void setOrganization(Organization organization){
            set("respondent_info",organization);
        }

        public Odrs getOdrs(){
            return get(Odrs.class, "odrs");
        }
        public void setOdrs(Odrs odrs){
            set("odrs",odrs);
        }


    }
    public static class Odrs extends BecknObjects<Odr>{
        public Odrs() {
        }

        public Odrs(JSONArray value) {
            super(value);
        }

        public Odrs(String payload) {
            super(payload);
        }
    }
    public static class Odr extends in.succinct.beckn.Odr {
        public Odr() {
        }

        public Odr(String payload) {
            super(payload);
        }

        public Odr(JSONObject object) {
            super(object);
        }


        public Organization getOrganization(){
            return get(Organization.class, "organization");
        }
        public void setOrganization(Organization organization){
            set("organization",organization);
        }


    }
    public static class Organization extends BecknObject {
        public Org getOrg(){
            return get(Org.class, "org");
        }
        public void setOrg(Org org){
            set("org",org);
        }

        public Contact getContact(){
            return get(Contact.class, "contact");
        }
        public void setContact(Contact contact){
            set("contact",contact);
        }
        public Person getPerson(){
            return get(Person.class, "person");
        }
        public void setPerson(Person person){
            set("person",person);
        }
    }
    public static class Org extends in.succinct.beckn.Organization {
        @Override
        public boolean isExtendedAttributesDisplayed() {
            return false;
        }

        public Org() {
        }
    }
    public static class IssueActions extends BecknObject{
        public IssueActions() {
        }

        public IssueActions(String payload) {
            super(payload);
        }

        public IssueActions(JSONObject object) {
            super(object);
        }

        public ComplainantActions getComplainantActions(){
            return get(ComplainantActions.class, "complainant_actions");
        }
        public void setComplainantActions(ComplainantActions complainant_actions){
            set("complainant_actions",complainant_actions);
        }

        public RespondentActions getRespondentActions(){
            return get(RespondentActions.class, "respondent_actions");
        }
        public void setRespondentActions(RespondentActions respondent_actions){
            set("respondent_actions",respondent_actions);
        }


    }
    public static class ComplainantActions extends BecknObjects<ComplainantAction> {
        public ComplainantActions() {
        }

        public ComplainantActions(JSONArray value) {
            super(value);
        }

        public ComplainantActions(String payload) {
            super(payload);
        }
    }
    public static class ComplainantAction extends BecknObject {
        public String getRemarks(){
            return get("short_desc");
        }
        public void setRemarks(String remarks){
            set("short_desc",remarks);
        }
        public RepresentativeAction getAction(){
            return  getEnum(RepresentativeAction.class, "complainant_action");
        }
        public void setAction(RepresentativeAction complainant_action){
            setEnum("complainant_action",complainant_action);
        }

        public Date getUpdatedAt(){
            return getTimestamp("updated_at");
        }
        public void setUpdatedAt(Date updated_at){
            set("updated_at",updated_at,TIMESTAMP_FORMAT);
        }

        public Organization getUpdatedBy(){
            return get(Organization.class, "updated_by");
        }
        public void setUpdatedBy(Organization updated_by){
            set("updated_by",updated_by);
        }


    }
    public static class RespondentActions extends BecknObjects<RespondentAction> {
        public RespondentActions() {
        }

        public RespondentActions(JSONArray value) {
            super(value);
        }

        public RespondentActions(String payload) {
            super(payload);
        }
    }
    public static class RespondentAction extends BecknObject {
        public RespondentAction() {
        }

        public RespondentAction(String payload) {
            super(payload);
        }

        public RespondentAction(JSONObject object) {
            super(object);
        }

        public RepresentativeAction getAction(){
            return getEnum(RepresentativeAction.class, "respondent_action");
        }
        public void setAction(RepresentativeAction respondent_action){
            setEnum("respondent_action",respondent_action);
        }

        public String getRemarks(){
            return get("remarks");
        }
        public void setRemarks(String remarks){
            set("remarks",remarks);
        }


        public Date getUpdatedAt(){
            return getTimestamp("updated_at");
        }
        public void setUpdatedAt(Date updated_at){
            set("updated_at",updated_at,TIMESTAMP_FORMAT);
        }

        public Organization getUpdatedBy(){
            return get(Organization.class, "updated_by");
        }
        public void setUpdatedBy(Organization updated_by){
            set("updated_by",updated_by);
        }
        
        public int getCascadedLevel(){
            return getInteger("cascaded_level");
        }
        public void setCascadedLevel(int cascaded_level){
            set("cascaded_level",cascaded_level);
        }


        public enum Action {
            PROCESSING,
            CASCADED,
            RESOLVED,
            NEED_MORE_INFO;

            EnumConvertor<Action> convertor = new EnumConvertor<>(Action.class);
        }
    }
    public static class IssueSource extends BecknObject {
        public String getNetworkParticipantId(){
            return get("network_participant_id");
        }
        public void setNetworkParticipantId(String network_participant_id){
            set("network_participant_id",network_participant_id);
        }

        public IssueSourceType getIssueSourceType(){
            return getEnum(IssueSourceType.class, "type", IssueSourceType.convertor);
        }
        public void setIssueSourceType(IssueSourceType issue_source_type){
            setEnum("type",issue_source_type,IssueSourceType.convertor);
        }


    }
    public enum IssueSourceType {
        CONSUMER,
        SELLER,
        INTERFACING_NP;


        public static EnumConvertor<IssueSourceType> convertor = new EnumConvertor<>(IssueSourceType.class);
    }
}
