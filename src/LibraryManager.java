
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryManager {
    private List<LibraryItem> items = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private final String FILE_NAME = "library_data.csv";
    private final String USER_FILE = "users_data.csv"; // เพิ่มไฟล์เก็บผู้ใช้


    public LibraryManager() {
        loadDataFromFile();
        loadUsersFromFile();

        // ถ้าอ่านไฟล์แล้วพบว่าระบบว่างเปล่า ให้สร้างข้อมูลมาแทน
        if (items.isEmpty()) {
            seedInitialData();
        }
    }


    private void seedInitialData() {
        System.out.println("[System] กำลังสร้างข้อมูลตั้งต้น (Mock Data) สำหรับการทดสอบ...");
        // ข้อมูลหนังสือ
        items.add(new PhysicalBook("B01", "Java Programming", 3, 0, 0, "John Doe"));
        items.add(new PhysicalBook("B02", "Object-Oriented Design", 2, 0, 0, "Jane Smith"));

        // ข้อมูลสื่อดิจิทัล
        items.add(new DigitalMedia("D01", "Clean Code Video", 5, 0, 0, "MP4"));

        // สร้างสมาชิกร
        users.add(new User("Ajarn"));

        saveDataToFile();
        saveUsersToFile();
    }

    public boolean hasAnyUsers() {
        return !users.isEmpty();
    }
    public boolean hasAnyItems() {
        return !items.isEmpty();
    }

    public void addItem(String id, String title, String author, int copies) {
        if (findItem(id) != null) {
            System.out.println("[ข้อผิดพลาด] รหัสสื่อนี้มีในระบบแล้ว"); return;
        }
        items.add(new PhysicalBook(id, title, copies, 0, 0, author));
        saveDataToFile();
        System.out.println("[สำเร็จ] เพิ่มหนังสือใหม่เรียบร้อย");
    }

    public void addItem(String id, String title, String format, int copies, boolean isDigital) {
        if (findItem(id) != null) {
            System.out.println("[ข้อผิดพลาด] รหัสสื่อนี้มีในระบบแล้ว"); return;
        }

        items.add(new DigitalMedia(id, title, copies, 0, 0, format));
        saveDataToFile();
        System.out.println("[สำเร็จ] เพิ่มสื่อดิจิทัลใหม่เรียบร้อย");
    }

    public void registerUser(String username) {
        if (findUser(username) == null) {
            users.add(new User(username));
            saveUsersToFile(); //บันทึกสมาชิกใหม่ลงไฟล์
            System.out.println("[สำเร็จ] สร้างบัญชี '" + username + "' เรียบร้อย");
        } else {
            System.out.println("[ข้อผิดพลาด] มีชื่อผู้ใช้นี้ในระบบแล้ว");
        }
    }


    public void editUsername(String oldName, String newName) {
        User user = findUser(oldName);
        if (user == null) {
            System.out.println("[ข้อผิดพลาด] ไม่พบชื่อเดิมในระบบ");
            return;
        }

        if (findUser(newName) != null) {
            System.out.println("[ข้อผิดพลาด] ชื่อใหม่ซ้ำกับผู้อื่น");
            return;
        }
        user.setUsername(newName);
        saveUsersToFile(); //บันทึกชื่อใหม่ลงไฟล์
        System.out.println("[สำเร็จ] เปลี่ยนชื่อเรียบร้อย");
    }


    public void searchItem(String keyword) {
        boolean found = false;
        for (LibraryItem item : items) {
            if (item.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                item.displayInfo(); //Polymorphism ใช้เพื่อแสดงหนังสือหรือดิจิทัลแวลาแสดงผล
                found = true;
            }
        }

        if (!found) System.out.println("[แจ้งเตือน] ไม่พบสื่อที่ค้นหา");
    }

    public void deleteItem(String id) {
        LibraryItem item = findItem(id);
        if (item != null && item.borrowedCopies == 0) {
            items.remove(item);
            saveDataToFile();
            System.out.println("[สำเร็จ] ลบสื่อรหัส " + id + " เรียบร้อย");
        } else {
            System.out.println("[ข้อผิดพลาด] ไม่พบรหัส หรือ สื่อนี้กำลังถูกยืมอยู่ (ลบไม่ได้)");
        }
    }


    public void processBorrow(String username, String itemId) {
        User user = findUser(username);
        LibraryItem item = findItem(itemId);

        if (user == null || item == null) {
            System.out.println("[ข้อผิดพลาด] ไม่พบข้อมูลผู้ใช้หรือรหัสสื่อ");
            return;
        }

        if (!user.canBorrowMore()) {
            System.out.println("[ปฏิเสธ] ผู้ใช้ยืมครบโควต้าแล้ว (" + User.MAX_BORROW_LIMIT + " ชิ้น)"); return;
        }

        if (item.borrowItem()) {
            user.addBorrowedItem(item);
            saveDataToFile();  //อัปเดตสต๊อกหนังสือ
            saveUsersToFile(); //อัปเดตประวัติการยืมของผู้ใช้ลงไฟล์
            System.out.println("[สำเร็จ] ยืม '" + item.getTitle() + "' เรียบร้อย");
        }

        else {
            System.out.println("[ข้อผิดพลาด] สื่อรายการนี้ของหมดสต๊อก");
        }
    }


    public void processReturnWithFine(String username, String itemId, int overdueDays) {
        User user = findUser(username);
        LibraryItem item = findItem(itemId);

        if (user != null && item != null && user.getBorrowedItems().contains(item)) {
            double fine = item
                    .calculateFine(overdueDays); //ใช้การ Polymorphism เพื่อให้ระบบจะรู้เองว่าสื่อชิ้นนี้คืออะไร
            item.returnItem();
            user.removeBorrowedItem(item);
            saveDataToFile();
            saveUsersToFile(); //อัปเดตประวัติการยืมของผู้ใช้เมื่อคืนสำเร็จ
            System.out.println("[สำเร็จ] คืน '" + item.getTitle() + "' เรียบร้อย");

            if (fine > 0) {
                System.out.println("[แจ้งเตือน] คุณมีค่าปรับล่าช้า: " + fine + " บาท");
            }
        }else {
            System.out.println("[ข้อผิดพลาด] ข้อมูลการคืนไม่ถูกต้อง หรือไม่ได้ยืมสื่อนี้");
        }
    }


    public void displayAllItems() {
        System.out.println("\n--- รายการสื่อทั้งหมด ---");
        for (LibraryItem item : items) {
            item.displayInfo();//ใช้การ Polymorphism เพื่อให้ระบบจะรู้เองว่าสื่อชิ้นนี้คืออะไร
        }
    }

    public void reportTopBorrowed() {
        System.out.println("\n--- รายงาน: สื่อยอดฮิต (Top Borrowed) ---");
        List<LibraryItem> tempItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            tempItems.add(items.get(i));
        }
        for (int i = 0; i < tempItems.size(); i++) {
            for (int j = i + 1; j < tempItems.size(); j++) {
                if (tempItems.get(i).getBorrowCount() < tempItems.get(j).getBorrowCount()) {
                    LibraryItem temp = tempItems.get(i);
                    tempItems.set(i, tempItems.get(j));
                    tempItems.set(j, temp);
                }
                //.set() เพื่อใช้หาตำแหน่งที่อยู่ของข้อมูลในlist
                //.get() เพื่อแสดงตำแห่ง".............."
            }
        }

        int limit = 5;
        if (tempItems.size() < 5) {
            limit = tempItems.size();
        }

        for (int i = 0; i < limit; i++) {
            LibraryItem item = tempItems.get(i);
            System.out.println("- " + item.getTitle() + " (ยืมสะสม " + item.getBorrowCount() + " ครั้ง)");
        }
    }


    public void reportCurrentlyBorrowed() {
        System.out.println("\n--- รายงาน: รายการที่กำลังถูกยืมค้างอยู่ ---");
        boolean found = false;
        for (User u : users) {
            for (LibraryItem item : u.getBorrowedItems()) {
                System.out.println("- [" + item.getId() + "] " + item.getTitle() + " | ยืมโดย: " + u.getUsername());
                found = true;
            }
        }

        if (!found) System.out.println("ไม่มีรายการค้างยืมในระบบ");
    }



    private User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u; }
        return null;
    }


    private LibraryItem findItem(String id) {
        for (LibraryItem item : items) {
            if (item.getId().equalsIgnoreCase(id)) return item;
        }
        return null;
    }


    private void saveDataToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (LibraryItem item : items) writer.println(item.toCsvFormat());
        } catch (IOException e) { System.out.println("[Error] Save items file failed"); }
    }
    //IOException ใช้เพื่อแจ้งเตือนปัญหาที่ทำให้โปรแกรมไม่สามารรถดำเนินการต่อได้ EX หาไฟล์ไม่เจอ or เน็ตหลุด
    //e.printStackTrace(); ใช้่แสดงรายละเอียดข้อผิดพลาด EX ข้อความอธิยาบ or หมายเลขบรรทัดที่มีปัญหา

    private void loadDataFromFile() {
        File file = new File(FILE_NAME);//ไปดึงข้อมูลมาจากไฟล์ library_data.csv
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length >= 7) {
                    int total = Integer.parseInt(d[3]);
                    int borrowed = Integer.parseInt(d[4]);
                    int count = Integer.parseInt(d[5]);
                    //Integer.parseInt ทำให้ข้อความที่เป็นตัวเลขกลายเป็นจำนวนต็ม

                    if (d[0].equals("Book")) items.add(
                            new PhysicalBook(d[1], d[2], total, borrowed, count, d[6]));
                    else if (
                            d[0].equals("Digital")) items.add(new DigitalMedia(d[1], d[2], total, borrowed, count, d[6]));
                }
            }
        } catch (Exception e) { System.out.println("[Error] Load items file failed"); }
    }

    //จัดการไฟล์ผู้ใช้งาน
    private void saveUsersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (User u : users) writer.println(u.toCsvFormat());
        } catch (IOException e) { System.out.println("[Error] Save users file failed"); }
    }

    private void loadUsersFromFile() {
        File file = new File(USER_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0) {
                    User user = new User(data[0]);

                    // วนลูปอ่านรหัสหนังสือ
                    for (int i = 1; i < data.length; i++) {
                        LibraryItem item = findItem(data[i]);
                        if (item != null) {
                            user.getBorrowedItems().add(item);
                        }
                    }
                    users.add(user);
                }
            }
        } catch (Exception e) {
            System.out.println("[Error] Load users file failed");
        }
    }
}