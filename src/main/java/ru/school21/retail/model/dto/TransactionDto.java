package ru.school21.retail.model.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransactionDto extends BaseDto<Long> {
    @CsvBindByName(column = "transaction_id", required = true)
    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByName(column = "customer_card_id", required = true)
    @CsvBindByPosition(position = 1)
    private Long customerCard;

    @CsvBindByName(column = "transaction_summ", required = true)
    @CsvBindByPosition(position = 2)
    private Double transactionSumm;

    @CsvBindByName(column = "transaction_datetime", required = true)
    @CsvBindByPosition(position = 3)
    @CsvDate(value = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDateTime;

    @CsvBindByName(column = "transaction_store_id", required = true)
    @CsvBindByPosition(position = 4)
    private Long transactionStore;
}
