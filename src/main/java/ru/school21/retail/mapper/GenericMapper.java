package ru.school21.retail.mapper;

import ru.school21.retail.model.dto.BaseDto;
import ru.school21.retail.model.entity.BaseEntity;

import java.util.List;

public abstract class GenericMapper<E extends BaseEntity, D extends BaseDto> {
    public abstract D toDto(E entity);
    public abstract E toEntity(D dto);
    public abstract List<D> toDtos(List<E> entity);
    public abstract List<E> toEntities(List<D> dto);
}
