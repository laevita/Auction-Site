/*
* EE422C Final Project submission by
* <Eva Nance>
* <esn369>
* <17155>
* Spring 2023
*/

package spackage;

public class Product {

	private String name;
    private String description;
	private double currentPrice;
	private double buyNowPrice;
	private boolean sold;

	public Product() {}
	    
	public Product(String name, String description, double currentPrice, double buyNowPrice, boolean sold) {
		this.name = name;
        this.description = description;
        this.currentPrice = currentPrice;
        this.buyNowPrice = buyNowPrice;
        this.sold = sold;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public double getBuyNowPrice() {
        return buyNowPrice;
    }
    
    public void setBuyNowPrice(double buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }
    
    public boolean getSold() {
        return sold;
    }
    
    public void setSold(boolean sold) {
        this.sold = sold;
    }
}

