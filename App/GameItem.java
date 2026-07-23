public class GameItem {
    private final String id;
    private final String title;
    private final String logoPath;
    private final double price;
    private final String genre;
    private final String description;

    public GameItem(String id, String title, String logoPath, double price, String genre, String description) {
        this.id = id;
        this.title = title;
        this.logoPath = logoPath;
        this.price = price;
        this.genre = genre;
        this.description = description;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLogoPath() { return logoPath; }
    public double getPrice() { return price; }
    public String getGenre() { return genre; }
    public String getDescription() { return description; }

    public String getFormattedPrice() {
        return price == 0.0 ? "FREE" : String.format("$%.2f", price);
    }
}
