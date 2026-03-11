package com.salon.controller;

import com.salon.dto.BotRequest;
import com.salon.dto.BotResponse;
import com.salon.service.BotService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bot")
public class BotController {

    private final BotService botService;

    public BotController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping("/chat")
    public ResponseEntity<BotResponse> chat(
            @Valid @RequestBody BotRequest request) {

        return ResponseEntity.ok(
                botService.reply(request.getMessage())
        );
    }
}
