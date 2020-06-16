package dam.android.raul.u5_t9;

public class wheaterPlace {
    private String description;
    private String tem;
    private String humidity;

    public wheaterPlace(String description, String tem, String humidity) {
        this.description = description;
        this.tem = tem;
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "Temp: " + this.tem + "º\nHumidity: " + this.humidity + "%\nDescription: " + this.description;
    }
}
