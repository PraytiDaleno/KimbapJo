package com.example.kimbapjo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "kimbapjo.db";
    public static final int DB_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fullname TEXT," +
                "phone TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "role TEXT DEFAULT 'customer')");

        ContentValues admin = new ContentValues();
        admin.put("fullname", "Administrator");
        admin.put("phone", "0000");
        admin.put("email", "admin@gmail.com");
        admin.put("password", "admin123");
        admin.put("role", "admin");
        db.insert("users", null, admin);

        db.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_name TEXT," +
                "product_price INTEGER," +
                "product_image INTEGER," +
                "quantity INTEGER)");

        db.execSQL("CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id TEXT," +
                "product_name TEXT," +
                "product_price INTEGER," +
                "product_image INTEGER," +
                "quantity INTEGER," +
                "payment_method TEXT," +
                "checkout_type TEXT," +
                "total_amount INTEGER," +
                "created_at TEXT)");

        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_code TEXT UNIQUE," +
                "customer_name TEXT," +
                "items TEXT," +
                "total_amount INTEGER," +
                "payment_method TEXT," +
                "status TEXT DEFAULT 'Dibuat'," +
                "created_at INTEGER," +
                "paid_at INTEGER," +
                "process_at INTEGER," +
                "done_at INTEGER," +
                "estimasi_menit INTEGER DEFAULT 15)");

        db.execSQL("CREATE TABLE order_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_code TEXT," +
                "status TEXT," +
                "created_at INTEGER)");

        db.execSQL("CREATE TABLE menu_stock (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "menu_name TEXT UNIQUE," +
                "category TEXT," +
                "stock INTEGER," +
                "is_available INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE promo_used (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "promo_name TEXT," +
                "used_at TEXT)");

        db.execSQL("CREATE TABLE app_settings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "setting_key TEXT UNIQUE," +
                "setting_value TEXT)");

        insertDefaultStock(db);
    }

    private void insertDefaultStock(SQLiteDatabase db) {
        insertStock(db, "🍗 Kimbab Ayam Katsu", "Kimbap", 10);
        insertStock(db, "🐟 Kimbab Tuna Spicy", "Kimbap", 10);
        insertStock(db, "🥩 Kimbab Beef Bulgogi", "Kimbap", 10);
        insertStock(db, "🍚 Nasi Ayam Katsu Sambal Bawang", "Rice Bowl", 10);
        insertStock(db, "🧈 Nasi Ayam Katsu Mentega", "Rice Bowl", 10);
        insertStock(db, "🍚 Nasi Ayam Katsu Polos", "Rice Bowl", 10);
        insertStock(db, "🍳 Nasi Ayam Teriyaki + Telur", "Rice Bowl", 10);
        insertStock(db, "🍳 Nasi Telur Dadar", "Rice Bowl", 10);
        insertStock(db, "🧀 Kimbab Goreng Keju", "Kimbap", 10);
        insertStock(db, "🍗 Kimbab Goreng Ayam", "Kimbap", 10);
        insertStock(db, "🥬 Kimbab Sayur", "Kimbap", 10);
        insertStock(db, "🍽️ Side Dish", "Side Dish", 10);
        insertStock(db, "🍧 Es Serut", "Es Serut", 10);
        insertStock(db, "🐟 Kimbab Goreng Tuna Spicy", "Kimbap", 10);
        insertStock(db, "🦐 Kimbab Seafood", "Kimbap", 10);
        insertStock(db, "🥩 Kimbab Bulgogi", "Kimbap", 10);
    }

    private void insertStock(SQLiteDatabase db, String name, String category, int stock) {
        ContentValues cv = new ContentValues();
        cv.put("menu_name", name);
        cv.put("category", category);
        cv.put("stock", stock);
        cv.put("is_available", stock > 0 ? 1 : 0);
        db.insertWithOnConflict("menu_stock", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE transactions ADD COLUMN order_id TEXT");
            } catch (Exception ignored) {}
        }

        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE users ADD COLUMN role TEXT DEFAULT 'customer'");
            } catch (Exception ignored) {}

            try {
                ContentValues admin = new ContentValues();
                admin.put("fullname", "Administrator");
                admin.put("phone", "0000");
                admin.put("email", "admin@gmail.com");
                admin.put("password", "admin123");
                admin.put("role", "admin");

                db.insertWithOnConflict(
                        "users",
                        null,
                        admin,
                        SQLiteDatabase.CONFLICT_IGNORE
                );
            } catch (Exception ignored) {}
        }

        if (oldVersion < 5) {
            db.execSQL("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "order_code TEXT UNIQUE," +
                    "customer_name TEXT," +
                    "items TEXT," +
                    "total_amount INTEGER," +
                    "payment_method TEXT," +
                    "status TEXT DEFAULT 'Dibuat'," +
                    "created_at INTEGER," +
                    "paid_at INTEGER," +
                    "process_at INTEGER," +
                    "done_at INTEGER," +
                    "estimasi_menit INTEGER DEFAULT 15)");

            db.execSQL("CREATE TABLE IF NOT EXISTS order_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "order_code TEXT," +
                    "status TEXT," +
                    "created_at INTEGER)");

            db.execSQL("CREATE TABLE IF NOT EXISTS menu_stock (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "menu_name TEXT UNIQUE," +
                    "category TEXT," +
                    "stock INTEGER," +
                    "is_available INTEGER DEFAULT 1)");

            insertDefaultStock(db);
        }
    }

    // ================= USER =================

    public boolean registerUser(String fullname, String phone, String email, String password) {
        return registerUser(fullname, phone, email, password, "customer");
    }

    public boolean registerUser(String fullname, String phone, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("fullname", fullname);
        cv.put("phone", phone);
        cv.put("email", email);
        cv.put("password", password);
        cv.put("role", role);

        long result = db.insert("users", null, cv);
        return result != -1;
    }

    public boolean checkLogin(String emailOrPhone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE (email=? OR phone=?) AND password=?",
                new String[]{emailOrPhone, emailOrPhone, password}
        );

        boolean result = cursor.getCount() > 0;
        cursor.close();

        return result;
    }

    public String getUserRole(String emailOrPhone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT role FROM users WHERE (email=? OR phone=?) AND password=? LIMIT 1",
                new String[]{emailOrPhone, emailOrPhone, password}
        );

        String role = "";

        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }

        cursor.close();
        return role;
    }

    public Cursor getUserByCredentials(String emailOrPhone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM users WHERE (email=? OR phone=?) AND password=? LIMIT 1",
                new String[]{emailOrPhone, emailOrPhone, password}
        );
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM users WHERE id=? LIMIT 1",
                new String[]{String.valueOf(userId)}
        );
    }

    public String getUserNameById(int userId) {
        Cursor cursor = getUserById(userId);
        String name = "Customer";
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
        }
        cursor.close();
        return name;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users ORDER BY id DESC", null);
    }

    public Cursor getFirstUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users ORDER BY id DESC LIMIT 1", null);
    }

    public String getLatestCustomerName() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT fullname FROM users WHERE role='customer' ORDER BY id DESC LIMIT 1",
                null
        );

        String customerName = "Customer";
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            customerName = cursor.getString(0);
        }
        cursor.close();
        return customerName;
    }

    public boolean updateUser(int id, String fullname, String phone, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("fullname", fullname);
        cv.put("phone", phone);
        cv.put("email", email);
        cv.put("password", password);

        int result = db.update(
                "users",
                cv,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "users",
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // ================= CART =================

    public boolean addCart(String name, int price, int image, int quantity) {

        if (!isMenuAvailable(name)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        if (isProductInCart(name)) {
            return increaseProductQuantity(name, quantity);
        }

        ContentValues cv = new ContentValues();
        cv.put("product_name", name);
        cv.put("product_price", price);
        cv.put("product_image", image);
        cv.put("quantity", quantity);

        long result = db.insert("cart", null, cv);
        return result != -1;
    }

    public boolean isProductInCart(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM cart WHERE product_name=? LIMIT 1",
                new String[]{productName}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }

    public boolean increaseProductQuantity(String productName, int addQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(
                "UPDATE cart SET quantity = quantity + ? WHERE product_name=?",
                new Object[]{addQuantity, productName}
        );

        return true;
    }

    public Cursor getCart() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM cart", null);
    }

    public boolean updateCartQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("quantity", quantity);

        int result = db.update(
                "cart",
                cv,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
    public boolean deleteCartItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "cart",
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    public int getCartCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM cart", null);

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", null, null);
    }

    // ================= STOCK =================

    public boolean insertOrUpdateStock(String menuName, String category, int stock) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("menu_name", menuName);
        cv.put("category", category);
        cv.put("stock", stock);
        cv.put("is_available", stock > 0 ? 1 : 0);

        long result = db.insertWithOnConflict(
                "menu_stock",
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        return result != -1;
    }

    public int getMenuStock(String menuName) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT stock FROM menu_stock WHERE menu_name=? LIMIT 1",
                new String[]{menuName}
        );

        int stock = 0;

        if (cursor.moveToFirst()) {
            stock = cursor.getInt(0);
        }

        cursor.close();
        return stock;
    }

    public boolean isMenuAvailable(String menuName) {
        return getMenuStock(menuName) > 0;
    }

    public boolean reduceStock(String menuName, int quantity) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(
                "UPDATE menu_stock SET stock = stock - ? WHERE menu_name=?",
                new Object[]{quantity, menuName}
        );

        return true;
    }

    public Cursor getAllStock() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM menu_stock ORDER BY category ASC",
                null
        );
    }
    // ================= TRANSAKSI =================

    // ================= TRANSAKSI =================

    public String generateOrderId() {
        return "ORD-" + System.currentTimeMillis();
    }

    public boolean addTransaction(String name, int price, int image, int quantity,
                                  String paymentMethod, String checkoutType, int totalAmount) {
        String orderId = generateOrderId();

        return addTransaction(
                orderId,
                name,
                price,
                image,
                quantity,
                paymentMethod,
                checkoutType,
                totalAmount
        );
    }

    public boolean addTransaction(String orderId, String name, int price, int image, int quantity,
                                  String paymentMethod, String checkoutType, int totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("order_id", orderId);
        cv.put("product_name", name);
        cv.put("product_price", price);
        cv.put("product_image", image);
        cv.put("quantity", quantity);
        cv.put("payment_method", paymentMethod);
        cv.put("checkout_type", checkoutType);
        cv.put("total_amount", totalAmount);
        cv.put("created_at", String.valueOf(System.currentTimeMillis()));

        long result = db.insert("transactions", null, cv);
        return result != -1;
    }

    public Cursor getTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM transactions ORDER BY id DESC", null);
    }

    public Cursor getLastTransaction() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM transactions ORDER BY id DESC LIMIT 1", null);
    }

    public int getTransactionCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM transactions", null);
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
    // ================= ORDERS =================

    public String generateOrderCode() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM orders",
                null
        );

        int count = 1;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0) + 1;
        }

        cursor.close();

        return String.format("KBP%03d", count);
    }

    public boolean createOrder(
            String customerName,
            String items,
            int totalAmount,
            String paymentMethod,
            int estimasiMenit
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        String orderCode = generateOrderCode();

        long now = System.currentTimeMillis();

        ContentValues cv = new ContentValues();

        cv.put("order_code", orderCode);
        cv.put("customer_name", customerName);
        cv.put("items", items);
        cv.put("total_amount", totalAmount);
        cv.put("payment_method", paymentMethod);
        cv.put("status", "Dibuat");
        cv.put("created_at", now);
        cv.put("paid_at", now);
        cv.put("estimasi_menit", estimasiMenit);

        long result = db.insert("orders", null, cv);

        if (result != -1) {
            addOrderHistory(orderCode, "Pesanan Dibuat");
        }

        return result != -1;
    }

    public boolean createOrderFromTransactions(
            String orderCode,
            String customerName,
            String paymentMethod,
            int totalAmount,
            int estimasiMenit
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor existing = db.rawQuery(
                "SELECT id FROM orders WHERE order_code=? LIMIT 1",
                new String[]{orderCode}
        );
        boolean alreadyExists = existing.moveToFirst();
        existing.close();
        if (alreadyExists) return true;

        Cursor cursor = db.rawQuery(
                "SELECT product_name, quantity FROM transactions WHERE order_id=? ORDER BY id ASC",
                new String[]{orderCode}
        );

        StringBuilder items = new StringBuilder();
        while (cursor.moveToNext()) {
            if (items.length() > 0) items.append(" . ");
            items.append(cursor.getInt(1))
                    .append(" x ")
                    .append(cursor.getString(0));
        }
        cursor.close();

        if (items.length() == 0) return false;

        long now = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        cv.put("order_code", orderCode);
        cv.put("customer_name", customerName);
        cv.put("items", items.toString());
        cv.put("total_amount", totalAmount);
        cv.put("payment_method", paymentMethod);
        cv.put("status", "Dibuat");
        cv.put("created_at", now);
        cv.put("paid_at", now);
        cv.put("estimasi_menit", estimasiMenit);

        long result = db.insert("orders", null, cv);
        if (result != -1) {
            addOrderHistory(orderCode, "Pesanan Dibuat");
        }
        return result != -1;
    }

    public Cursor getAllOrders() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM orders ORDER BY id DESC",
                null
        );
    }

    public Cursor getOrderByCode(String orderCode) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM orders WHERE order_code=? LIMIT 1",
                new String[]{orderCode}
        );
    }

    public boolean updateOrderStatus(String orderCode, String status) {

        SQLiteDatabase db = this.getWritableDatabase();

        long now = System.currentTimeMillis();

        ContentValues cv = new ContentValues();

        cv.put("status", status);

        if (status.equals("Diproses")) {
            cv.put("process_at", now);
        }

        if (status.equals("Selesai")) {
            cv.put("done_at", now);
        }

        int result = db.update(
                "orders",
                cv,
                "order_code=?",
                new String[]{orderCode}
        );

        if (result > 0) {
            addOrderHistory(orderCode, status);
        }

        return result > 0;
    }

    public boolean addOrderHistory(String orderCode, String status) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("order_code", orderCode);
        cv.put("status", status);
        cv.put("created_at", System.currentTimeMillis());

        long result = db.insert(
                "order_history",
                null,
                cv
        );

        return result != -1;
    }

    public Cursor getOrderHistory(String orderCode) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM order_history WHERE order_code=? ORDER BY id ASC",
                new String[]{orderCode}
        );
    }

    public int countAllOrders() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM orders",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public int countOrdersByStatus(String status) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM orders WHERE status=?",
                new String[]{status}
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public int countTodayOrders() {
        return countOrdersSince(startOfToday(), null);
    }

    public int countTodayOrdersByStatus(String status) {
        return countOrdersSince(startOfToday(), status);
    }

    private int countOrdersSince(long startTime, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        if (status == null) {
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM orders WHERE created_at>=?",
                    new String[]{String.valueOf(startTime)}
            );
        } else {
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM orders WHERE created_at>=? AND status=?",
                    new String[]{String.valueOf(startTime), status}
            );
        }

        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    public Cursor getLatestOrders(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM orders ORDER BY created_at DESC LIMIT " + Math.max(1, limit),
                null
        );
    }

    public int countLowStock(int threshold) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM menu_stock WHERE stock<=?",
                new String[]{String.valueOf(threshold)}
        );
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    private long startOfToday() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // ================= SETTINGS =================

    public boolean saveSetting(String key, String value) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("setting_key", key);
        cv.put("setting_value", value);

        long result = db.insertWithOnConflict(
                "app_settings",
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        return result != -1;
    }

    public String getSetting(String key, String defaultValue) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT setting_value FROM app_settings WHERE setting_key=?",
                new String[]{key}
        );

        String value = defaultValue;

        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }

        cursor.close();

        return value;
    }

    public String getLanguage() {
        return getSetting("language", "Indonesia");
    }

    public boolean saveLanguage(String language) {
        return saveSetting("language", language);
    }

    // ================= HELPER =================

    public int parsePrice(String priceStr) {

        try {

            String clean =
                    priceStr.replaceAll("[^0-9]", "");

            return Integer.parseInt(clean);

        } catch (Exception e) {

            return 0;
        }
    }
}
