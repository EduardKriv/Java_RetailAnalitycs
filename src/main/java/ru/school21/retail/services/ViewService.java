package ru.school21.retail.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school21.retail.model.entity.View;
import ru.school21.retail.repositories.ViewRepository;

@Service
public class ViewService extends BaseService<View, Long> {

    @Autowired
    protected ViewService(ViewRepository repository) {
        super(repository);
    }

    public View getViewOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("View [%d] not found", id)));
    }
}
