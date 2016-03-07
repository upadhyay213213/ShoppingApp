package activities.shoppingapp.com.shoppingapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import activities.shoppingapp.com.shoppingapp.models.HandBag;

/**
 * Created by prerana_katyarmal on 2/18/2016.
 */
public class HandbagUtil {

    /**
     *
     * @param handBag
     * @param ctx
     */
    public static void addProductToDataBase(HandBag handBag, Context ctx) {
        String query;
        DataBaseHelper.init(ctx);
        if (!isProductAlreadyInDB(handBag, ctx)) {
            query = "INSERT INTO ProductInfo (Product_ID,Image_URL,ProductName)VALUES "
                    + "(?,?,?)";
            SQLiteDatabase db = DataBaseHelper.getSqliteDatabase();
            SQLiteStatement statement = db.compileStatement(query);
            statement = bindValuesToStatement(handBag, statement);
            statement.execute();
            statement.close();
        }
    }

    /**
     *
     * @param productId
     * @param ctx
     * @return
     */
    private static boolean isProductAlreadyInDB(HandBag productId, Context ctx) {
        return isProductInDB(productId.getId(), ctx);
    }

    /**
     *
     * @param productId
     * @param ctx
     * @return
     */
    public static boolean isProductInDB(int productId, Context ctx) {
        String query = "Select Product_ID from ProductInfo where Product_ID=" + productId;
        SQLiteDatabase db = DataBaseHelper.getSqliteDatabase();
        Cursor cursor = DataBaseHelper.executeSelectQuery(db, query, null);
        if (cursor.getCount() <= 0) {
            return false;
        }
        cursor.close();
        return true;
    }


    /**
     *
     * @param productInfo
     * @param statement
     * @return
     */
    private static SQLiteStatement bindValuesToStatement(HandBag productInfo, SQLiteStatement statement) {
        try {
            statement.bindDouble(1, productInfo.getId());
            statement.bindDouble(2, productInfo.getDrawableImage());
            statement.bindString(3, productInfo.getBagName());
        } catch (Exception e) {

        }
        return statement;
    }


    /**
     *
     * @param ctx
     * @return
     */
    public static ArrayList<HandBag> getProducts(Context ctx) {
        DataBaseHelper.init(ctx);
        ArrayList<HandBag> productList = new ArrayList<>();
        String query = "Select * from ProductInfo";
        SQLiteDatabase db = DataBaseHelper.getSqliteDatabase();
        Cursor cur = DataBaseHelper.executeSelectQuery(db, query, null);
        while (cur.moveToNext()) {
            HandBag product = new HandBag();
            product.setId(cur.getInt(cur.getColumnIndexOrThrow("Product_ID")));
            product.setDrawableImage(cur.getInt(cur.getColumnIndexOrThrow("Image_URL")));
            product.setBagName(cur.getString(cur.getColumnIndexOrThrow("ProductName")));
            productList.add(product);
        }
        cur.close();
        return productList;
    }


    /**
     *
     * @param productID
     * @param ctx
     */
    public static void deleteProductFromDatabase(int productID, Context ctx) {
        SQLiteDatabase db = DataBaseHelper.getSqliteDatabase();
        String query = "Delete from ProductInfo WHERE Product_ID= " + productID;
        db.execSQL(query);
    }
}
