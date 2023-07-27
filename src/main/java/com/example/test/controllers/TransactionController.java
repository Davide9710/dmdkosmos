/**
 *  By Tatsuya Mori, Kazuki Ozeki
 */

package com.example.test.controllers;

import com.example.test.models.entities.TransactionEntity;
import com.example.test.scalardb.MyBank;
import com.scalar.db.exception.transaction.TransactionException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.test.models.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequiredArgsConstructor

public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/transaction")
    public String showForm(Model model){
//        model.addAttribute("transactionList", transactionService.findAll());
        model.addAttribute("transactionList", new TransactionEntity());
        return "transaction_form";
    }

    @PostMapping("/transaction")
    public String showResult(
            @RequestParam("toAccountId") String toId,
            @RequestParam("amount") int amount,
            @ModelAttribute TransactionEntity transactionEntity,
            Authentication authentication,
            Model model
    ) throws TransactionException, IOException {
        String fromId = authentication.getName();

        String fromTable = "";
        if (fromId.charAt(0) == 'm') {
            fromTable = "mysql";
        } else {
            fromTable = "postgres";
        }

        String toTable = "";
        if (toId.charAt(0) == 'm') {
            toTable = "mysql";
        } else {
            toTable = "postgres";
        }
        String toName = MyBank.getAccountName(toTable, toId);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String date = now.format(formatter);

        String transactionId = MyBank.transfer(fromTable, fromId, toTable, toId, amount, date);

        model.addAttribute("transactionId", transactionId);
        model.addAttribute("toId", toId);
        model.addAttribute("toName", toName);
        model.addAttribute("amount", amount);
        model.addAttribute("date", date);

        return "transaction_result";
    }
}
