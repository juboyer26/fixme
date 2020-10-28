package market.model;

public class Asset {
    public String name;
    public int totalAmount;
    public double price;

    public Asset(String name, int totalAmount, double price) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.price = price;
    }

    public boolean buyAsset(int quantity) {
        this.totalAmount -= quantity;
        if (this.totalAmount <= 0) {
            return false;
        }
        System.out.println("Transaction completed sold asset " + name + " quantity: " + quantity);
        return true;
    }

    public boolean sellAsset(int quant, double price) {
        if (this.price <= price) {
            this.totalAmount += quant;
            System.out.println("Transaction completed buying asset " + name + " quantity: " + quant);
            return true;
        }
        return false;
    }

    public double getPrice() {
        return this.price;
    }

    public int getTotalAmount() {
        return this.totalAmount;
    }
}