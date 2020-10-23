package market.model;

public class Asset {
    private String name;
    private int totalAmount;
    private double price;

    public Asset(String name, int totalAmount, double price) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.price =  price;
    }

    public boolean buyAsset(int quantity){
        this.totalAmount -= quantity;
        if (this.totalAmount <= 0){
            return false;
        }
        return true;
    }

    public boolean sellAsset(int quant, double price){
        double originalPrice = this.price * quant;
        double sellPrice = price * quant;
        if (originalPrice == sellPrice || originalPrice > sellPrice){
            this.totalAmount = this.totalAmount + quant;
            return true;
        }
        return false;
    }

    public double getPrice(){
        return this.price;
    }

    public int getTotalAmount(){
        return this.totalAmount;
    }
}