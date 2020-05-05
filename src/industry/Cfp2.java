package industry;

import java.util.Date;

public class Cfp2 {
    private Integer amountInQueue;
    private Integer productionTime;
    private Date estimatedProductionEnd;
    private Integer confidenceOfEstimation; //10-full confidence, 1-no confidence
    public Cfp2(Integer amountInQueue, Integer productionTime, Date estimatedProductionEnd, Integer confidenceOfEstimation){
        this.amountInQueue = amountInQueue;
        this.productionTime = productionTime;
        this.estimatedProductionEnd = estimatedProductionEnd;
        this.confidenceOfEstimation = confidenceOfEstimation;
    }

    public Integer getAmountInQueue() {
        return amountInQueue;
    }

    public Integer getProductionTime() {
        return productionTime;
    }

    public Date getEstimatedProductionEnd() {
        return estimatedProductionEnd;
    }

    public Integer getConfidenceOfEstimation() {
        return confidenceOfEstimation;
    }
}
