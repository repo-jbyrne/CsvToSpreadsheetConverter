package model;

public class NumericCell extends Cell{

    Double value;

    public NumericCell(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return (value);
    }

    @Override
    public String getOutputText(String padding) {
        return padding + value.toString();
    }
}
