package tutorly.model.scheduledclass;

public class ScheduledClass {
    private int id;
    private final String subject;
    private int[] sessionIds;

    public ScheduledClass(int id, String subject, int[] sessionIds) {
        this.id = id;
        this.subject = subject;
        this.sessionIds = sessionIds;
    }
}
