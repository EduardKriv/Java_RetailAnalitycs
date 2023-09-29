package ru.school21.retail.util;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.school21.retail.model.dto.BaseDto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class FileImportExport {
    public static <T extends BaseDto> List<T> importCsv(MultipartFile file, Class<T> clazz) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CustomMappingStrategy<T> strategy = new CustomMappingStrategy<>();
            strategy.setType(clazz);

            return new CsvToBeanBuilder<T>(reader)
                    .withSkipLines(1)
                    .withMappingStrategy(strategy)
                    .withType(clazz)
                    .build()
                    .parse();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static <T extends BaseDto> void exportCsv(Writer writer, List<T> dto, Class<T> clazz) {
        try {
            CustomMappingStrategy<T> strategy = new CustomMappingStrategy<>();
            strategy.setType(clazz);

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(true)
                    .withMappingStrategy(strategy)
                    .build();

            beanToCsv.write(dto);

        }  catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void resultExportCsv(HttpServletResponse servletResponse, Map<String, List<Object>> result) {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"result.csv\"");
        servletResponse.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = servletResponse.getWriter()) {
            StringBuilder keys = new StringBuilder();
            result.keySet().forEach(x -> keys.append(x).append(","));
            keys.deleteCharAt(keys.length() - 1);

            List<StringBuilder> values = new ArrayList<>();
            int size = result.values().stream().findAny().get().size();
            for(int i = 0; i < size; ++i) {
                values.add(new StringBuilder());
                for (List<Object> value : result.values()) {
                    if (value.get(i) != null) {
                        values.get(i).append(value.get(i).toString()).append(",");
                    } else {
                        values.get(i).append(",");
                    }
                }
                values.get(i).deleteCharAt(values.get(i).length() - 1);
            }
            writer.println(keys);
            values.forEach(writer::println);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
