import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private List<LibraryItem> borrowedItems;
    public static final int MAX_BORROW_LIMIT = 3;

    public User(String username) {
        this.username = username;
        this.borrowedItems = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public void setUsername(String newUsername) { this.username = newUsername; }
    public List<LibraryItem> getBorrowedItems() { return borrowedItems; }

    public boolean canBorrowMore() { return borrowedItems.size() < MAX_BORROW_LIMIT; }
    public void addBorrowedItem(LibraryItem item) { borrowedItems.add(item); }
    public void removeBorrowedItem(LibraryItem item) { borrowedItems.remove(item); }



    public String toCsvFormat() {
        StringBuilder sb = new StringBuilder(username);
        for (LibraryItem item : borrowedItems) {
            sb.append(",").append(item.getId());
        }
        return sb.toString();
    }
}