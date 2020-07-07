package industry;

import java.util.List;

public class ProduceElement {
    public List<PlanElement> _subProducts;
    public long _productionTimeStart;
    public long _productionTimeEnd;
    public long _lastPlanSubproductTime;
    public boolean _readyToProduce;
    public PlanMessage _planMessage;
    public ProduceElement(List<PlanElement> subProducts, PlanMessage plan, long lastPlanSubproductTime){
        _subProducts = subProducts;
        _planMessage = plan;
        _productionTimeEnd = 0;
        _productionTimeStart = 0;
        _lastPlanSubproductTime = lastPlanSubproductTime;
        if(subProducts.size() == 0)
            _readyToProduce = true;
        else
            _readyToProduce = false;
    }
    public void Acquire(String id){
        boolean readyToProduce = true;
        for(int i = 0; i < _subProducts.size(); i++){
            if(_subProducts.get(i)._messageContent.getId().equals(id)){
                _subProducts.get(i)._obtained = true;
            } else if(_subProducts.get(i)._obtained == false){
                readyToProduce = false;
            }
        }
        _readyToProduce = readyToProduce;
    }
}
