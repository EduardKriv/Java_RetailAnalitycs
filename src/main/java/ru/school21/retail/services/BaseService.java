package ru.school21.retail.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public abstract class BaseService<T, V> {

    protected final JpaRepository<T, V> repository;

    public Optional<T> findById(V id) {
        return repository.findById(id);
    }

    public void delete(V id) {
        repository.deleteById(id);
    }

    public T save(T object) {
        return repository.saveAndFlush(object);
    }

    public T update(T object) {
        return repository.saveAndFlush(object);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public List<T> saveAll(List<T> objects) {
        return repository.saveAllAndFlush(objects);
    }
}
