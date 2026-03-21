public class DigitalMedia extends LibraryItem {
    private String format; // เช่น PDF, EPUB

    public DigitalMedia(String id, String title, int totalCopies, int borrowedCopies, int borrowCount, String format) {
        super(id, title, totalCopies, borrowedCopies, borrowCount);
        this.format = format;
    }

    @Override
    public void displayInfo() {
        System.out.println("[ดิจิทัล] รหัส: " + id + " | ชื่อ: " + title + " | รูปแบบ: " + format +
                " | สิทธิ์ทั้งหมด: " + totalCopies + " | ว่างให้โหลด: " + getAvailableCopies());
    }

    @Override
    public double calculateFine(int overdueDays) {
        return 0.0; // สื่อดิจิทัลหมดอายุเอง ไม่มีค่าปรับ
    }

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
        return "Digital," + id + "," + title + "," + totalCopies + "," + borrowedCopies + "," + borrowCount + "," + format;
    }
}