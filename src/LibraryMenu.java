
import java.util.Scanner;

public class LibraryMenu {
    private LibraryManager manager;
    private Scanner scanner;

    public LibraryMenu() {
        this.manager = new LibraryManager();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== 📚 ระบบจัดการห้องสมุด =====");
            System.out.println("1. จัดการบัญชีสมาชิก (ต้องทำเป็นอันดับแรก)");
            System.out.println("2. จัดการสื่อ (เพิ่ม/ลบ/ค้นหา)");
            System.out.println("3. บริการยืม-คืน (Borrow / Return)");
            System.out.println("4. รายงานและสถิติ (Reports)");
            System.out.println("0. ออกจากระบบ");
            System.out.print("เลือกเมนูหลัก: ");

            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) continue;

            try {
                int menu = Integer.parseInt(choice);


                if (menu >= 2 && menu <= 4) {
                    if (!manager.hasAnyUsers()) {
                        System.out.println("\n[ระบบล็อก] ⚠️ กรุณาสมัครสมาชิกที่เมนู 1 ก่อนทำรายการอื่นๆ ครับ");
                        continue;
                    }
                }

                switch (menu) {
                    case 1: handleUserManagement(); break;
                    case 2: handleMediaManagement(); break;
                    case 3: handleBorrowReturn(); break;
                    case 4: handleReports(); break;
                    case 0:
                        running = false;
                        System.out.println("ออกจากระบบเรียบร้อย ขอบคุณครับ!");
                        break;

                    default:
                        System.out.println("[ข้อผิดพลาด] กรุณาเลือกเมนู 0-4");
                }
            }
            catch (NumberFormatException e) {
                System.out.println("[ข้อผิดพลาด] กรุณาป้อนตัวเลขเท่านั้น!");
            }
        }
        scanner.close();//ใช้เพื่อปิด obj scanner ให้คืนค่า
    }

    private void handleUserManagement() {
        System.out.println("\n--- จัดการบัญชีสมาชิก ---");
        System.out.println("1. สมัครสมาชิกใหม่ | 2. เปลี่ยนชื่อผู้ใช้");
        System.out.print("เลือก: ");
        String sub = scanner.nextLine().trim();

        if (sub.equals("1")) {
            System.out.print("ตั้งชื่อผู้ใช้: ");
            String name = scanner.nextLine().trim();

            if(!name.isEmpty()) manager.registerUser(name);

            else System.out.println("[ข้อผิดพลาด] แก้ไขชื่ออีกครั้ง");

        } else if (sub.equals("2")) {
            System.out.print("ชื่อผู้ใช้เดิม: ");
            String oldName = scanner.nextLine().trim();
            System.out.println(); // แก้อาการ Terminal ค้าง รออะไรไม่รู้
            System.out.print("ชื่อผู้ใช้ใหม่: ");
            String newName = scanner.nextLine().trim();
            manager.editUsername(oldName, newName);
        } else {
            System.out.println("[ข้อผิดพลาด] เลือกคำสั่งไม่ถูกต้อง");
        }
    }


    private void handleMediaManagement() {
        System.out.println("\n--- จัดการสื่อ ---");
        System.out.println("1. เพิ่มสื่อใหม่ | 2. ค้นหา | 3. ลบสื่อ");
        System.out.print("เลือก: ");
        String sub = scanner.nextLine();

        if (sub.equals("1")) {
            System.out.print("ประเภท (1=หนังสือ, 2=ดิจิทัล): ");
            String type = scanner.nextLine();
            System.out.print("รหัสสื่อ: ");
            String id = scanner.nextLine();
            System.out.print("ชื่อเรื่อง: ");
            String title = scanner.nextLine();


            if(id.equals("") || title.equals("")) {
                System.out.println("[ข้อผิดพลาด] ข้อมูลห้ามว่าง");
            } else {
                try {
                    System.out.print("จำนวนทั้งหมด: ");
                    String copiesText = scanner.nextLine();
                    int copies = Integer.parseInt(copiesText);

                    if (type.equals("1")) {
                        System.out.print("ผู้แต่ง: ");
                        String author = scanner.nextLine();
                        manager.addItem(id, title, author, copies);

                    } else if (type.equals("2")) {
                        System.out.print("ฟอร์แมตไฟล์: ");
                        String format = scanner.nextLine(); // ประกาศตัวแปรมารับค่าก่อน
                        manager.addItem(id, title, format, copies, true);

                    } else {
                        System.out.println("[ข้อผิดพลาด] ประเภทไม่ถูกต้อง");
                    }
                } catch (Exception e) { // ดัก Exception แบบเหมาเข่ง
                    System.out.println("[ข้อผิดพลาด] จำนวนต้องเป็นตัวเลข");
                }
            }

        } else if (sub.equals("2")) {
            System.out.print("พิมพ์คำค้นหา: ");
            String keyword = scanner.nextLine();
            manager.searchItem(keyword);
        } else if (sub.equals("3")) {
            System.out.print("รหัสที่ต้องการลบ: ");
            String deleteId = scanner.nextLine();
            System.out.println(); // แก้อาการ Terminal ค้าง รออะไรไม่รู้
            manager.deleteItem(deleteId);

        } else {
            System.out.println("[ข้อผิดพลาด] เลือกเมนูไม่ถูกต้องครับ");
        }
    }


    private void handleBorrowReturn() {
        System.out.println("\n--- บริการยืม-คืน ---");
        System.out.println("1. ยืมสื่อ | 2. คืนสื่อ");
        System.out.print("เลือก: ");
        String sub = scanner.nextLine().trim();

        if (sub.equals("1")) {

            if (!manager.hasAnyItems()) {
                System.out.println("[ระบบล็อก] ขณะนี้ไม่มีสื่อในระบบเลย! กรุณาไปเพิ่มข้อมูลที่เมนู 2 ก่อนครับ");
                return;
            }
            System.out.print("ชื่อผู้ใช้ของคุณ: ");
            String bUser = scanner.nextLine().trim();

            System.out.print("รหัสสื่อที่ต้องการยืม: ");
            String bId = scanner.nextLine().trim();
            manager.processBorrow(bUser, bId);

        } else if (sub.equals("2")) {
            System.out.print("ชื่อผู้ใช้ของคุณ: ");
            String rUser = scanner.nextLine().trim();

            System.out.print("รหัสสื่อที่คืน: ");
            String rId = scanner.nextLine().trim();

            try {
                System.out.print("จำนวนวันล่าช้า (0=ไม่ล่าช้า): ");
                int days = Integer.parseInt(scanner.nextLine().trim());
                manager.processReturnWithFine(rUser, rId, days);

            } catch (NumberFormatException e) {
                System.out.println("[ข้อผิดพลาด] กรุณาใส่ตัวเลข");
            }
        }
    }


    private void handleReports() {
        System.out.println("\n--- รายงานและสถิติ ---");
        System.out.println("1. รายการสื่อทั้งหมด | 2. สื่อที่ถูกยืมมากสุด | 3. รายการที่ค้างคืน");
        System.out.print("เลือก: ");
        String sub = scanner.nextLine().trim();
        if (sub.equals("1")){
            manager.displayAllItems();
        }

        else if (sub.equals("2")){
            manager.reportTopBorrowed();
        }
        else if (sub.equals("3")){
            manager.reportCurrentlyBorrowed();
        }
    }
}