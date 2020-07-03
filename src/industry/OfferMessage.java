package industry;

import java.util.Date;

public class OfferMessage {
    private long _productionEnd;
    public OfferMessage(long productionEnd){
        _productionEnd = productionEnd;
    }
    public long getProductionEnd() {
        return _productionEnd;
    }
}
