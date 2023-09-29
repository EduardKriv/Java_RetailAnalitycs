package ru.school21.retail.controllers;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.school21.retail.services.ExecuteQueryService;
import ru.school21.retail.util.FileImportExport;

import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static ru.school21.retail.dao.ManagerDAO.CALL;

@Controller
@RequiredArgsConstructor
public class SqlController {

    private final ExecuteQueryService executeQueryService;
    private Map<String, List<Object>> result;

    @PostMapping("/sqlQuery")
    public String executeQuery(String query, @NotNull Model model) throws SQLException {
        result = executeQueryService.executeQuery(query);
        model.addAttribute("result", result);
        return "sqlQuery";
    }

    @PostMapping("/func")
    public String executeCall(int id, @NotNull Model model,
                              @RequestParam(value = "arg", required = false) Object... args) throws SQLException {
        result = executeQueryService.executeFunction(CALL.get(id), args);
        model.addAttribute("result", result);
        return "results";
    }

    @PostMapping("/func/3")
    public String executeF3(int id, @NotNull Model model,
                            @RequestParam(value = "arg", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arg) throws SQLException {
        return executeCall(id, model, arg);
    }

    @PostMapping("/proc")
    public String executeProc(int id, @NotNull Model model,
                              @RequestParam(value = "arg", required = false) Object... args) throws SQLException {
        result = executeQueryService.executeProcedure(CALL.get(id), args);
        model.addAttribute("result", result);
        return "results";
    }

    @GetMapping("view/{id}")
    public String showView(@PathVariable int id, @NotNull Model model) throws SQLException {
        result = executeQueryService.executeQuery(CALL.get(id));
        model.addAttribute("result", result);
        return "results";
    }

    @PostMapping("viewGroups")
    public String showViewGroups(int method, int days, int transactions, @NotNull Model model) throws SQLException {
        switch (method) {
            case 1:
                executeQueryService.executeVoidFunction(CALL.get(8), "default", 0);
                break;
            case 2:
                executeQueryService.executeVoidFunction(CALL.get(8), "days", days);
                break;
            case 3:
                executeQueryService.executeVoidFunction(CALL.get(8), "transactions", transactions);
                break;
        }
        result = executeQueryService.executeQuery(CALL.get(4));
        model.addAttribute("result", result);
        return "results";
    }

    @RequestMapping(path = "/unloadResult")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.resultExportCsv(servletResponse, result);
    }
}
