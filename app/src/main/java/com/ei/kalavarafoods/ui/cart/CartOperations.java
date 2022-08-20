package com.ei.kalavarafoods.ui.cart;

import android.database.Cursor;

import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.model.database.ProductEntity;

import java.util.List;

public class CartOperations {
    static public int subTotal(Cursor cartItems) {
        float Subtotal = 0;
//        Cursor cartItems = cartDB.getCartItems();
        while (cartItems.moveToNext()) {
            float itemTotal = Float.parseFloat(cartItems.getString(7)) * ((cartItems.getString(9).equals("noOffer")) ?
                    Float.parseFloat(cartItems.getString(5)) : Float.parseFloat(cartItems.getString(9)));
            Subtotal += itemTotal;
        }
        return (int) Subtotal;
    }

    public static float subTotalRoom(List<ProductEntity> productEntityList){
        float subTotal = 0;
        for (ProductEntity productEntity: productEntityList) {
            float itemTotal = Float.parseFloat(productEntity.getProductOrderQuantitiy()) *
                    ((productEntity.getProductOfferprice().equals("noOffer")) ?
                            Float.parseFloat(productEntity.getProductUnitprice()) :  Float.parseFloat(productEntity.getProductOfferprice()));
            subTotal += itemTotal;
        }
        return subTotal;
    }

    public static float subTotalRoomProduct(List<Product> productEntityList){
        float subTotal = 0;
        for (Product productEntity: productEntityList) {
            float itemTotal = Float.parseFloat(productEntity.getProductOrderQuantitiy()) *
                    ((productEntity.getProductOfferprice().equals("noOffer")) ?
                            Float.parseFloat(productEntity.getProductUnitprice()) :  Float.parseFloat(productEntity.getProductOfferprice()));
            subTotal += itemTotal;
        }
        return subTotal;
    }
}
