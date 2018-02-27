public class Stock {
    private StockName name;

    public Stock(StockName name)
    {
        this.name = name;
    }

    public void setName(StockName name) {
        this.name = name;
    }

    public StockName getName() {
        return name;
    }
}
