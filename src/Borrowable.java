// Interface สำหรับกำหนดพฤติกรรมการยืม-คืน
public interface Borrowable {
    boolean borrowItem();
    boolean returnItem();
}