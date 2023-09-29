package ru.school21.retail.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.school21.retail.services.ExecuteQueryService;
import ru.school21.retail.services.ViewService;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

    private final ViewService viewService;
    private final ExecuteQueryService executeQueryService;

    @GetMapping("/")
    public String index() {
        return "head";
    }

    @GetMapping("/data")
    public String dataMain() {
        return "data";
    }

    @GetMapping("/suggestions")
    public String suggestionsMain() throws SQLException {
        for (long i = 1L; i <= 4L; ++i) {
            executeQueryService.executeVoidQuery(viewService.getViewOrThrow(i).getQuery());
        }
        return "suggestions";
    }

    @GetMapping("/sqlQuery")
    public String queryMain() {
        return "sqlQuery";
    }

    @GetMapping("/about")
    public String aboutMain() {
        return "about";
    }
}
