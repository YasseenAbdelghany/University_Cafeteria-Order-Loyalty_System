package Services;

import Core.MenuItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to calculate which menu items can be obtained for free with loyalty points.
 * Conversion rate: 1 point = 0.1 EGP discount
 */
public class FreeProductCalculator {
    private static final double EGP_PER_POINT = 0.1; // 1 point = 0.1 EGP
    
    private final MenuManager menuManager;
    
    public FreeProductCalculator(MenuManager menuManager) {
        this.menuManager = menuManager;
    }
    
    /**
     * Get list of menu items that can be fully covered by available points.
     * 
     * @param availablePoints The number of loyalty points available
     * @return List of affordable menu items, sorted by points required (ascending)
     */
    public List<AffordableItem> getAffordableItems(int availablePoints) {
        List<AffordableItem> affordableItems = new ArrayList<>();
        
        try {
            // Get all active menu items
            List<MenuItem> allItems = menuManager.getAvailableItems();
            
            // Calculate which items can be fully covered by points
            for (MenuItem item : allItems) {
                double itemPrice = item.getPrice().getAmount().doubleValue();
                int pointsRequired = (int) Math.ceil(itemPrice / EGP_PER_POINT);
                
                // Only include items that can be fully covered
                if (pointsRequired <= availablePoints) {
                    affordableItems.add(new AffordableItem(item, pointsRequired, itemPrice));
                }
            }
            
            // Sort by points required (ascending)
            affordableItems.sort((a, b) -> Integer.compare(a.pointsRequired, b.pointsRequired));
            
        } catch (Exception e) {
            System.err.println("Error calculating affordable items: " + e.getMessage());
        }
        
        return affordableItems;
    }
    
    /**
     * Check if a specific item can be obtained for free with available points.
     * 
     * @param item The menu item to check
     * @param availablePoints The number of loyalty points available
     * @return true if the item can be fully covered by points
     */
    public boolean canAfford(MenuItem item, int availablePoints) {
        double itemPrice = item.getPrice().getAmount().doubleValue();
        int pointsRequired = (int) Math.ceil(itemPrice / EGP_PER_POINT);
        return pointsRequired <= availablePoints;
    }
    
    /**
     * Calculate how many points are needed to get an item for free.
     * 
     * @param item The menu item
     * @return Points required to fully cover the item price
     */
    public int getPointsRequired(MenuItem item) {
        double itemPrice = item.getPrice().getAmount().doubleValue();
        return (int) Math.ceil(itemPrice / EGP_PER_POINT);
    }
    
    /**
     * Inner class to represent an affordable menu item with its point cost.
     */
    public static class AffordableItem {
        public final MenuItem menuItem;
        public final int pointsRequired;
        public final double priceInEGP;
        
        public AffordableItem(MenuItem menuItem, int pointsRequired, double priceInEGP) {
            this.menuItem = menuItem;
            this.pointsRequired = pointsRequired;
            this.priceInEGP = priceInEGP;
        }
        
        @Override
        public String toString() {
            return String.format("%s - %d points (%.2f EGP)", 
                menuItem.getName(), pointsRequired, priceInEGP);
        }
    }
}
