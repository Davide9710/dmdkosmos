package com.example.test.controllers;

import com.example.test.dto.ReservedBookDTO;
import com.example.test.scalardbv2.MyLibrary;
import com.scalar.db.exception.transaction.TransactionException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping()
public class IndexController {

    @GetMapping()
    public String index(Authentication authentication, Model model) throws IOException, TransactionException {

        String accountName = authentication.getName();
        String accountId = MyLibrary.getAccountId(accountName);
        List<ReservedBookDTO> reservedBookDTOS = MyLibrary.getReservedBooks(accountId);

        model.addAttribute("accountId", accountId);
        model.addAttribute("accountName", accountName);
        model.addAttribute("bookedBooks", reservedBookDTOS);

        return "index";
    }
}