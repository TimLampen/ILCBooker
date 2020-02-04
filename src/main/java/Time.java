/**
 * Created by Timothy Lampen on 10/28/2019.
 */
public enum Time {


    AFTERNOON("17:30:00","5:30 PM-8:30 PM", "11:00:00", "11:00 AM-2:00 PM"),
    EVENING("20:30:00","8:30 PM-11:30 PM", "14:00:00", "2:00 PM-5:00 PM");

    private String lookupTime;
    private String formattedTime;
    private String weekendLookupTime;
    private String weekendFormattedTime;

    Time(String lookupTime, String formattedTime, String weekendLookupTime, String weekendFormattedTime){
        this.lookupTime = lookupTime;
        this.formattedTime = formattedTime;
        this.weekendLookupTime = weekendLookupTime;
        this.weekendFormattedTime = weekendFormattedTime;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public String getLookupTime() {
        return lookupTime;
    }
}
