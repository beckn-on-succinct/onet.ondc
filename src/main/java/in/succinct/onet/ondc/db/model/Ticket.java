package in.succinct.onet.ondc.db.model;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.model.Model;

public interface Ticket extends Model {
    public String getNetworkOrderId();
    public void setNetworkOrderId(String orderId);

    public String getTicketId();
    public void setTicketId(String ticketId);

    public String getOrderId();
    public void setOrderId(String sellerNPOrderId);

    @Index
    public String getNameOfBuyerNP();
    public void setNameOfBuyerNP(String nameOfBuyerNP);

    @Index
    public String getNameOfSellerNP();
    public void setNameOfSellerNP(String nameOfSellerNP);

    @COLUMN_DEF(value = StandardDefault.SOME_VALUE,args = "Off-network")
    public String getNameOfLogisticsNP();
    public void setNameOfLogisticsNP(String nameOfLogisticsNP);

    public String getTicketCreationDate();
    public void setTicketCreationDate(String ticketCreationDate);

    public String getTicketCreationTime();
    public void setTicketCreationTime(String ticketCreationTime);

    public String getIssueCategory();
    public void setIssueCategory(String issueCategory);

    public String getOrderCategory();
    public void setOrderCategory(String orderCategory);

    public String getTicketStatus();
    public void setTicketStatus(String ticketStatus);

    public String getTicketClosureDate();
    public void setTicketClosureDate(String ticketClosureDate);

    public String getTicketClosureTime();
    public void setTicketClosureTime(String ticketClosureTime);
}
