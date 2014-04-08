package currencies;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author 119848
 */
@XmlRootElement(name = "conversion")
public class ExchangeRate {

    String from;
    String to;
    Float rate;

    public ExchangeRate() {
    }

    public ExchangeRate(String from, String to, Float rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }
}
