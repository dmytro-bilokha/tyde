package com.dmytrobilokha.tyde.point.jaxrs;

import java.util.List;

public class LastPointsModel {

    private List<PointModel> points;
    private long earliestPossibleTimestamp;

    public List<PointModel> getPoints() {
        return points;
    }

    public void setPoints(List<PointModel> points) {
        this.points = points;
    }

    public long getEarliestPossibleTimestamp() {
        return earliestPossibleTimestamp;
    }

    public void setEarliestPossibleTimestamp(long earliestPossibleTimestamp) {
        this.earliestPossibleTimestamp = earliestPossibleTimestamp;
    }

}
