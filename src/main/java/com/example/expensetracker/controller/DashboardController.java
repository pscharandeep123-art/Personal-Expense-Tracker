package com.example.expensetracker.controller;

import com.example.expensetracker.service.ExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ExpenseService expenseService;

    public DashboardController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("totalExpenses", expenseService.calculateTotal(username));
        model.addAttribute("recentExpenses", expenseService.recentExpenses(username));
        model.addAttribute("categorySummary", expenseService.categorySummary(username));
        model.addAttribute("fullName", session.getAttribute("fullName"));
        return "dashboard";
    }
}
