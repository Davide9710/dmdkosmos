package com.example.test.controllers;

import com.example.test.models.entities.RemoveEntity;
import com.example.test.models.entities.TransactionEntity;
import com.example.test.scalardb.MyBank;
import com.scalar.db.exception.transaction.TransactionException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.test.models.services.RemoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor

public class RemoveController {

    @GetMapping("/remove")
    public String showList(Model model){
        model.addAttribute("removeList", new RemoveEntity());
        return "remove_form";
    }

    @PostMapping("/remove")
    public String showResult(
            @RequestParam("transactionId") String transactionId,
            @ModelAttribute RemoveEntity removeEntity,
            Authentication authentication,
            Model model
    ) throws TransactionException, IOException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String date = now.format(formatter);

        MyBank.cancel(transactionId, date);

        model.addAttribute("transactionID", transactionId);

        return "remove_result";
    }
}
