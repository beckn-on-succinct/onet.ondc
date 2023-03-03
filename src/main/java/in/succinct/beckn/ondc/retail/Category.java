package in.succinct.beckn.ondc.retail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Category extends in.succinct.beckn.Category {
    public Category(){
        super();
    }

    public CategoryCode getCategoryCode(){
        return stc.get((String)get("id"));
    }

    Map<CategoryCode,String> cts = new HashMap<>(){{
       put(CategoryCode.FASHION,"Fashion");
       put(CategoryCode.HOME_DECOR,"Home Decor");
       put(CategoryCode.FOOD_AND_BEVERAGES,"Food & Beverages");
       put(CategoryCode.PACKAGED_FOOD,"Packaged Food");
       put(CategoryCode.PACKAGED_GOODS_NON_FOOD,"Packaged Commodities");
    }};
    Map<String,CategoryCode> stc = new HashMap<>(){{
        for (Entry<CategoryCode, String> categoryCodeStringEntry : cts.entrySet()) {
            CategoryCode cc = categoryCodeStringEntry.getKey();
            String s = categoryCodeStringEntry.getValue();
            put(s,cc);
        }
    }};
    public void setCategoryCode(CategoryCode cc){
        set("id",cts.get(cc));
    }


}
