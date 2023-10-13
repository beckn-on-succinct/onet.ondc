package in.succinct.beckn.ondc.retail;

import in.succinct.beckn.TagGroups;

import java.util.Date;

public class Intent extends in.succinct.beckn.Intent {

    @Override
    public boolean isExtendedAttributesDisplayed() {
        return false;
    }



    public boolean isIncrementalRequest(){
        TagGroups list = getTags();
        return list != null && list.get("catalog_inc") != null;
    }

    @Override
    public boolean isIncrementalRequestStartTrigger(){
        return "start".equals(getTag("catalog_inc","mode"));
    }
    @Override
    public void setIncrementalRequestStartTrigger(boolean incremental_request_start_trigger){
        setTag("catalog_inc","mode",incremental_request_start_trigger ? "start" : null);
    }

    @Override
    public boolean isIncrementalRequestEndTrigger(){
        return "end".equals(getTag("catalog_inc","mode"));
    }
    @Override
    public void setIncrementalRequestEndTrigger(boolean incremental_request_end_trigger){
        setTag("catalog_inc","mode",incremental_request_end_trigger ? "end" : null);
    }

    public Date getStartTime(){
        return parseTimestamp(getTag("catalog_inc","start_time"));
    }
    public void setStartTime(Date start_time){
        setTag("catalog_inc","start_time",formatDateTime(start_time,TIMESTAMP_FORMAT));
    }

    public Date getEndTime(){
        return parseTimestamp(getTag("catalog_inc","end_time"));
    }
    public void setEndTime(Date end_time){
        setTag("catalog_inc","end_time",formatDateTime(end_time,TIMESTAMP_FORMAT));
    }
}
