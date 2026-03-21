// Abstract Class เป็นคลาสแม่ที่ไม่สามารถสร้าง Object ได้โดยตรง
public abstract class LibraryItem implements Borrowable {
    protected String id;
    protected String title;
    protected int totalCopies;     // จำนวนทั้งหมดที่มี
    protected int borrowedCopies;  // จำนวนที่ถูกยืมไป
    protected int borrowCount;     // สถิติยอดการยืมรวม

    public LibraryItem(String id, String title, int totalCopies, int borrowedCopies, int borrowCount) {
        this.id = id;
        this.title = title;
        this.totalCopies = totalCopies;
        this.borrowedCopies = borrowedCopies;
        this.borrowCount = borrowCount;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String newTitle) {
        this.title = newTitle; }

    public int getBorrowCount() {
        return borrowCount;
    }
    public int getAvailableCopies() {
        return totalCopies - borrowedCopies;
    }

    // Abstract methods บังคับให้คลาสลูกต้องนำไป Overriding
    public abstract void displayInfo();
    public abstract double calculateFine(int overdueDays);
    public abstract String toCsvFormat();
}