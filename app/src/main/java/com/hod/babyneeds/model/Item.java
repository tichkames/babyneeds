package com.hod.babyneeds.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Item {
    private int id;
    private String itemName;
    private String itemColor;
    private int itemQuantity;
    private int itemSize;
    private String dateItemAdded;
}
