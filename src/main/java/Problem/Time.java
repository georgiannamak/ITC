package Problem;

import javax.xml.bind.annotation.*;

public class Time  {
    @XmlAttribute
    private String days;
    @XmlAttribute
    private int start;
    @XmlAttribute
    private int length;
    @XmlAttribute
    private String weeks;
    @XmlAttribute
    private  int penalty;
    @XmlTransient
    private int end;

    public Time() {
    }

    public Time(String days, int start, int length, String weeks) {
        this.days = days;
        this.start = start;
        this.length = length;
        this.weeks = weeks;
    }

    @XmlTransient
    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    @XmlTransient
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    @XmlTransient
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @XmlTransient
    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }
    @XmlTransient
    public int getEnd() {
        return start + length;
    }
    @XmlTransient
    public int getPenalty() {
        return penalty;
    }

    public boolean IsDifeerentTime(Time t)
    {
        if(weeks.equals(t.getWeeks()))
        {
            if(days.equals(t.getDays()))
            {
                if(start==t.getStart())
                {
                    return length != t.getLength();
                }
            }
        }
        return true;
    }

}