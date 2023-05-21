package in.succinct.beckn.ondc.registry;

import in.succinct.beckn.ondc.registry.Entity.NetworkParticipant;
import in.succinct.beckn.ondc.registry.Entity.NetworkParticipants;

import java.util.Date;

public class    Message extends in.succinct.beckn.Message {
    public Date getTimestamp(){
        return getTimestamp("timestamp");
    }
    public void setTimestamp(Date timestamp){
        set("timestamp",timestamp,TIMESTAMP_FORMAT);
    }

    public String getRequestId(){
        return get("request_id");
    }
    public void setRequestId(String request_id){
        set("request_id",request_id);
    }

    public Entity getEntity(){
        return get(Entity.class, "entity");
    }
    public void setEntity(Entity entity){
        set("entity",entity);
    }


    public NetworkParticipants getNetworkParticipants(){
        return get(NetworkParticipants.class, "network_participant");
    }
    public void setNetworkParticipants(NetworkParticipants network_participants){
        set("network_participant",network_participants);
    }
}
