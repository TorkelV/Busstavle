package no.lekrot.busstavle;

import java.time.LocalDateTime;
import java.util.List;

public class BusRide {

    private List<LocalDateTime> departures;
    private String busid;
    private String busText;

    public BusRide(List<LocalDateTime> departures, String busid, String busText){
        this.departures = departures;
        this.busid = busid;
        this.busText = busText;
    }

    public List<LocalDateTime> getDepartures() {
        return departures;
    }

    public void setDepartures(List<LocalDateTime> departures) {
        this.departures = departures;
    }

    public String getBusid() {
        return busid;
    }

    public void setBusid(String busid) {
        this.busid = busid;
    }

    public String getBusText() {
        return busText;
    }

    public void setBusText(String busText) {
        this.busText = busText;
    }
}
