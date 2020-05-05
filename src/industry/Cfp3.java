package industry;

import java.util.List;
import java.util.Map;

public class Cfp3 {
    private String productId; //7 cyfr, 2 pierwsze to numer maszyny która zleciła wykonanie produktu, 5 kolejnych to reużywalne kolejne numery
    private String productName;
    private Integer requestedActionStage;
    private String requestedActionName;
    private Map<String, List<String>> bookedActions; //zawiera akcje które już zostały zarezerwowane do wykonania
    public Cfp3(String productId, String productName, Integer requestedActionStage, String requestedActionName, Map<String, List<String>> bookedActions){
        this.productId = productId;
        this.productName = productName;
        this.requestedActionName = requestedActionName;
        this.requestedActionStage = requestedActionStage;
        this.bookedActions = bookedActions;
    }
}