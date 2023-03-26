package in.succinct.onet.ondc.extensions;

import com.venky.swf.db.extensions.AfterModelSaveExtension;
import in.succinct.onet.ondc.db.model.BecknOrderMeta;

public class AfterSaveBecknOrderMeta extends AfterModelSaveExtension<BecknOrderMeta> {
    static {
        registerExtension(new AfterSaveBecknOrderMeta());
    }
    @Override
    public void afterSave(BecknOrderMeta model) {
        model.mineTransactionLines();
    }
}
