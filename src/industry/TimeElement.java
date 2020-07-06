package industry;

public class TimeElement {
    public long _startTime;
    public long _endTime;
    public int _priority;
    public TimeElement(long startTime, long endTime, int priority){
        _startTime = startTime;
        _endTime = endTime;
        _priority = priority;
    }
}
