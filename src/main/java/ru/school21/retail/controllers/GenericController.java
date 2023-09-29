package ru.school21.retail.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school21.retail.mapper.GenericMapper;
import ru.school21.retail.model.dto.BaseDto;
import ru.school21.retail.model.entity.BaseEntity;
import ru.school21.retail.services.BaseService;
import ru.school21.retail.util.FileImportExport;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public abstract class GenericController<E extends BaseEntity, D extends BaseDto<T>, T extends Comparable<T>>  {
    protected final BaseService<E, T> genericService;
    private final GenericMapper<E, D> genericMapper;
    private final String modelAndPageName;
    private final Class<D> clazz;

    @GetMapping("all")
    public String findAll(@NotNull Model model) {
        model.addAttribute(modelAndPageName, genericMapper.toDtos(genericService.findAll()).stream()
                                                                      .sorted(Comparator.comparing(BaseDto::getId))
                                                                      .toList());
        return modelAndPageName;
    }

    @PostMapping("delete")
    public String delete(@RequestParam T id) {
        genericService.delete(id);
        return "redirect:all";
    }

    @PostMapping("add")
    public String add(@ModelAttribute E entity) {
        genericService.save(entity);
        return "redirect:all";
    }

    @GetMapping("updates/{id}")
    public String update(@PathVariable T id, @NotNull Model model) {
        D dto = genericMapper.toDto(genericService.findById(id).orElseThrow());
        model.addAttribute(modelAndPageName,dto);
        return modelAndPageName + "-update";
    }

    @PostMapping("update")
    public String update(@ModelAttribute D dto) {
        genericService.save(genericMapper.toEntity(dto));
        return "redirect:all";
    }

    @PostMapping("upload")
    public String upload(@RequestParam("file") MultipartFile file) {
            List<D> dtos = FileImportExport.importCsv(file, clazz);
            genericService.saveAll(genericMapper.toEntities(dtos));
        return "redirect:all";
    }

    @GetMapping("unload")
    public void unload(@NotNull HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"" + modelAndPageName + ".csv\"");
        servletResponse.setCharacterEncoding("UTF-8");
        List<D> beans = genericMapper.toDtos(genericService.findAll());
        FileImportExport.exportCsv(servletResponse.getWriter(), beans, clazz);
    }
}
