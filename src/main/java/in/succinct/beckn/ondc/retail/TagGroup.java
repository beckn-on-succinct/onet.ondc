package in.succinct.beckn.ondc.retail;


import in.succinct.beckn.Descriptor;

public class TagGroup extends in.succinct.beckn.TagGroup {
    public TagGroup() {
    }
    
    public TagGroup(String code, Object value) {
        super(code, value);
    }
    
    public TagGroup(String code, String name, Object value) {
        super(code, name, value);
    }
    
    @Override
    protected String getCode(){
        return getDescriptor() == null ? null : getDescriptor().getCode();
    }
    
    @Override
    protected void setCode(String code){
        getDescriptor(true).setCode(code);
    }
    
    @Override
    public String getName(){
        return getDescriptor() == null ? null : getDescriptor().getName();
    }
    
    @Override
    public void setName(String name){
        getDescriptor(true).setName(name);
    }
    
    public Descriptor getDescriptor(){
        return get(Descriptor.class, "descriptor");
    }
    public Descriptor getDescriptor(boolean createIfAbsent){
        return get(Descriptor.class, "descriptor",createIfAbsent);
    }
    
    public void setDescriptor(Descriptor descriptor){
        set("descriptor",descriptor);
    }
    
    
}
