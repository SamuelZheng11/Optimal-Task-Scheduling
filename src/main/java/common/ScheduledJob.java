package common;

import java.util.Comparator;

/**
 * A TaskJob wrapper for testing purposes.
 */
public final class ScheduledJob implements Job,  Comparable<ScheduledJob> {
    public final int _startTime;
    public final int _processorNo;
    public final TaskJob _taskJob;
    public final int _finishTime;

    public ScheduledJob(TaskJob job, int startTime, int processor){
        _taskJob = job;
        _startTime = startTime;
        _processorNo = processor;
        _finishTime = _startTime + job.getDuration();
    }

    @Override
    public int getDuration() {
        return _taskJob.getDuration();
    }

    public int compareTo(ScheduledJob a){
        if(this._startTime < a._startTime){
            return -1;
        }else if(a._startTime == this._startTime){
            return 0;
        }else{
            return 1;
        }
    }
}
