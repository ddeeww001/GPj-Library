public class PhysicalBook extends LibraryItem {
    private String author;

    public PhysicalBook(String id, String title, int totalCopies, int borrowedCopies, int borrowCount, String author) {
        super(id, title, totalCopies, borrowedCopies, borrowCount);
        this.author = author;
    }

    // Overriding เมธอดจากคลาสแม่และ Interface
    @Override
    public void displayInfo() {
        System.out.println("[หนังสือ] รหัส: " + id + " | ชื่อ: " + title + " | ผู้แต่ง: " + author +
                " | มีทั้งหมด: " + totalCopies + " | คงเหลือ: " + getAvailableCopies());
    }

    @Override
    public double calculateFine(int overdueDays) {
        return overdueDays > 0 ? overdueDays * 5.0 : 0.0; // ปรับวันละ 5 บาท
    }//ใช้คำนวนค่าปรับ

    @Override
    public boolean borrowItem() {
        if (borrowedCopies < totalCopies) {
            borrowedCopies++;
            borrowCount++;
            return true;
        }
        return false;
    }

    @Override
    public boolean returnItem() {
        if (borrowedCopies > 0) {
            borrowedCopies--;
            return true;
        }
        return false;
    }

    @Override
    public String toCsvFormat() {
        return "Book," + id + "," + title + "," + totalCopies + "," + borrowedCopies + "," + borrowCount + "," + author;
    }
}