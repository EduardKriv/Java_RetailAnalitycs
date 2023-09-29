package ru.school21.retail.mapper;

import org.mapstruct.Mapper;
import ru.school21.retail.model.dto.CheckDto;
import ru.school21.retail.model.entity.Check;

@Mapper(uses = { TransactionMapper.class, SkuMapper.class })
public abstract class CheckMapper extends GenericMapper<Check, CheckDto> {
}
