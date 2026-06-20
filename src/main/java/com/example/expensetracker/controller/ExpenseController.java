package com.example.expensetracker.controller;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @ModelAttribute("categories")
    public Category[] categories() {
        return Category.values();
    }

    @GetMapping("/expenses")
    public String viewExpenses(@RequestParam(required = false) String keyword,
                               HttpSession session,
                               Model model) {
        String username = getLoggedInUsername(session);
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("expenses", expenseService.search(username, keyword));
        model.addAttribute("keyword", keyword);
        return "expenses";
    }

    @GetMapping("/expenses/add")
    public String addExpenseForm(HttpSession session, Model model) {
        if (getLoggedInUsername(session) == null) {
            return "redirect:/login";
        }
        Expense expense = new Expense();
        expense.setDate(LocalDate.now());
        model.addAttribute("expense", expense);
        model.addAttribute("pageTitle", "Add Expense");
        model.addAttribute("formAction", "/expenses/add");
        return "expense-form";
    }

    @PostMapping("/expenses/add")
    public String addExpense(@Valid @ModelAttribute Expense expense,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        String username = getLoggedInUsername(session);
        if (username == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Add Expense");
            model.addAttribute("formAction", "/expenses/add");
            return "expense-form";
        }
        expenseService.save(expense, username);
        redirectAttributes.addFlashAttribute("success", "Expense added successfully");
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/edit/{id}")
    public String editExpenseForm(@PathVariable Long id,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        String username = getLoggedInUsername(session);
        if (username == null) {
            return "redirect:/login";
        }
        return expenseService.findByIdForUser(id, username)
                .map(expense -> {
                    model.addAttribute("expense", expense);
                    model.addAttribute("pageTitle", "Edit Expense");
                    model.addAttribute("formAction", "/expenses/edit/" + id);
                    return "expense-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Expense not found");
                    return "redirect:/expenses";
                });
    }

    @PostMapping("/expenses/edit/{id}")
    public String editExpense(@PathVariable Long id,
                              @Valid @ModelAttribute Expense expense,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        String username = getLoggedInUsername(session);
        if (username == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Expense");
            model.addAttribute("formAction", "/expenses/edit/" + id);
            return "expense-form";
        }
        expenseService.update(id, expense, username);
        redirectAttributes.addFlashAttribute("success", "Expense updated successfully");
        return "redirect:/expenses";
    }

    @PostMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        String username = getLoggedInUsername(session);
        if (username == null) {
            return "redirect:/login";
        }
        expenseService.delete(id, username);
        redirectAttributes.addFlashAttribute("success", "Expense deleted successfully");
        return "redirect:/expenses";
    }

    private String getLoggedInUsername(HttpSession session) {
        return (String) session.getAttribute("loggedInUser");
    }
}
