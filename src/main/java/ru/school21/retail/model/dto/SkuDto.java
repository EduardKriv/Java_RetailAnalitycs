package ru.school21.retail.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

@Data
@EqualsAndHashCode(callSuper = false)
public class SkuDto extends BaseDto<Long> {
    @CsvBindByName(column = "sku_id", required = true)
    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByName(column = "sku_name", required = true)
    @CsvBindByPosition(position = 1)
    private String skuName;

    @CsvBindByName(column = "group_id", required = true)
    @CsvBindByPosition(position = 2)
    private Long skuGroup;
}
