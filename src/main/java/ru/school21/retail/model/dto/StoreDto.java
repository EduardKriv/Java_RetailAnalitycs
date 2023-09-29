package ru.school21.retail.model.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class StoreDto extends BaseDto<Long> {
    @CsvBindByName(column = "store_id", required = true)
    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByName(column = "transaction_store_id", required = true)
    @CsvBindByPosition(position = 1)
    private Long transactionStore;

    @CsvBindByName(column = "sku_id", required = true)
    @CsvBindByPosition(position = 2)
    private String sku;

    @CsvBindByName(column = "sku_purchase_price", required = true)
    @CsvBindByPosition(position = 3)
    private Double skuPurchasePrice;

    @CsvBindByName(column = "sku_retail_price", required = true)
    @CsvBindByPosition(position = 4)
    private Double skuRetailPrice;
}
