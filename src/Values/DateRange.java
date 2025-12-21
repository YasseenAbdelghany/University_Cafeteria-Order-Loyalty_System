package Values;

import java.time.LocalDate;

public class DateRange {
    private LocalDate from;
    private LocalDate to;

    public DateRange() {}

    public DateRange(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "DateRange{from=" + from + ", to=" + to + '}';
    }
}
