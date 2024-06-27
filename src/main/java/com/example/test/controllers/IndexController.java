package com.example.test.controllers;

import com.example.test.scalardb.MyBank;
import com.scalar.db.exception.transaction.TransactionException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping()
public class IndexController {
    @GetMapping()
    public String index(Authentication authentication,  Model model) throws IOException, TransactionException {
        String accountId = authentication.getName();
        String table = "";
        if (accountId.charAt(0) == 'm') {
            table = "mysql";
        } else {
            table = "postgres";
        }
        String accountName = MyBank.getAccountName(table, accountId);
        int balance = MyBank.getBalance(table, accountId);

        model.addAttribute("accountId", accountId);
        model.addAttribute("accountName", accountName);
        model.addAttribute("balance", balance);

        return "index";
    }
}