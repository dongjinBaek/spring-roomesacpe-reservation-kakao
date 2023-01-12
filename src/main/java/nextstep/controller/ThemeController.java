package nextstep.controller;

import nextstep.dto.request.CreateThemeRequest;
import nextstep.dto.response.ThemeResponse;
import nextstep.service.ThemeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/themes")
@RestController
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping("")
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody CreateThemeRequest createThemeRequest) {
        ThemeResponse createdTheme = themeService.createTheme(createThemeRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/themes/" + createdTheme.getId())
                .body(createdTheme);
    }
}